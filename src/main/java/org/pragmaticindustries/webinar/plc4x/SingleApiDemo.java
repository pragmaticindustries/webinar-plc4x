package org.pragmaticindustries.webinar.plc4x;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Simple Demo how to read from either Siemens S7 or OPC UA Server without changing code (Single API).
 *
 * @author julian
 * Created by julian on 01.04.20
 */
public class SingleApiDemo {

    private static final Logger logger = LoggerFactory.getLogger(SingleApiDemo.class);

    public static void main(String[] args) {
        PlcDriverManager driverManager = new PlcDriverManager();

        if (args.length != 2) {
            throw new IllegalArgumentException("Bitte genau zwei Argumente angeben, PLC Adresse und PLC Feld!");
        }

        String url = args[0];
        String field = args[1];

        try (PlcConnection connection = driverManager.getConnection(url)) {
            logger.info("Verbindung ist aufgebaut...");

            // Send Read Request
            CompletableFuture<? extends PlcReadResponse> requestFuture = connection.readRequestBuilder()
                .addItem("field to read", field)
                .build()
                .execute();
            logger.info("Anfrage gesendet...");

            PlcReadResponse response = requestFuture.get();
            logger.info("Antwort erhalten...");

            // Inspect response
            logger.info("Status der Antwort ist {}", response.getResponseCode("field to read"));
            logger.info("Wert der Antwort ist {}", response.getInteger("field to read"));
        } catch (Exception e) {
            logger.error("Konnte keine Verbindung aufbauen", e);
        }
    }
}
