package com.test;

import java.lang.reflect.Method;

import com.itu.framework.annotations.URL;

public class Main {
    public static void main(String[] args) {
        System.out.println("Scanning for methods annotated with @URL...");
        Class<URLTest> clazz = URLTest.class;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(URL.class)) {
                URL urlAnnotation = method.getAnnotation(URL.class);
                System.out.println("Found annotated method: " + method.getName() + " with URL: " + urlAnnotation.value());
            }
        }
    }
}