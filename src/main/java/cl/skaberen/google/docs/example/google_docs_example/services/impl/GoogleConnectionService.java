package cl.skaberen.google.docs.example.google_docs_example.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.apachecommons.CommonsLog;

import cl.skaberen.google.docs.example.google_docs_example.Utils.Global;
import cl.skaberen.google.docs.example.google_docs_example.services.IGoogleConnectionService;

@Service
@CommonsLog
public class GoogleConnectionService implements IGoogleConnectionService {

	@Value(value = "classpath:google/credentials.json")
	private Resource googleCredentials;

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
				clientSecrets = GoogleClientSecrets.load(Global.JSON_FACTORY, clientSecretsReader);
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
			response = new GoogleAuthorizationCodeTokenRequest(HTTP_TRANSPORT, Global.JSON_FACTORY,
					clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(), code,
					callbackUri).execute();

			// Build a new GoogleCredential instance and return it.
			credential = new GoogleCredential.Builder().setClientSecrets(clientSecrets)
					.setJsonFactory(Global.JSON_FACTORY).setTransport(HTTP_TRANSPORT).build()
					.setAccessToken(response.getAccessToken()).setRefreshToken(response.getRefreshToken());

			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// End of Step 2 <--
		return result;
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Resource googleCredentials)
			throws IOException {
		// Load client secrets.
		InputStream in = googleCredentials.getInputStream();
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Global.JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, Global.JSON_FACTORY,
				clientSecrets, Global.SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(Global.TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8087)
				.setCallbackPath("/google/oauth2callback").build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
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
