package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GetMapping;
import com.itu.framework.annotations.PostMapping;
import com.itu.framework.annotations.RequestParam;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @GetMapping("/greeting")
    public ModelView sayHello() {
        ModelView mv = new ModelView("hello");
        mv.addObject("name", "Faniry");
        return mv;
    }

    @GetMapping("/{name}")
    public String sayHelloToName(String name) {
        return "Hello " + name + "!";
    }

    @GetMapping("/bye")
    public String sayGoodbye() {
        return "Goodbye!";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query) {
        return "Searching for: " + query;
    }

    @GetMapping("/form")
    public ModelView displayForm() {
        return new ModelView("form");
    }

    @PostMapping("/save-user")
    public ModelView saveUser(String firstName, String lastName) {
        ModelView mv = new ModelView("result");
        mv.addObject("firstName", firstName);
        mv.addObject("lastName", lastName);
        return mv;
    }
}