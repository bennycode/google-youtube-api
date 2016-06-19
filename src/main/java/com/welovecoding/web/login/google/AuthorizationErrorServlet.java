package com.welovecoding.web.login.google;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GooglePlusLoginErrorServlet", urlPatterns = {URL.GOOGLE_PLUS_LOGIN_ERROR})
public class GooglePlusLoginErrorServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(GooglePlusLoginErrorServlet.class.getName());

  static {
    LOG.setLevel(Level.INFO);
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String error = request.getParameter("error");

    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet GooglePlusLoginErrorServlet</title>");
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>Google Plus Login Error</h1>");
      out.println(String.format("<p>Reason: %s</p>", error));
      out.println("</body>");
      out.println("</html>");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }
}
