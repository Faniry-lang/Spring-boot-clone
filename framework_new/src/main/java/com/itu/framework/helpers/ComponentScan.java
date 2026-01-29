package com.itu.framework.helpers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GetMapping;
import com.itu.framework.annotations.PostMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ComponentScan {

    public static Map<String, java.util.List<Mapping>> scanControllers(String packageName) throws Exception {
        Map<String, java.util.List<Mapping>> urlMappings = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            System.out.println("SCANNING RESOURCE: " + resource);
            System.out.println("PROTOCOL: " + resource.getProtocol());
            if (resource.getProtocol().equals("file")) {
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    scanDir(directory, packageName, urlMappings);
                }
            } else if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                System.out.println("JAR PATH: " + jarPath);
                try (JarFile jar = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && name.endsWith(".class")) {
                             System.out.println("FOUND CLASS IN JAR: " + name);
                            String className = name.substring(0, name.length() - 6).replace('/', '.');
                            // Skip inner classes
                            if (!className.contains("$")) {
                                processClass(className, urlMappings);
                            }
                        }
                    }
                }
            }
        }
        return urlMappings;
    }

    private static void scanDir(File directory, String packageName, Map<String, java.util.List<Mapping>> urlMappings) throws Exception {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDir(file, packageName + "." + file.getName(), urlMappings);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                processClass(className, urlMappings);
            }
        }
    }

    private static void processClass(String className, Map<String, java.util.List<Mapping>> urlMappings) throws Exception {
        Class<?> clazz = Class.forName(className);

        if (clazz.isAnnotationPresent(Controller.class)) {
            Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
            String baseUrl = controllerAnnotation.value();

            for (Method method : clazz.getDeclaredMethods()) {
                String methodUrl = null;
                String httpMethod = null;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    methodUrl = method.getAnnotation(GetMapping.class).value();
                    httpMethod = "GET";
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    methodUrl = method.getAnnotation(PostMapping.class).value();
                    httpMethod = "POST";
                }

                if (methodUrl != null) {
                    String fullUrl = (baseUrl + methodUrl).replaceAll("/+", "/");

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