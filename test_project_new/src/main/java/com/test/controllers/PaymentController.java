package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GET;
import com.itu.framework.annotations.POST;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.view.ModelView;
import com.test.models.Paiement;
import com.test.models.Utilisateur;
import com.itu.framework.annotations.Json;
import com.itu.framework.upload.UploadedFile;

import java.util.ArrayList;
import java.util.List;

@Controller("/paiement")
public class PaymentController {

    @GET
    @UrlMapping("/form")
    public ModelView displayForm() {
        ModelView mv = new ModelView("paiement-form");

        List<Utilisateur> users = new ArrayList<>();
        users.add(new Utilisateur(1, "Alice"));
        users.add(new Utilisateur(2, "Bob"));
        users.add(new Utilisateur(3, "Carol"));

        mv.addObject("users", users);
        return mv;
    }

    @POST
    @UrlMapping("/save")
    public ModelView savePayment(Paiement paiement) {
        // In-memory creation - mimic persistence
        ModelView mv = new ModelView("paiement-details");
        mv.addObject("paiement", paiement);
        return mv;
    }

    @Json
    @GET
    @UrlMapping("/user-json")
    public Utilisateur getSampleUserJson() {
        return new Utilisateur(99, "JsonUser");
    }

    @GET
    @UrlMapping("/upload-form")
    public ModelView uploadForm() {
        return new ModelView("upload-form");
    }

    @POST
    @UrlMapping("/upload")
    public ModelView handleUpload(java.util.Map<String, byte[]> files) {
        ModelView mv = new ModelView("upload-result");
        if (files != null && !files.isEmpty()) {
            // pick the first entry (filename -> bytes)
            java.util.Map.Entry<String, byte[]> e = files.entrySet().iterator().next();
            String filename = e.getKey();
            byte[] bytes = e.getValue();
            mv.addObject("filename", filename);
            mv.addObject("size", bytes != null ? bytes.length : 0);
        }
        return mv;
    }
}
