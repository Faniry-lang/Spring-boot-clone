package com.itu.framework;

import com.itu.framework.annotations.FrameworkApplication;
import com.itu.framework.server.ServerApplication;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FrameworkRunner {

    public static void run(Class<?> applicationClass, String[] args) {
        try {
            
            if (!applicationClass.isAnnotationPresent(FrameworkApplication.class)) {
                throw new IllegalArgumentException(
                    "Application class must be annotated with @FrameworkApplication: " + applicationClass.getName()
                );
            }

            FrameworkApplication annotation = applicationClass.getAnnotation(FrameworkApplication.class);
            
            int port = annotation.port();
            if (args != null) {
                for (String arg : args) {
                    if (arg.startsWith("--port=")) {
                        try {
                            port = Integer.parseInt(arg.substring("--port=".length()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            
            File webappDir = extractWebappToTempDir(applicationClass);
            
            File webXmlFile = new File(webappDir, "WEB-INF/web.xml");
            if (!webXmlFile.exists()) {
                throw new IllegalStateException(
                    "WEB-INF/web.xml not found. Please ensure your project has a web.xml file in src/main/webapp/WEB-INF/"
                );
            }
            
            System.out.println("Starting Framework Application...");
            System.out.println("Port: " + port);
            System.out.println("Web.xml: " + webXmlFile.getAbsolutePath());
            
            Tomcat tomcat = ServerApplication.start(port, "/", webappDir);
            
            System.out.println("Framework Application started successfully on port " + port);
            System.out.println("Press Ctrl+C to stop");
            

            tomcat.getServer().await();
            
        } catch (Exception e) {
            System.err.println("Failed to start Framework Application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static File extractWebappToTempDir(Class<?> applicationClass) throws IOException, URISyntaxException {
 
        URL webXml = applicationClass.getResource("/WEB-INF/web.xml");
        
        if (webXml != null && "file".equals(webXml.getProtocol())) {
          
            Path classesPath = new File(applicationClass.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath();
            File projectDir = classesPath.toFile().getParentFile().getParentFile();
            File webapp = new File(projectDir, "src/main/webapp");
            if (webapp.exists()) {
                return webapp;
            }
        }


        String codeLocation = applicationClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(codeLocation);
        
        if (!jarFile.exists() || !jarFile.getName().endsWith(".jar")) {

            File tmpDir = Files.createTempDirectory("framework-webapp").toFile();
            tmpDir.deleteOnExit();
            return tmpDir;
        }

        File tmpDir = Files.createTempDirectory("framework-webapp").toFile();
        tmpDir.deleteOnExit();

        try (JarFile jf = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
      
                if (name.startsWith("WEB-INF/")) {
                    if (name.endsWith("/")) continue;
                    File out = new File(tmpDir, name);
                    out.getParentFile().mkdirs();
                    try (InputStream is = jf.getInputStream(entry)) {
                        Files.copy(is, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }

        return tmpDir;
    }
}
