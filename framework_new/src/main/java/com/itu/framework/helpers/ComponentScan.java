package com.itu.framework.helpers;

import java.util.ArrayList;
import java.util.List;

public class ComponentScan {
    public static List<Class<?>> getAnnotatedClassesByAnnotation(Class annotationClass)
    {
        List<Class<?>> projectClasses = ClassPathScanning.getProjectClasses();
        List<Class<?>> annotatedclasses = new ArrayList<>();
        for(Class<?> clazz : projectClasses)
        {
            if(clazz.isAnnotationPresent(annotationClass) && clazz.getAnnotation(annotationClass) != null)
            {
                annotatedclasses.add(clazz);
            }
        }
        return annotatedclasses;
    }
}
