package com.itu.framework;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {

    @Override
    protected void service(
        HttpServletRequest req,
        HttpServletResponse res 
    ) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        PrintWriter out = res.getWriter();
        out.println(url);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
