package com.itu.framework.helpers;

public class Mapping {
    private String className;
    private String methodName;
    private String httpMethod;

    public Mapping(String className, String methodName, String httpMethod) {
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
