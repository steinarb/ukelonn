package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UkelonnServletTest {

    @Test
    public void testRender() throws IOException, ServletException {
        UkelonnServlet servlet = new UkelonnServlet("/ukelonn");
        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/ukelonn");
        StringWriter responseContent = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(responseContent));

        servlet.doGet(request, response);

        System.out.println("html: " + responseContent.toString());
        assertNotEquals(0, responseContent.toString().length());
    }

}
