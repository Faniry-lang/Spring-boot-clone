package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.annotations.RequestParam;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @UrlMapping("/greeting")
    public ModelView sayHello() {
        return new ModelView("hello");
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