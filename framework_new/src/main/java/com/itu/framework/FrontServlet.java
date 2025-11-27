package com.itu.framework;

import com.itu.framework.helpers.ComponentScan;
import com.itu.framework.helpers.Mapping;
import com.itu.framework.view.ModelView;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FrontServlet extends HttpServlet {

    private Map<String, Mapping> urlMappings = new HashMap<>();
    private String controllerPackage;
    private String viewPrefix;
    private String viewSuffix;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.controllerPackage = config.getInitParameter("controller-package");
        if (this.controllerPackage == null || this.controllerPackage.isEmpty()) {
            throw new ServletException("The 'controller-package' init-param is missing or empty in web.xml");
        }

        this.viewPrefix = config.getInitParameter("view-prefix");
        if (this.viewPrefix == null) {
            this.viewPrefix = "";
        }

        this.viewSuffix = config.getInitParameter("view-suffix");
        if (this.viewSuffix == null) {
            this.viewSuffix = "";
        }

        try {
            this.urlMappings = ComponentScan.scanControllers(this.controllerPackage);
        } catch (Exception e) {
            throw new ServletException("Failed to scan controllers", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String path = req.getPathInfo();
        if (path == null) {
            path = req.getServletPath();
        }
        PrintWriter out = resp.getWriter();

        Mapping mapping = urlMappings.get(path);

        if (mapping == null) {
            // Check for patterns with placeholders
            for (String pattern : urlMappings.keySet()) {
                if (pattern.contains("{")) {
                    // Convert pattern to regex: /etudiants/{id} -> /etudiants/[^/]+
                    String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
                    if (path.matches(regex)) {
                        mapping = urlMappings.get(pattern);
                        break;
                    }
                }
            }
        }

        if (mapping == null) {
            // Try to forward to the default servlet for static resources
            if (req.getServletPath().startsWith("/WEB-INF/") || req.getServletPath().contains(".")) {
                req.getServletContext().getNamedDispatcher("default").forward(req, resp);
                return;
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found: " + path);
            return;
        }

        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            Object controllerInstance = clazz.getConstructor().newInstance();
            Method method = clazz.getMethod(mapping.getMethodName());

            Object result = method.invoke(controllerInstance);

            if (result instanceof String) {
                out.println(result);

            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                String viewPath = this.viewPrefix + mv.getView() + this.viewSuffix;

                if (getServletContext().getResource(viewPath) == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found: " + viewPath);
                    return;
                }

                req.getRequestDispatcher(viewPath).forward(req, resp);
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error executing method");
            e.printStackTrace(out);
        }
    }

}