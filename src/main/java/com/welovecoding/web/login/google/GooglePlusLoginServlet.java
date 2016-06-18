package com.welovecoding.web.login.google;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.youtube.YouTubeScopes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {URL.GOOGLE_PLUS_LOGIN})
public class GooglePlusLoginServlet extends HttpServlet {

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static String CLIENT_ID;
  private static String CLIENT_SECRET;
  private static final List<String> SCOPES = Arrays.asList(
    PlusScopes.USERINFO_EMAIL,
    PlusScopes.USERINFO_PROFILE,
    YouTubeScopes.YOUTUBE_READONLY
  );

  private static final Logger LOG = Logger.getLogger(GooglePlusLoginServlet.class.getName());

  static {
    LOG.setLevel(Level.INFO);
  }

  /**
   * @throws javax.servlet.ServletException
   * @todo Outsource credentials into Bean
   */
  @Override
  public void init() throws ServletException {
    super.init();

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream stream = classLoader.getResourceAsStream("production.properties");

    if (stream != null) {
      try {
        Properties properties = new Properties();
        properties.load(stream);
        CLIENT_ID = properties.getProperty("GOOGLE_PLUS_LOGIN_CLIENT_ID");
        CLIENT_SECRET = properties.getProperty("GOOGLE_PLUS_LOGIN_CLIENT_SECRET");
        LOG.log(Level.INFO, "Client secret: {0}", CLIENT_SECRET);
      } catch (IOException ex) {
        Logger.getLogger(GooglePlusLoginServlet.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static String getBaseUrl(HttpServletRequest req) {
    String scheme = req.getScheme() + "://";
    String serverName = req.getServerName();
    String serverPort = (req.getServerPort() == 80) ? "" : ":" + req.getServerPort();
    String contextPath = req.getContextPath();
    return scheme + serverName + serverPort + contextPath;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    String baseUrl = getBaseUrl(request);
    OAuth20Service service = new ServiceBuilder()
      .apiKey(CLIENT_ID)
      .apiSecret(CLIENT_SECRET)
      .scope("profile")
      .callback(baseUrl + URL.GOOGLE_PLUS_LOGIN_CALLBACK)
      .build(GoogleApi20.instance());

    HttpSession session = request.getSession();
    session.setAttribute("scribejava", service);

    response.sendRedirect(service.getAuthorizationUrl());
  }
}
