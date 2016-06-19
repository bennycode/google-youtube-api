package com.welovecoding.web.login.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GooglePlusLoginUtilBean implements Serializable {

  private static String CLIENT_ID;
  private static String CLIENT_SECRET;

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final Logger LOG = Logger.getLogger(GooglePlusLoginUtilBean.class.getName());

  static {
    LOG.setLevel(Level.INFO);
  }

  @PostConstruct
  void init() {
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
        LOG.log(Level.SEVERE, ex.getMessage());
      }
    }
  }

  public GoogleTokenResponse convertCodeToToken(String code, String redirectUri) throws IOException {
    GoogleAuthorizationCodeTokenRequest request
      = new GoogleAuthorizationCodeTokenRequest(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        CLIENT_ID,
        CLIENT_SECRET,
        code,
        redirectUri);

    return request.execute();
  }
}
