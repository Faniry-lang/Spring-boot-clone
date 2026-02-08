package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GET;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.annotations.Session;
import com.itu.framework.view.ModelView;
import com.itu.framework.annotations.Anonym;
import com.itu.framework.annotations.Authorized;
import com.itu.framework.annotations.Role;

import java.util.Map;

@Controller("/security")
public class SecurityTestController {

    @GET
    @UrlMapping("/links")
    public ModelView links(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("security-links");
        Object user = session.get("user");
        mv.addObject("user", user);
        return mv;
    }

    @GET
    @UrlMapping("/anonym")
    @Anonym
    public ModelView anonym() {
        ModelView mv = new ModelView("security-result");
        mv.addObject("message", "Accès anonyme autorisé");
        return mv;
    }

    @GET
    @UrlMapping("/authorized")
    @Authorized
    public ModelView authorized() {
        ModelView mv = new ModelView("security-result");
        mv.addObject("message", "Accès pour utilisateur authentifié autorisé");
        return mv;
    }

    @GET
    @UrlMapping("/role")
    @Role({"ADMIN"})
    public ModelView role() {
        ModelView mv = new ModelView("security-result");
        mv.addObject("message", "Accès réservé aux ADMIN autorisé");
        return mv;
    }

    @GET
    @UrlMapping("/login")
    public ModelView login(@Session Map<String, Object> session, String username, String role) {
        ModelView mv = new ModelView("security-login-result");
        if (username != null && !username.isEmpty()) {
            session.put("user", username);
            if (role == null || role.isEmpty()) {
                session.put("userRole", "USER");
            } else {
                session.put("userRole", role);
            }
            mv.addObject("success", true);
            mv.addObject("username", username);
            mv.addObject("role", session.get("userRole"));
        } else {
            mv.addObject("success", false);
        }
        return mv;
    }

    @GET
    @UrlMapping("/logout")
    public ModelView logout(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("security-login-result");
        Object u = session.get("user");
        session.remove("user");
        session.remove("userRole");
        mv.addObject("success", false);
        mv.addObject("username", u);
        return mv;
    }
}
