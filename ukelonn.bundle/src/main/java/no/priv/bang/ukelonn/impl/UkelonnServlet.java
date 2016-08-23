package no.priv.bang.ukelonn.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UkelonnServlet extends HttpServlet {
    private static final long serialVersionUID = -3496606785818930881L;
    private final String registrationPath;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        final PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head><title>Ukelønn</title></head>");
        writer.println("<body align='center'>");
        writer.println("<h1>Ukelønn</h1>");
        writer.println("<img src='/images/logo.png' border='0'/>");
        writer.println("<h1>" + getServletConfig().getInitParameter("from") + "</h1>");
        writer.println("<p>");
        writer.println("Served by servlet registered at: " + registrationPath);
        writer.println("<br/>");
        writer.println("Servlet Path: " + request.getServletPath());
        writer.println("<br/>");
        writer.println("Path Info: " + request.getPathInfo());
        writer.println("</p>");
        writer.println("</body></html>");
    }

    public UkelonnServlet(String registrationPath) {
        super();
        this.registrationPath = registrationPath;
    }

}
