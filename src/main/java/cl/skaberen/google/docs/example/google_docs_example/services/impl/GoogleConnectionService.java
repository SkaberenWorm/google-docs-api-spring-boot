package cl.skaberen.google.docs.example.google_docs_example.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.apachecommons.CommonsLog;

import cl.skaberen.google.docs.example.google_docs_example.services.IGoogleConnectionService;

@Service
@CommonsLog
public class GoogleConnectionService implements IGoogleConnectionService {

	@Value(value = "classpath:google/credentials.json")
	private Resource googleCredentials;

	private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final String DOCUMENT_ID = "195j9eDD3ccgjQRttHhJPymLJUCOUjs-jmwTrekvdjFE";

	private static final String CLIENT_SECRETS = "/client_secrets.json";

	private static GoogleClientSecrets clientSecrets;

	private String authorizationCode;

	private Credential credential;

	private String urlSource;

	private InputStream getSecretFile() throws IOException {
		return googleCredentials.getInputStream();
	}

	public GoogleConnectionService() {
		System.out.println("google connection");
	}

	@Override
	public GoogleClientSecrets getClientSecrets() {
		if (clientSecrets == null) {
			try {
				// load client secrets
				InputStreamReader clientSecretsReader = new InputStreamReader(getSecretFile());
				clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretsReader);
				log.info("clientSecrets");
				log.info(clientSecrets);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return clientSecrets;
	}

	@Override
	public Credential getCredentials() {
		return credential;
	}

	/**
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public boolean exchangeCode(String code) {
		this.authorizationCode = code;

		// Step 2: Exchange --> exchange code for tokens
		boolean result = false;
		String callbackUri = clientSecrets.getDetails().getRedirectUris().get(0);
		GoogleTokenResponse response;
		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			response = new GoogleAuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY,
					clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(), code,
					callbackUri).execute();

			// Build a new GoogleCredential instance and return it.
			credential = new GoogleCredential.Builder().setClientSecrets(clientSecrets).setJsonFactory(JSON_FACTORY)
					.setTransport(HTTP_TRANSPORT).build().setAccessToken(response.getAccessToken())
					.setRefreshToken(response.getRefreshToken());
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// End of Step 2 <--
		return result;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	@Override
	public String getRedirectUrl() {
		if (clientSecrets != null) {
			return clientSecrets.getDetails().getRedirectUris().get(0);
		}
		return null;
	}

	@Override
	public String getSourceUrl() {
		return this.urlSource;
	}

	@Override
	public void setSourceUrl(String sourceUrl) {
		this.urlSource = sourceUrl;
	}
}
