package com.itu.framework.helpers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ComponentScan {

    public static Map<String, java.util.List<Mapping>> scanControllers(String packageName) throws Exception {
        Map<String, java.util.List<Mapping>> urlMappings = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Package not found: " + packageName);
        }

        File directory = new File(resource.toURI());
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Controller.class)) {
                    Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
                    String baseUrl = controllerAnnotation.value();

                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(UrlMapping.class)) {
                            UrlMapping urlAnnotation = method.getAnnotation(UrlMapping.class);
                            String methodUrl = urlAnnotation.value();
                            String fullUrl = (baseUrl + methodUrl).replaceAll("/+", "/");

                            String httpMethod = "GET"; // Default
                            if (method.isAnnotationPresent(com.itu.framework.annotations.POST.class)) {
                                httpMethod = "POST";
                            } else if (method.isAnnotationPresent(com.itu.framework.annotations.GET.class)) {
                                httpMethod = "GET";
                            }

                            if (!urlMappings.containsKey(fullUrl)) {
                                urlMappings.put(fullUrl, new java.util.ArrayList<>());
                            }

                            // Check for duplicate method mapping for same URL
                            for (Mapping m : urlMappings.get(fullUrl)) {
                                if (m.getHttpMethod().equals(httpMethod)) {
                                    throw new IllegalStateException(
                                            "Duplicate URL and Method found: " + fullUrl + " " + httpMethod);
                                }
                            }

                            urlMappings.get(fullUrl).add(new Mapping(clazz.getName(), method.getName(), httpMethod));
                        }
                    }
                }
            }
        }
        return urlMappings;
    }
}