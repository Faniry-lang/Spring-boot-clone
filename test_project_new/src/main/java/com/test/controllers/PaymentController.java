package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GetMapping;
import com.itu.framework.annotations.PostMapping;
import com.itu.framework.view.ModelView;
import com.test.models.Paiement;
import com.test.models.Utilisateur;
import com.itu.framework.annotations.Json;

import java.util.ArrayList;
import java.util.List;

@Controller("/paiement")
public class PaymentController {

    @GetMapping("/form")
    public ModelView displayForm() {
        ModelView mv = new ModelView("paiement-form");

        List<Utilisateur> users = new ArrayList<>();
        users.add(new Utilisateur(1, "Alice"));
        users.add(new Utilisateur(2, "Bob"));
        users.add(new Utilisateur(3, "Carol"));

        mv.addObject("users", users);
        return mv;
    }

    @PostMapping("/save")
    public ModelView savePayment(Paiement paiement) {
        // In-memory creation - mimic persistence
        ModelView mv = new ModelView("paiement-details");
        mv.addObject("paiement", paiement);
        return mv;
    }

    @Json
    @GetMapping("/user-json")
    public Utilisateur getSampleUserJson() {
        return new Utilisateur(99, "JsonUser");
    }

    @GetMapping("/upload-form")
    public ModelView uploadForm() {
        return new ModelView("upload-form");
    }

    @PostMapping("/upload")
    public ModelView handleUpload(java.util.Map<String, byte[]> files) {
        ModelView mv = new ModelView("upload-result");
        if (files != null && !files.isEmpty()) {
            java.util.Map.Entry<String, byte[]> e = files.entrySet().iterator().next();
            String filename = e.getKey();
            byte[] bytes = e.getValue();
            mv.addObject("filename", filename);
            mv.addObject("size", bytes != null ? bytes.length : 0);
        }
        return mv;
    }
}
