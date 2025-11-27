package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @UrlMapping("/greeting")
    public ModelView sayHello() {
        return new ModelView("hello");
    }

    @UrlMapping("/bye")
    public String sayGoodbye() {
        return "Goodbye!";
    }

    @UrlMapping("/{name}")
    public String sayHelloToName() {
        return "Hello you!";
    }
}