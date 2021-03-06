package org.pragmaticindustries.webinar.plc4x;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Simple Demo how to use PLC4X.
 *
 * @author julian
 * Created by julian on 01.04.20
 */
public class SimpleDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDemo.class);

    public static void main(String[] args) {
        PlcDriverManager driverManager = new PlcDriverManager();

        try (PlcConnection connection = driverManager.getConnection("s7://192.168.167.210/1/1")) {
            logger.info("Verbindung ist aufgebaut...");

            // Send Read Request
            CompletableFuture<? extends PlcReadResponse> requestFuture = connection.readRequestBuilder()
                .addItem("field to read", "%DB444.DBW0:INT")
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
