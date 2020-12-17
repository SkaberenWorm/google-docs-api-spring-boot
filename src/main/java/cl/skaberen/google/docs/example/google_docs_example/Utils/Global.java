package cl.skaberen.google.docs.example.google_docs_example.Utils;

import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.DocsScopes;

public class Global {

	public static final String APPLICATION_NAME = "Skaberen Google Docs API - Sring boot";
	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public static final String TOKENS_DIRECTORY_PATH = "tokens";
	public static final List<String> SCOPES = Arrays.asList(DocsScopes.DOCUMENTS);
	/**
	 * Documento de ejemplo
	 */
	public static final String DOCUMENT_ID = "11ahHIXgOzo-4XK-ya7xc6AWXv3jX0b1VtImGeBGfLlI";

	public static HttpTransport HTTP_TRANSPORT;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

}
