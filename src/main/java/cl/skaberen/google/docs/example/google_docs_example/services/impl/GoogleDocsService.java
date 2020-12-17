package cl.skaberen.google.docs.example.google_docs_example.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.apachecommons.CommonsLog;

import cl.skaberen.google.docs.example.google_docs_example.Utils.Global;
import cl.skaberen.google.docs.example.google_docs_example.services.IGoogleDocsService;

@Service
@CommonsLog
public class GoogleDocsService implements IGoogleDocsService {

    @Value(value = "classpath:google/credentials.json")
    private Resource googleCredentials;

    @Value(value = "classpath:template_email/templateV3.html")
    private Resource template_emailV3;

    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String DOCUMENT_ID = "11ahHIXgOzo-4XK-ya7xc6AWXv3jX0b1VtImGeBGfLlI";

    @Override
    public String getTitleDoc(GoogleConnectionService connection) {
        try {
            // Build a new authorized API client service.
            Docs service = new Docs.Builder(Global.HTTP_TRANSPORT, JSON_FACTORY, connection.getCredentials())
                    .setApplicationName(APPLICATION_NAME).build();

            Document response = service.documents().get(DOCUMENT_ID).execute();
            log.info(response.getBody());
            String title = response.getTitle();

            System.out.printf("The title of the doc is: %s\n", title);
            return title;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void createDoc(GoogleConnectionService connection) {
        try {
            Docs service = new Docs.Builder(Global.HTTP_TRANSPORT, JSON_FACTORY, connection.getCredentials())
                    .setApplicationName(APPLICATION_NAME).build();
            Document doc = new Document().setTitle("My Document");
            doc = service.documents().create(doc).execute();
            System.out.println("Created document with title: " + doc.getTitle());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

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
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, Global.SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8087)
                .setCallbackPath("/google/oauth2callback").build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
