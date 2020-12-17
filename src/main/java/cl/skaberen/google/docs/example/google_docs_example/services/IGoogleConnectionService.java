package cl.skaberen.google.docs.example.google_docs_example.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

public interface IGoogleConnectionService {

	GoogleClientSecrets getClientSecrets();

	Credential getCredentials();

	boolean exchangeCode(String code);

	String getRedirectUrl();

	String getSourceUrl();

	void setSourceUrl(String sourceUrl);
}
