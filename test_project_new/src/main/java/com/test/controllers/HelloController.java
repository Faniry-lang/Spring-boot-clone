package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.annotations.RequestParam;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @UrlMapping("/greeting")
    public ModelView sayHello() {
        ModelView mv = new ModelView("hello");
        mv.addObject("name", "Faniry");
        return mv;
    }

    @UrlMapping("/{name}")
    public String sayHelloToName(String name) {
        return "Hello " + name + "!";
    }

    @UrlMapping("/bye")
    public String sayGoodbye() {
        return "Goodbye!";
    }

    @UrlMapping("/search")
    public String search(@RequestParam("q") String query) {
        return "Searching for: " + query;
    }
}