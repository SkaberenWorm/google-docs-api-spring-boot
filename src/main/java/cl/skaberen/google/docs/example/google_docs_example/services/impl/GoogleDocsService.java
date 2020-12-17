package cl.skaberen.google.docs.example.google_docs_example.services.impl;

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

    @Override
    public String getTitleDoc(GoogleConnectionService connection, String documentId) {
        try {
            // Build a new authorized API client service.
            Docs service = new Docs.Builder(Global.HTTP_TRANSPORT, Global.JSON_FACTORY, connection.getCredentials())
                    .setApplicationName(Global.APPLICATION_NAME).build();

            Document response = service.documents().get(documentId).execute();
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
            Docs service = new Docs.Builder(Global.HTTP_TRANSPORT, Global.JSON_FACTORY, connection.getCredentials())
                    .setApplicationName(Global.APPLICATION_NAME).build();
            Document doc = new Document().setTitle("My Document");
            doc = service.documents().create(doc).execute();
            System.out.println("Created document with title: " + doc.getTitle());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
