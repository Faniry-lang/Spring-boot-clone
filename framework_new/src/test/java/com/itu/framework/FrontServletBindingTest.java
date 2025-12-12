package com.itu.framework;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Date;
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

    public static class OrderTest {
        private java.util.List<String> tags;
        private java.util.Set<Integer> codes;

        public OrderTest() {}

        public java.util.List<String> getTags() { return tags; }
        public void setTags(java.util.List<String> tags) { this.tags = tags; }

        public java.util.Set<Integer> getCodes() { return codes; }
        public void setCodes(java.util.Set<Integer> codes) { this.codes = codes; }
    }

    public static class DateWrapperTest {
        private Date date;

        public DateWrapperTest() {}

        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
    }

    @Test
    public void testCollectionBinding() throws Exception {
        FrontServlet servlet = new FrontServlet();

        LinkedHashMap<String, String[]> params = new LinkedHashMap<>();
        // Repeated parameter values for tags
        params.put("order.tags", new String[]{"t1", "t2", "t3"});
        // Single parameter for codes but multiple values
        params.put("order.codes", new String[]{"10", "20", "30"});

        Method m = FrontServlet.class.getDeclaredMethod("instantiateObjectFromParams", Class.class, String.class, LinkedHashMap.class);
        m.setAccessible(true);
        Object result = m.invoke(servlet, OrderTest.class, "order", params);

        assertNotNull(result);
        assertTrue(result instanceof OrderTest);

        OrderTest o = (OrderTest) result;
        assertNotNull(o.getTags());
        assertEquals(3, o.getTags().size());
        assertTrue(o.getTags().contains("t2"));

        assertNotNull(o.getCodes());
        assertEquals(3, o.getCodes().size());
        assertTrue(o.getCodes().contains(20));
    }

    @Test
    public void testDateParsing() throws Exception {
        FrontServlet servlet = new FrontServlet();

        LinkedHashMap<String, String[]> params = new LinkedHashMap<>();
        params.put("dw.date", new String[]{"2025-12-12"});

        Method m = FrontServlet.class.getDeclaredMethod("instantiateObjectFromParams", Class.class, String.class, LinkedHashMap.class);
        m.setAccessible(true);
        Object result = m.invoke(servlet, DateWrapperTest.class, "dw", params);

        assertNotNull(result);
        assertTrue(result instanceof DateWrapperTest);
        DateWrapperTest dwt = (DateWrapperTest) result;
        assertNotNull(dwt.getDate());
        // Basic check: year is 2025 (months are 0-based in Date#getYear deprecated, so use calendar)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(dwt.getDate());
        assertEquals(2025, cal.get(java.util.Calendar.YEAR));
        assertEquals(12, cal.get(java.util.Calendar.MONTH) + 1);
        assertEquals(12, cal.get(java.util.Calendar.DAY_OF_MONTH));
    }
}
