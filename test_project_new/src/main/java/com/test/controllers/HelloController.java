package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;

@Controller("/hello")
public class HelloController {

    @UrlMapping("/greeting")
    public String sayHello() {
        return "Hello from the framework!";
    }

    @UrlMapping("/bye")
    public String sayGoodbye() {
        return "Goodbye!";
    }
}