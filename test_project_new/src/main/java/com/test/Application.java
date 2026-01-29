package com.test;

import com.itu.framework.FrameworkRunner;
import com.itu.framework.annotations.FrameworkApplication;

@FrameworkApplication(port = 8080)
public class Application {

    public static void main(String[] args) {
        FrameworkRunner.run(Application.class, args);
    }
}
