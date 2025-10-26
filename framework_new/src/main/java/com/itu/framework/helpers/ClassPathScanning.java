package com.itu.framework.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathScanning {
    private static Path getTargetClassesPath() {
        try {
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            
            return projectRoot.resolve("target").resolve("classes");

        } catch (Exception e) {
            System.err.println("Erreur: Impossible de déterminer le chemin 'target/classes' du projet test. Assurez-vous d'exécuter la commande à la racine du projet test.");
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> pathToClass(Path rootPath, Path classPath) {
        try {
            String relativePath = rootPath.relativize(classPath).toString();

            String className = relativePath.substring(0, relativePath.length() - ".class".length());
            
            className = className.replace(java.io.File.separator, ".");
            if (className.startsWith(".")) {
                className = className.substring(1);
            }
            
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());

        } catch (Exception e) {
            System.err.println("Erreur de chargement de classe pour le Path: " + classPath.toString());
            e.printStackTrace(); 
            return null;
        }
    }

    public static List<Class<?>> getProjectClasses() {
        Path folderPath = getTargetClassesPath();

        if (folderPath == null || !Files.exists(folderPath)) {
            System.err.println("Le dossier 'target/classes' n'existe pas ou n'a pas pu être trouvé à: " + folderPath);
            return List.of();
        }

        try {
            System.out.println("Scanning Classes à partir de: " + folderPath.toAbsolutePath());
            
            return Files.walk(folderPath)
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".class"))
                        .map(p -> pathToClass(folderPath, p))
                        .filter(c -> c != null)
                        .collect(Collectors.toList());
                        
        } catch(IOException e) {
            System.err.println("Erreur lors du balayage du Classpath du projet test.");
            e.printStackTrace();
            return List.of();
        }
    }
}
