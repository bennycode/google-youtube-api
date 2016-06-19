package com.welovecoding.web.login.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @see https://github.com/google/google-http-java-client
 * @see
 * @see https://developers.google.com/api-client-library/java/
 * @see https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#web_server_applications
 */
@ApplicationScoped
public class UtilBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private static GoogleClientSecrets clientSecrets;
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final Logger LOG = Logger.getLogger(UtilBean.class.getName());
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
    InputStream stream = classLoader.getResourceAsStream("/production/google/client_secrets.json");
    InputStreamReader reader = new InputStreamReader(stream);
    try {
      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, ex.getMessage());
    }
  }

  public GoogleTokenResponse convertCodeToToken(String code, String redirectUri) throws IOException {
    return new GoogleAuthorizationCodeTokenRequest(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      clientSecrets.getDetails().getClientId(),
      clientSecrets.getDetails().getClientSecret(),
      code,
      redirectUri
    ).execute();
  }

  public AuthorizationCodeFlow getFlow() {
    return new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
    ).build();
  }
}
