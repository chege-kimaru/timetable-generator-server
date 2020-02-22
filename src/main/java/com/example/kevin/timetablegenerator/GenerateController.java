package com.example.kevin.timetablegenerator;

import com.example.kevin.timetablegenerator.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GenerateController {
    @PostMapping(value = "/", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HttpEntity<List<Allocation>> generate(@RequestBody String body) {
        GeneratorService generator = new GeneratorService(new JSONParser(body));
        return new HttpEntity<>(generator.allocations);
    }
}
