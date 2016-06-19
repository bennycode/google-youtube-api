package com.welovecoding.web.login.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import static com.welovecoding.web.login.google.AuthorizationCodeServlet.getBaseUrl;
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

/**
 * Authorization code callback servlet to process the authorization code or error response from authorization page redirect.
 *
 * @see https://code.google.com/p/google-api-java-client/wiki/OAuth2
 * @see https://github.com/google/google-oauth-java-client/blob/dev/google-oauth-client-servlet/src/main/java/com/google/api/client/extensions/servlet/auth/oauth2/AbstractAuthorizationCodeCallbackServlet.java
 */
@WebServlet(urlPatterns = {URL.GOOGLE_PLUS_LOGIN_CALLBACK})
public class AuthorizationCodeCallbackServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(AuthorizationCodeCallbackServlet.class.getName());

  @Inject
  private UtilBean googlePlusLoginUtil;
  private String accessToken;

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
      onError(request, response);
    } else {
      String code = request.getParameter("code");
      if (code == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The 'code' URL parameter is missing");
      } else {
        String baseUrl = getBaseUrl(request);
        GoogleTokenResponse tokenResponse = googlePlusLoginUtil.convertCodeToToken(code, baseUrl + URL.GOOGLE_PLUS_LOGIN_CALLBACK);
        accessToken = tokenResponse.getIdToken();
        onSuccess(request, response);
      }
    }
  }

  private void onSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LOG.log(Level.INFO, "Access Token: {0}", accessToken);
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

  private void onError(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    ServletContext context = super.getServletContext();
    RequestDispatcher dispatcher = context.getRequestDispatcher(URL.GOOGLE_PLUS_LOGIN_ERROR);
    dispatcher.forward(request, response);
  }

}