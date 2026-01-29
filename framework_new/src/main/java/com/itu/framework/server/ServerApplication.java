package com.itu.framework.server;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.DirResourceSet;

import java.io.File;

public class ServerApplication {
    public static Tomcat start(int port, String contextPath, File webappDir) throws Exception {
        if (webappDir == null) {
            throw new IllegalArgumentException("webappDir cannot be null");
        }
        if (!webappDir.exists()) {
            throw new IllegalArgumentException("webappDir does not exist: " + webappDir.getAbsolutePath());
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        // use a temp directory for Tomcat working dir
        File baseDir = new File(System.getProperty("java.io.tmpdir"), "embedded-tomcat");
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // add webapp context
        Context ctx = tomcat.addWebapp(contextPath, webappDir.getAbsolutePath());

        // Configure resources so that WEB-INF/classes from the exploded webapp are visible
        WebResourceRoot resources = new StandardRoot(ctx);
        File classesDir = new File(webappDir, "WEB-INF/classes");
        if (classesDir.exists()) {
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"));
        }
        ctx.setResources(resources);

        // Ensure connector is created
        tomcat.getConnector();

        tomcat.start();

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception e) {
                // ignore
            }
        }));

        return tomcat;
    }
}