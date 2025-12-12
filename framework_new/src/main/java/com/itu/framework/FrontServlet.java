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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Enumeration;

public class FrontServlet extends HttpServlet {

    private Map<String, java.util.List<Mapping>> urlMappings = new HashMap<>();
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

        Mapping mapping = null;
        String requestMethod = req.getMethod();

        if (urlMappings.containsKey(path)) {
            for (Mapping m : urlMappings.get(path)) {
                if (m.getHttpMethod().equalsIgnoreCase(requestMethod)) {
                    mapping = m;
                    break;
                }
            }
        }

        if (mapping == null) {
            // Check for patterns with placeholders
            for (String pattern : urlMappings.keySet()) {
                if (pattern.contains("{")) {
                    // Convert pattern to regex: /etudiants/{id} -> /etudiants/[^/]+
                    String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
                    if (path.matches(regex)) {
                        for (Mapping m : urlMappings.get(pattern)) {
                            if (m.getHttpMethod().equalsIgnoreCase(requestMethod)) {
                                mapping = m;
                                break;
                            }
                        }
                        if (mapping != null)
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
            Method method = null;

            // Find the method with the matching name
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(mapping.getMethodName())) {
                    method = m;
                    break;
                }
            }

            if (method == null) {
                throw new NoSuchMethodException(mapping.getMethodName());
            }

            // Build a parameters LinkedHashMap (preserves insertion order)
            LinkedHashMap<String, String> paramsMap = new LinkedHashMap<>();
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String pname = paramNames.nextElement();
                String[] values = req.getParameterValues(pname);
                if (values != null && values.length > 0) {
                    paramsMap.put(pname, values[0]);
                }
            }

            Object[] args = new Object[method.getParameterCount()];
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            // Iterate over method parameters to bind values
            for (int i = 0; i < parameters.length; i++) {
                java.lang.reflect.Parameter parameter = parameters[i];
                String paramName = parameter.getName();

                // Check if parameter has @RequestParam annotation
                if (parameter.isAnnotationPresent(com.itu.framework.annotations.RequestParam.class)) {
                    com.itu.framework.annotations.RequestParam requestParam = parameter
                            .getAnnotation(com.itu.framework.annotations.RequestParam.class);
                    String requestParamName = requestParam.value();
                    String value = req.getParameter(requestParamName);

                    if (value != null) {
                        // Basic type conversion
                        if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                            args[i] = Integer.parseInt(value);
                        } else {
                            args[i] = value;
                        }
                    }
                } else {
                    // Try to bind primitive / String from request parameters map
                    String value = paramsMap.get(paramName);
                    if (value != null) {
                        if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                            args[i] = Integer.parseInt(value);
                        } else {
                            args[i] = value;
                        }
                    } else {
                        // Try to bind as an object: build instance from paramsMap
                        Class<?> pType = parameter.getType();
                        if (!pType.isPrimitive() && pType != String.class && pType != Integer.class
                                && pType != int.class) {
                            args[i] = instantiateObjectFromParams(pType, paramName, paramsMap);
                        } else {
                            // Check if this parameter is a path variable
                            for (String pattern : urlMappings.keySet()) {
                                if (urlMappings.get(pattern).equals(mapping) && pattern.contains("{" + paramName + "}")) {
                                    String[] patternParts = pattern.split("/");
                                    String[] pathParts = path.split("/");

                                    for (int j = 0; j < patternParts.length; j++) {
                                        if (patternParts[j].equals("{" + paramName + "}")) {
                                            if (j < pathParts.length) {
                                                String pathValue = pathParts[j];
                                                if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                                                    args[i] = Integer.parseInt(pathValue);
                                                } else {
                                                    args[i] = pathValue;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Object result = method.invoke(controllerInstance, args);

            if (result instanceof String) {
                out.println(result);

            } else if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                String viewPath = this.viewPrefix + mv.getView() + this.viewSuffix;

                // Transfer data from ModelView to Request attributes
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }

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

    // Instantiate an object and populate its fields from params map.
    private Object instantiateObjectFromParams(Class<?> type, String paramPrefix, LinkedHashMap<String, String> params) throws Exception {
        Object instance = type.getConstructor().newInstance();

        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldName = field.getName();

            // Candidates: prefix.fieldName, fieldName
            String keyWithPrefix = paramPrefix + "." + fieldName;
            String plainKey = fieldName;

            String value = null;
            if (params.containsKey(keyWithPrefix)) {
                value = params.get(keyWithPrefix);
            } else if (params.containsKey(plainKey)) {
                value = params.get(plainKey);
            }

            Class<?> fType = field.getType();
            if (value != null) {
                // Try setter first
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    java.lang.reflect.Method setter = type.getMethod(setterName, fType);
                    Object converted = convertValue(value, fType);
                    setter.invoke(instance, converted);
                    continue;
                } catch (NoSuchMethodException ignored) {
                }

                // Otherwise set field directly
                boolean accessible = field.canAccess(instance);
                field.setAccessible(true);
                Object converted = convertValue(value, fType);
                field.set(instance, converted);
                field.setAccessible(accessible);
            } else {
                // Maybe nested object fields exist: check any key starting with prefix.fieldName.
                String nestedPrefix = paramPrefix + "." + fieldName;
                boolean hasNested = false;
                for (String k : params.keySet()) {
                    if (k.startsWith(nestedPrefix + ".") || k.startsWith(fieldName + ".") ) {
                        hasNested = true;
                        break;
                    }
                }
                if (hasNested) {
                    Object nested = instantiateObjectFromParams(fType, nestedPrefix, params);
                    boolean accessible = field.canAccess(instance);
                    field.setAccessible(true);
                    field.set(instance, nested);
                    field.setAccessible(accessible);
                }
            }
        }
        return instance;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        }
        // Fallback: return the raw string
        return value;
    }

}