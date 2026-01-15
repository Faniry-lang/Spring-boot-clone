package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GET;
import com.itu.framework.annotations.POST;
import com.itu.framework.annotations.Session;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.view.ModelView;
import com.test.models.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller("/session")
public class SessionTestController {

    // Simulated in-memory database
    private static List<Student> studentDatabase = new ArrayList<>();

    static {
        studentDatabase.add(new Student(1, "Alice Dupont", "alice@example.com"));
        studentDatabase.add(new Student(2, "Bob Martin", "bob@example.com"));
        studentDatabase.add(new Student(3, "Carol Dubois", "carol@example.com"));
        studentDatabase.add(new Student(4, "David Bernard", "david@example.com"));
    }

    /**
     * Display the home page with student list and current session info
     */
    @GET
    @UrlMapping("/home")
    public ModelView showHome(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("session-home");
        mv.addObject("students", studentDatabase);

        // Get current logged-in student from session (if any)
        Student currentStudent = (Student) session.get("currentStudent");
        mv.addObject("currentStudent", currentStudent);

        // Get visit count
        Integer visitCount = (Integer) session.get("visitCount");
        if (visitCount == null) {
            visitCount = 0;
        }
        visitCount++;
        session.put("visitCount", visitCount);
        mv.addObject("visitCount", visitCount);

        return mv;
    }

    /**
     * Login by storing a student in session
     */
    @GET
    @UrlMapping("/login")
    public ModelView login(@Session Map<String, Object> session, Integer id) {
        Student student = findStudentById(id);

        if (student != null) {
            session.put("currentStudent", student);
            session.put("loginTime", System.currentTimeMillis());
        }

        ModelView mv = new ModelView("session-login-result");
        mv.addObject("student", student);
        mv.addObject("success", student != null);
        return mv;
    }

    /**
     * Logout by removing student from session
     */
    @GET
    @UrlMapping("/logout")
    public ModelView logout(@Session Map<String, Object> session) {
        Student currentStudent = (Student) session.get("currentStudent");
        session.remove("currentStudent");
        session.remove("loginTime");

        ModelView mv = new ModelView("session-logout-result");
        mv.addObject("student", currentStudent);
        return mv;
    }

    /**
     * Display current session information
     */
    @GET
    @UrlMapping("/info")
    public ModelView sessionInfo(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("session-info");
        mv.addObject("sessionData", session);

        Student currentStudent = (Student) session.get("currentStudent");
        mv.addObject("currentStudent", currentStudent);

        Long loginTime = (Long) session.get("loginTime");
        if (loginTime != null) {
            long duration = (System.currentTimeMillis() - loginTime) / 1000;
            mv.addObject("sessionDuration", duration);
        }

        return mv;
    }

    /**
     * Add an item to shopping cart in session
     */
    @GET
    @UrlMapping("/cart/add")
    public ModelView addToCart(@Session Map<String, Object> session, String item) {
        @SuppressWarnings("unchecked")
        List<String> cart = (List<String>) session.get("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.put("cart", cart);
        }

        if (item != null && !item.isEmpty()) {
            cart.add(item);
        }

        ModelView mv = new ModelView("session-cart");
        mv.addObject("cart", cart);
        return mv;
    }

    /**
     * View shopping cart
     */
    @GET
    @UrlMapping("/cart/view")
    public ModelView viewCart(@Session Map<String, Object> session) {
        @SuppressWarnings("unchecked")
        List<String> cart = (List<String>) session.get("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        ModelView mv = new ModelView("session-cart");
        mv.addObject("cart", cart);
        return mv;
    }

    /**
     * Clear shopping cart
     */
    @GET
    @UrlMapping("/cart/clear")
    public ModelView clearCart(@Session Map<String, Object> session) {
        session.remove("cart");

        ModelView mv = new ModelView("session-cart");
        mv.addObject("cart", new ArrayList<>());
        mv.addObject("message", "Panier vidé avec succès");
        return mv;
    }

    // Helper method
    private Student findStudentById(Integer id) {
        if (id == null) return null;

        for (Student s : studentDatabase) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }
}

