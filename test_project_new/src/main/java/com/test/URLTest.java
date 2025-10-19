package com.test;

import com.itu.framework.annotations.URL;

public class URLTest {

    @URL("/hello")
    public void hello() {
        System.out.println("This is the hello method.");
    }

    @URL("/world")
    public void world() {
        System.out.println("This is the world method.");
    }

    public void noAnnotation() {
        System.out.println("This method has no annotation.");
    }
}