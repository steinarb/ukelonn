package no.priv.bang.ukelonn.impl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.rendersnake.HtmlAttributesFactory.*;
import org.rendersnake.ext.servlet.HtmlServletCanvas;

public class UkelonnServlet extends HttpServlet {
    private static final long serialVersionUID = -3496606785818930881L;
    private final String registrationPath;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        HtmlServletCanvas html = new HtmlServletCanvas(request, response, response.getWriter());
        html
            .html()
            .head().title().content("Ukelønn")._head()
            .body(align("center"))
            .h1().content("Ukelønn")
            .img(src("/images/logo.png").border("0"))
            .h1().content(getServletConfig().getInitParameter("from"))
            .p()
            .write("Served by servlet registered at: " + registrationPath).br()
            .write("Servlet Path: " + request.getServletPath()).br()
            .write("Path Info: " + request.getPathInfo())
            ._p()
            ._body()
            ._html();
    }

    public UkelonnServlet(String registrationPath) {
        super();
        this.registrationPath = registrationPath;
    }

}
