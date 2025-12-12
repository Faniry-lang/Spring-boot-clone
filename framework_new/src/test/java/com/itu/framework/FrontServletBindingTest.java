package com.itu.framework;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class FrontServletBindingTest {

    public static class UtilisateurTest {
        private int id;
        private String name;

        public UtilisateurTest() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class PaiementTest {
        private int id;
        private double amount;
        private UtilisateurTest utilisateur;

        public PaiementTest() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public UtilisateurTest getUtilisateur() { return utilisateur; }
        public void setUtilisateur(UtilisateurTest utilisateur) { this.utilisateur = utilisateur; }
    }

    @Test
    public void testInstantiateObjectFromParams() throws Exception {
        FrontServlet servlet = new FrontServlet();

        LinkedHashMap<String, String[]> params = new LinkedHashMap<>();
        params.put("paiement.id", new String[]{"42"});
        params.put("paiement.amount", new String[]{"123.45"});
        params.put("paiement.utilisateur.id", new String[]{"7"});
        params.put("paiement.utilisateur.name", new String[]{"TestUser"});

        Method m = FrontServlet.class.getDeclaredMethod("instantiateObjectFromParams", Class.class, String.class, LinkedHashMap.class);
        m.setAccessible(true);
        Object result = m.invoke(servlet, PaiementTest.class, "paiement", params);

        assertNotNull(result);
        assertTrue(result instanceof PaiementTest);

        PaiementTest p = (PaiementTest) result;
        assertEquals(42, p.getId());
        assertEquals(123.45, p.getAmount(), 0.0001);
        assertNotNull(p.getUtilisateur());
        assertEquals(7, p.getUtilisateur().getId());
        assertEquals("TestUser", p.getUtilisateur().getName());
    }
}
