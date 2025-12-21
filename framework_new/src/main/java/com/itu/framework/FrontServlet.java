package com.itu.framework;

import com.itu.framework.helpers.ComponentScan;
import com.itu.framework.helpers.Mapping;
import com.itu.framework.view.ModelView;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.itu.framework.annotations.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

@MultipartConfig
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
        String path = computeRequestPath(req);
        PrintWriter out = resp.getWriter();

        String requestMethod = req.getMethod();
        Mapping mapping = findMappingForPath(path, requestMethod);

        if (mapping == null) {
            if (isStaticResourceRequest(req)) {
                req.getServletContext().getNamedDispatcher("default").forward(req, resp);
                return;
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found: " + path);
            return;
        }

        try {
            Class<?> controllerClass = Class.forName(mapping.getClassName());
            Object controllerInstance = controllerClass.getConstructor().newInstance();
            Method method = findMethodForMapping(controllerClass, mapping.getMethodName());

            if (method == null) {
                throw new NoSuchMethodException(mapping.getMethodName());
            }

            LinkedHashMap<String, String[]> paramsMap = buildParamsMap(req);
            Object[] args = bindMethodArguments(method, paramsMap, path, mapping, req);

            Object result = method.invoke(controllerInstance, args);

            // If the controller method is annotated with @Json, serialize the result to JSON.
            if (method.isAnnotationPresent(Json.class)) {
                writeJsonResponse(result, resp);
            } else {
                processControllerResult(result, req, resp, out);
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error executing method");
            e.printStackTrace(out);
        }
    }

    private String computeRequestPath(HttpServletRequest req) {
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        return path;
    }

    private Mapping findMappingForPath(String path, String requestMethod) {
        // direct match
        if (urlMappings.containsKey(path)) {
            for (Mapping m : urlMappings.get(path)) {
                if (m.getHttpMethod().equalsIgnoreCase(requestMethod)) {
                    return m;
                }
            }
        }

        // pattern match with placeholders
        for (String pattern : urlMappings.keySet()) {
            if (pattern.contains("{")) {
                String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
                if (path.matches(regex)) {
                    for (Mapping m : urlMappings.get(pattern)) {
                        if (m.getHttpMethod().equalsIgnoreCase(requestMethod)) {
                            return m;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isStaticResourceRequest(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        return servletPath != null && (servletPath.startsWith("/WEB-INF/") || servletPath.contains("."));
    }

    private Method findMethodForMapping(Class<?> controllerClass, String methodName) {
        for (Method m : controllerClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    private LinkedHashMap<String, String[]> buildParamsMap(HttpServletRequest req) {
        LinkedHashMap<String, String[]> paramsMap = new LinkedHashMap<>();

        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            Map<String, byte[]> uploadedBytes = new HashMap<>();
            try {
                for (Part part : req.getParts()) {
                    String name = part.getName();
                    String submitted = part.getSubmittedFileName();
                    if (submitted == null) {
                        // regular form field
                        InputStream is = part.getInputStream();
                        byte[] bytes = is.readAllBytes();
                        String charset = req.getCharacterEncoding() != null ? req.getCharacterEncoding() : "UTF-8";
                        String value = new String(bytes, charset);
                        paramsMap.put(name, new String[]{value});
                    } else {
                        // file part - key by original filename
                        byte[] bytes = part.getInputStream().readAllBytes();
                        if (!uploadedBytes.containsKey(submitted)) {
                            uploadedBytes.put(submitted, bytes);
                        }
                    }
                }
            } catch (Exception e) {
                Enumeration<String> paramNames = req.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String pname = paramNames.nextElement();
                    String[] values = req.getParameterValues(pname);
                    if (values != null && values.length > 0) {
                        paramsMap.put(pname, values);
                    }
                }
            }

            req.setAttribute("uploadedFilesBytes", uploadedBytes);
            return paramsMap;
        }

        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String pname = paramNames.nextElement();
            String[] values = req.getParameterValues(pname);
            if (values != null && values.length > 0) {
                paramsMap.put(pname, values);
            }
        }
        return paramsMap;
    }

    private Object[] bindMethodArguments(Method method, LinkedHashMap<String, String[]> paramsMap, String path, Mapping mapping, HttpServletRequest req) throws Exception {
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter parameter = parameters[i];
            String paramName = parameter.getName();

            if (parameter.isAnnotationPresent(com.itu.framework.annotations.RequestParam.class)) {
                com.itu.framework.annotations.RequestParam requestParam = parameter
                        .getAnnotation(com.itu.framework.annotations.RequestParam.class);
                String requestParamName = requestParam.value();
                String value = req.getParameter(requestParamName);

                if (value != null) {
                    if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                        args[i] = Integer.parseInt(value);
                    } else {
                        args[i] = value;
                    }
                }
                continue;
            }

            String[] pvals = paramsMap.get(paramName);
            String value = (pvals != null && pvals.length > 0) ? pvals[0] : null;
            if (value != null) {
                if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                    args[i] = Integer.parseInt(value);
                } else {
                    args[i] = value;
                }
                continue;
            }

            Class<?> pType = parameter.getType();
            // First, support binding uploaded files if present
            Object uploadedBytesAttr = req.getAttribute("uploadedFilesBytes");
            if (uploadedBytesAttr instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, byte[]> uploadedBytes = (Map<String, byte[]>) uploadedBytesAttr;

                // Map<String, byte[]> binding - keys are original filenames
                if (Map.class.isAssignableFrom(parameter.getType())) {
                    java.lang.reflect.Type pTypeGeneric = parameter.getParameterizedType();
                    if (pTypeGeneric instanceof ParameterizedType) {
                        java.lang.reflect.Type[] typeArgs = ((ParameterizedType) pTypeGeneric).getActualTypeArguments();
                        if (typeArgs != null && typeArgs.length == 2 && typeArgs[0] instanceof Class && typeArgs[1] instanceof Class) {
                            Class<?> keyCls = (Class<?>) typeArgs[0];
                            java.lang.Class<?> valCls = (Class<?>) typeArgs[1];
                            if (keyCls == String.class && valCls == byte[].class) {
                                args[i] = uploadedBytes;
                                continue;
                            }
                        }
                    }
                }
            }

            if (!pType.isPrimitive() && pType != String.class && pType != Integer.class && pType != int.class) {
                args[i] = instantiateObjectFromParams(pType, paramName, paramsMap);
                continue;
            }

            // Check for path variable: pattern that contains {paramName} and whose mapping list contains this mapping
            for (String pattern : urlMappings.keySet()) {
                if (pattern.contains("{" + paramName + "}") && urlMappings.get(pattern).contains(mapping)) {
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

        return args;
    }

    private void processControllerResult(Object result, HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws Exception {
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
    }

    private void writeJsonResponse(Object result, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();

        // If result is ModelView, serialize the data map; otherwise serialize the object directly.
        if (result instanceof ModelView) {
            ModelView mv = (ModelView) result;
            mapper.writeValue(resp.getWriter(), mv.getData());
        } else {
            mapper.writeValue(resp.getWriter(), result);
        }
    }

    // Instantiate an object and populate its fields from params map.
    private Object instantiateObjectFromParams(Class<?> type, String paramPrefix, LinkedHashMap<String, String[]> params) throws Exception {
        Object instance = type.getConstructor().newInstance();

        java.lang.reflect.Field[] fields = type.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            String fieldName = field.getName();

            // Candidates: prefix.fieldName, fieldName
            String keyWithPrefix = paramPrefix + "." + fieldName;
            String plainKey = fieldName;

            String[] rawValues = null;
            if (params.containsKey(keyWithPrefix)) {
                rawValues = params.get(keyWithPrefix);
            } else if (params.containsKey(plainKey)) {
                rawValues = params.get(plainKey);
            }

            Class<?> fType = field.getType();

            // Handle collections (List/Set)
            if (Collection.class.isAssignableFrom(fType)) {
                // Determine element type if available
                Class<?> elementType = String.class;
                Type gType = field.getGenericType();
                if (gType instanceof ParameterizedType) {
                    Type[] args = ((ParameterizedType) gType).getActualTypeArguments();
                    if (args != null && args.length > 0) {
                        if (args[0] instanceof Class) {
                            elementType = (Class<?>) args[0];
                        }
                    }
                }

                Collection<Object> collection;
                if (Set.class.isAssignableFrom(fType)) {
                    collection = new HashSet<>();
                } else {
                    collection = new ArrayList<>();
                }

                // Case 1: repeated parameter names: keyWithPrefix or plainKey -> multiple values
                if (rawValues != null && rawValues.length > 1) {
                    for (String rv : rawValues) {
                        Object converted = convertValue(rv, elementType);
                        collection.add(converted);
                    }
                } else {
                    // Case 2: indexed nested objects like prefix.field[0].sub
                    String nestedPrefix = paramPrefix + "." + fieldName;
                    // Find indices
                    java.util.Set<Integer> indices = new java.util.TreeSet<>();
                    for (String k : params.keySet()) {
                        if (k.startsWith(nestedPrefix + "[")) {
                            int start = k.indexOf('[') + 1;
                            int end = k.indexOf(']', start);
                            if (start > 0 && end > start) {
                                try {
                                    int idx = Integer.parseInt(k.substring(start, end));
                                    indices.add(idx);
                                } catch (NumberFormatException ignored) {}
                            }
                        } else if (k.startsWith(fieldName + "[")) {
                            int start = k.indexOf('[') + 1;
                            int end = k.indexOf(']', start);
                            if (start > 0 && end > start) {
                                try {
                                    int idx = Integer.parseInt(k.substring(start, end));
                                    indices.add(idx);
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }

                    if (!indices.isEmpty()) {
                        for (int idx : indices) {
                            String elementPrefix = nestedPrefix + "[" + idx + "]";
                            Object nested = instantiateObjectFromParams(elementType, elementPrefix, params);
                            collection.add(nested);
                        }
                    }
                }

                // Set collection via setter or direct
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    java.lang.reflect.Method setter = type.getMethod(setterName, field.getType());
                    setter.invoke(instance, collection);
                    continue;
                } catch (NoSuchMethodException ignored) {
                }

                boolean accessible = field.canAccess(instance);
                field.setAccessible(true);
                field.set(instance, collection);
                field.setAccessible(accessible);
                continue;
            }

            if (rawValues != null && rawValues.length > 0) {
                String value = rawValues[0];
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
        } else if (targetType == Date.class) {
            // try multiple common formats
            String[] formats = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd"};
            for (String fmt : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                    return sdf.parse(value);
                } catch (Exception ignored) {
                }
            }
        }
        // Fallback: return the raw string
        return value;
    }

}