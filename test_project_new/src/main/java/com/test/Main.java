package com.test;

import java.lang.reflect.Method;
import java.util.List;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.URL;
import com.itu.framework.helpers.ComponentScan;

public class Main {
    public static void main(String[] args) {
        /*
        System.out.println("Scanning for methods annotated with @URL...");
        Class<URLTest> clazz = URLTest.class;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(URL.class)) {
                URL urlAnnotation = method.getAnnotation(URL.class);
                System.out.println("Found annotated method: " + method.getName() + " with URL: " + urlAnnotation.value());
            }
        }
        */
        Class<Controller> controllerClass = Controller.class;
        List<Class<?>> annotatedControllerClasses = ComponentScan.getAnnotatedClassesByAnnotation(controllerClass);
        for(Class clazz : annotatedControllerClasses)
        {
            System.out.println(clazz.toString());
        }
    }
}