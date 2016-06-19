package com.welovecoding.web.login.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.youtube.YouTubeScopes;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @see https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#web_server_applications
 */
@ApplicationScoped
public class GooglePlusLoginUtilBean implements Serializable {

  private static String CLIENT_ID;
  private static String CLIENT_SECRET;
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final Logger LOG = Logger.getLogger(GooglePlusLoginUtilBean.class.getName());
  private static final List<String> SCOPES = Arrays.asList(
    PlusScopes.USERINFO_EMAIL,
    PlusScopes.USERINFO_PROFILE,
    YouTubeScopes.YOUTUBE_READONLY
  );

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
    return new GoogleAuthorizationCodeTokenRequest(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      CLIENT_ID,
      CLIENT_SECRET,
      code,
      redirectUri
    ).execute();
  }

  public String getUser(GoogleTokenResponse response) throws IOException {
    GoogleAuthorizationCodeFlow flow
      = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        CLIENT_ID,
        CLIENT_SECRET,
        SCOPES).build();

    Credential credential = flow.createAndStoreCredential(response, null);
    HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);

    GenericUrl url = new GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo");
    HttpRequest request = requestFactory.buildGetRequest(url);
    String jsonIdentity = request.execute().parseAsString();

    return jsonIdentity;
  }
}
