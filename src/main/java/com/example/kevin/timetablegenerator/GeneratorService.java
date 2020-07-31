package com.example.kevin.timetablegenerator;

import com.example.kevin.timetablegenerator.models.*;

import java.util.*;

public class GeneratorService {
    List<SubjectGroup> subjectGroups = new ArrayList<>();
    List<Subject> subjects = new ArrayList<>();
    List<Stream> streams = new ArrayList<>();
    List<Teacher> teachers = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    List<Period> periods = new ArrayList<>();
    List<Allocation> allocations = new ArrayList<>();

    /**
     * Number of lessons per subject
     */
    int ALLOCATION_LESSONS = 5;

    final int EPOCH = 30;
    int MAX_TEACHER_LESSONS = 6;
    boolean ALL_LABS_ALLOCATED = true;
    boolean TEACHER_2_CONSECUTIVE_LESSONS_WITHOUT_BREAK_RULE = true;
    boolean TEACHER_2_CONSECUTIVE_SCIENCE_DOUBLES_RULE = true;
    boolean SUBJECT_REPEAT_ON_SAME_DAY_RULE = true;
    boolean STUDENT_MAX_2_SCIENCE_DOUBLE_DAY_RULE = true;
    boolean SUBJECT_SAME_GROUP_CONSECUTIVE_RULE = true;

    public GeneratorService(JSONParser parser) {
        subjectGroups = parser.getSubjectGroups();
        subjects = parser.getSubjects();
        streams = parser.getStreams();
        teachers = parser.getTeachers();
        rooms = parser.getRooms();
        periods = parser.getPeriods();
        allocations = parser.getAllocations();

        run();

        //test
        try {
            if (streams.get(0) != null) {
                printTimeTables("Class 1", "class", streams.get(0).id);
            }
            if (streams.get(1) != null) {
                printTimeTables("Class 2", "class", streams.get(1).id);
            }
//        printTimeTables("liz", "teacher", 1);
//        printTimeTables("kevin", "teacher", 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        Random random = new Random(System.currentTimeMillis());
        ListIterator<Allocation> allocationsIterator = allocations.listIterator();
        //Randomize starting position of allocations
        if (allocations.size() > 4) {
            int index = random.nextInt(allocations.size() - 2);
            for (int i = 0; i < index; i++) {
                allocationsIterator.next();
            }
        }
        //repeat assigning a predefined number of times
        for (int epoch = 0; epoch < EPOCH; epoch++) {
            // relax rules for the next 10 rounds
            if (epoch == 10) {
                TEACHER_2_CONSECUTIVE_LESSONS_WITHOUT_BREAK_RULE = false;
                TEACHER_2_CONSECUTIVE_SCIENCE_DOUBLES_RULE = false;
                STUDENT_MAX_2_SCIENCE_DOUBLE_DAY_RULE = false;
                SUBJECT_SAME_GROUP_CONSECUTIVE_RULE = false;
            }
            if (epoch == 20) {
                MAX_TEACHER_LESSONS = 8;
                SUBJECT_REPEAT_ON_SAME_DAY_RULE = false;
            }

            //loop over the days
            for (int day = 1; day <= 5; day++) {
                //Loop over the periods
                ListIterator<Period> periodsIterator = periods.listIterator();
                while (periodsIterator.hasNext()) {
                    Period period = periodsIterator.next();
                    if (!allocationsIterator.hasNext()) allocationsIterator = allocations.listIterator();

                    Allocation allocation = allocationsIterator.next();
                    allocation.periods.computeIfAbsent(day, k -> new ArrayList<>());

                    //Assign rooms first

                    if (!ALL_LABS_ALLOCATED) {
                        Allocation allocationRoom = nextRoom();
                        //loop through this subject's rooms, use the one that is free
                        //Has to be a double lesson
                        if (allocationRoom == null) ALL_LABS_ALLOCATED = true;
                        else {
                            for (Room room : rooms) {
                                allocationRoom.periods.computeIfAbsent(day, k -> new ArrayList<>());
//                                if (room.subject.name.equals(allocationRoom.subject.name)) {
                                if (room.subject_id == allocationRoom.subject_id) {
                                    Period prevPeriod = period;
                                    if (isValid(allocationRoom, day, period.id, true, room)) {
                                        allocationRoom.periods.get(day).add(period);

                                        //Double functionality
                                        if (periodsIterator.hasNext()) {
                                            period = periodsIterator.next();
                                            if (!period.toBreak && isValid(allocationRoom, day, period.id, true, room)) {
                                                //Double successful
                                                //Set room
                                                allocationRoom.periods.get(day).add(period);

                                                allocationRoom.room = new RoomAllocation(room, day, prevPeriod, period);
                                            } else {
                                                //Go back to the previous period as this one has not been used for the double
                                                periodsIterator.previous();
                                                //Clear periods
                                                allocationRoom.periods.get(day).clear();
                                            }
                                        } else {
                                            //double cannot apply as it is end of day
                                            //Clear periods
                                            allocationRoom.periods.get(day).clear();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (isValid(allocation, day, period.id, false, null)) {
                            allocation.periods.get(day).add(period);

                            //Double functionality
                            if (periodsIterator.hasNext()) {
                                period = periodsIterator.next();
                                if (!period.toBreak && isValid(allocation, day, period.id, false, null)) {
                                    //Double successfull
                                    allocation.periods.get(day).add(period);
                                } else {
                                    //Go back to the previous period as this one has not been used for the double
                                    periodsIterator.previous();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Allocation nextRoom() {
        for (Allocation allocation : allocations) {
            if (allocation.subject.hasLab) {
                if (allocation.room == null || allocation.periods.size() < 2) return allocation;
            }
        }
        return null;
    }

    private Period getPeriodById(int id) {
        for (Period period : periods) {
            if (period.id == id) return period;
        }
        return null;
    }

    private boolean isValid(Allocation a, int day, int periodId, boolean validateRoom, Room room) {
        //Is Room valid
        if (validateRoom) {
            //Check if this room has been allocated at this time today
            for (Allocation allocation : allocations) {
                if (allocation.room != null &&
                        allocation.room.room.id == room.id &&
                        allocation.room.day == day &&
                        (allocation.room.period1.id == periodId || allocation.room.period2.id == periodId)) {
                    return false;
                }
            }
        }


        //Class does not happen twice in a day not unless it is a double
        if (SUBJECT_REPEAT_ON_SAME_DAY_RULE) {
            for (Period period : a.periods.get(day)) {
                if (period.id != periodId - 1 || period.toBreak) return false;
            }
        }

        //Teacher should have a max of 4 lessons per day
        //Teacher cannot have 3 sessions consecutively without break
        //Teacher cannot have 2 science doubles consecutively
        //Find all allocated sciences for this teacher today
        //period id, allocation id that day
        Map<Integer, Integer> teacherPeriods = new TreeMap<>();
        Map<Integer, Integer> teacherPeriodSciences = new TreeMap<>();
        for (Allocation allocation : allocations) {
            allocation.periods.computeIfAbsent(day, k -> new ArrayList<>());
            if (allocation.teacher_id == a.teacher_id) {
                for (Period period : allocation.periods.get(day)) {
                    teacherPeriods.put(period.id, allocation.id);
                }
            }


            if (allocation.subject.group.name.equals("science") && allocation.teacher_id == a.teacher_id) {
                for (Period period : allocation.periods.get(day)) {
                    teacherPeriodSciences.put(period.id, allocation.id);
                }
            }
        }
        //Teacher has a max lessons per day
        if (teacherPeriods.size() >= MAX_TEACHER_LESSONS) return false;

        //Teacher cannot have more than 2 sessions consecutively without break
        if (TEACHER_2_CONSECUTIVE_LESSONS_WITHOUT_BREAK_RULE) {
            int l = 1;
            for (int period : teacherPeriods.keySet()) {
                if (l >= 2) {
                    //check if the -2 period and -1 period are consecutive without break
                    //check if the -1 period and current allocation are going to be consecutive without a break
                    try {
                        int neg2 = teacherPeriods.get(period - 2);
                        int neg1 = teacherPeriods.get(period - 1);
                        if (!getPeriodById(neg2).toBreak && !getPeriodById(neg1).toBreak && periodId == period - 1) {
                            return false;
                        }
                    } catch (NullPointerException e) {

                    }
                }
                l++;
            }
        }

        //Teacher cannot have 2 science doubles consecutively
        if (TEACHER_2_CONSECUTIVE_SCIENCE_DOUBLES_RULE) {
            int i = 1;
            for (int period : teacherPeriodSciences.keySet()) {
                if (i >= 3) {
                    //check if the -3 period and -2 period are same allocation (that is double)
                    //check if the -1 period are same allocation with current allocation (to create double)
                    try {
                        int neg3 = teacherPeriodSciences.get(period - 3);
                        int neg2 = teacherPeriodSciences.get(period - 2);
                        if (neg3 == neg2 && !getPeriodById(neg3).toBreak && !getPeriodById(neg2).toBreak) {
                            int neg1 = teacherPeriodSciences.get(period - 1);
                            if (neg1 == a.id && !getPeriodById(neg1).toBreak) return false;
                        }
                    } catch (NullPointerException e) {

                    }
                }
                i++;
            }
        }


        for (Allocation allocation : allocations) {
            allocation.periods.computeIfAbsent(day, k -> new ArrayList<>());

            if (allocation.stream_id == a.stream_id) {
                //Students can only have a max of 2 sessions of science doubles a day
                if (STUDENT_MAX_2_SCIENCE_DOUBLE_DAY_RULE) {
                    int scienceDbsToday = 0;
                    if (allocation.subject.group.name.equals("science")) {
                        Period prevPeriod = null;
                        for (Period period : allocation.periods.get(day)) {
                            if (prevPeriod != null && !prevPeriod.toBreak && period.id - 1 == prevPeriod.id) {
                                scienceDbsToday++;
                                prevPeriod = null;
                            } else {
                                prevPeriod = period;
                            }
                        }
                    }
                    if (scienceDbsToday > 2) return false;
                }

                //subjects in the same group cannot be consecutive unless double
                if (SUBJECT_SAME_GROUP_CONSECUTIVE_RULE) {
                    for (Period period : allocation.periods.get(day)) {
                        boolean isConsecutive = period.id == periodId - 1 && !period.toBreak;
                        boolean isDouble = allocation.subject_id == a.subject_id;
                        boolean isSameGroup = allocation.subject.group.name.equals(a.subject.group.name);
                        if (isConsecutive && !isDouble && isSameGroup) return false;
                    }
                }
            }

            //Is teacher free
            //Is class free
            if (allocation.teacher_id == a.teacher_id ||
                    allocation.stream_id == a.stream_id) {
                for (Period period : allocation.periods.get(day)) {
                    if (period.id == periodId) return false;
                }
            }
        }
        return true;
    }

    //**********PRINT****//

    private String outputFormat() {
        StringBuilder format = new StringBuilder("| %-8s ");
        for (int j = 1; j < periods.size(); j++) {
            format.append("| %-20s ");
        }
        return format + " |%n";
    }

    private void printTimeTables(String name, String type, int id) {
        System.out.println("****" + name + "****");
        Object[][] tt = new Object[6][periods.size() + 1];
        tt[0][0] = "Day\\Time";
        for (int day = 1; day < tt.length; day++) {
            tt[day][0] = day;
        }
        for (int i = 1; i < periods.size(); i++) {
            tt[0][i] = periods.get(i).from + " - " + periods.get(i).to;
        }

        for (Allocation allocation : allocations) {
            boolean condition = false;
            if (type.equals("teacher")) {
                condition = allocation.teacher_id == id;
            } else if (type.equals("class")) {
                condition = allocation.stream_id == id;
            }
            if (condition) {
                for (int i = 1; i <= 5; i++) {
                    for (Period period : allocation.periods.get(i)) {
                        tt[i][period.id] = allocation;
                    }
                }
            }
        }

        for (int i = 0; i < tt.length; i++) {
            ArrayList<Object> args = new ArrayList<>();
            for (int j = 0; j < tt[0].length; j++) {
                if (i == 0 || j == 0)
                    args.add(tt[i][j]);
                else {
                    Allocation allocation = (Allocation) tt[i][j];
                    if (allocation != null) {
                        String room = "";
                        if (allocation.room != null &&
                                allocation.room.day == i &&
                                (allocation.room.period1.id == j || allocation.room.period2.id == j)) {
                            room = "(" + allocation.room.room.name + ")";
                        }
                        args.add(allocation.subject.name + "(" + allocation.teacher.name + ")" + room);
                    } else
                        args.add("----------");
                }
            }
            System.out.printf(outputFormat(), args.toArray());
        }
    }
}
