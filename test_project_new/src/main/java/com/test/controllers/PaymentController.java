package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GET;
import com.itu.framework.annotations.POST;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.view.ModelView;
import com.test.models.Paiement;
import com.test.models.Utilisateur;

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
}
