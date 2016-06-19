package com.welovecoding.web.login.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import static com.welovecoding.web.login.google.GooglePlusLoginServlet.getBaseUrl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {URL.GOOGLE_PLUS_LOGIN_CALLBACK})
/**
 * @see https://code.google.com/p/google-api-java-client/wiki/OAuth2
 */
public class GooglePlusLoginCallbackServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(GooglePlusLoginCallbackServlet.class.getName());

  @Inject
  private GooglePlusLoginUtilBean googlePlusLoginUtil;

  static {
    LOG.setLevel(Level.INFO);
  }

  /**
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   * @see https://developers.google.com/google-apps/tasks/oauth-authorization-callback-handler?hl=de
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    if (request.getParameter("error") != null) {
      ServletContext context = super.getServletContext();
      RequestDispatcher dispatcher = context.getRequestDispatcher(URL.GOOGLE_PLUS_LOGIN_ERROR);
      dispatcher.forward(request, response);
      return;
    }

    // Google returns a "code" that can be exchanged for an access token
    String code = request.getParameter("code");
    if (code == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The 'code' URL parameter is missing");
    } else {
      String baseUrl = getBaseUrl(request);
      GoogleTokenResponse tokenResponse = googlePlusLoginUtil.convertCodeToToken(code, baseUrl + URL.GOOGLE_PLUS_LOGIN_CALLBACK);
      String user = googlePlusLoginUtil.getUser(tokenResponse);
      String accessToken = tokenResponse.getIdToken();

      // Send response
      LOG.log(Level.INFO, "Access Token: {0}", accessToken);
      LOG.log(Level.INFO, "Google User: {0}", user);
      response.setContentType("text/html;charset=UTF-8");
      try (PrintWriter out = response.getWriter()) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet Bla</title>");
        out.println("</head>");
        out.println("<body>");
        out.println(String.format("<p>Access token: </p><pre>%s</pre>", accessToken));
        out.println("</body>");
        out.println("</html>");
      }
    }
  }

}
