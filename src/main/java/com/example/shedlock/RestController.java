package com.example.shedlock;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/test")
    public String test() {
        return "done";
    }

}
