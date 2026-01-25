package com.itu.framework.helpers;

import jakarta.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class SecurityConfig {
    private String roleKey = "role";
    private String authorizedKey = "authorized";
    private String anonymKey = "anonym";

    public SecurityConfig() {}

    public void loadFromServletContext(ServletContext ctx, String path) {
        try (InputStream is = ctx.getResourceAsStream(path)) {
            if (is == null) {
                return;
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document doc = dbf.newDocumentBuilder().parse(is);
            Element root = doc.getDocumentElement();
            Element keys = (Element) root.getElementsByTagName("keys").item(0);
            if (keys != null) {
                Element roleEl = (Element) keys.getElementsByTagName("role").item(0);
                Element authEl = (Element) keys.getElementsByTagName("authorized").item(0);
                Element anonEl = (Element) keys.getElementsByTagName("anonym").item(0);

                if (roleEl != null && roleEl.getTextContent() != null && !roleEl.getTextContent().trim().isEmpty()) {
                    this.roleKey = roleEl.getTextContent().trim();
                }
                if (authEl != null && authEl.getTextContent() != null && !authEl.getTextContent().trim().isEmpty()) {
                    this.authorizedKey = authEl.getTextContent().trim();
                }
                if (anonEl != null && anonEl.getTextContent() != null && !anonEl.getTextContent().trim().isEmpty()) {
                    this.anonymKey = anonEl.getTextContent().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRoleKey() {
        return roleKey;
    }

    public String getAuthorizedKey() {
        return authorizedKey;
    }

    public String getAnonymKey() {
        return anonymKey;
    }
}
