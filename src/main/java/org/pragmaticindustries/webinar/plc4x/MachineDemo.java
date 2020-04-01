package org.pragmaticindustries.webinar.plc4x;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Simple Demo how to use PLC4X.
 *
 * @author julian
 * Created by julian on 01.04.20
 */
public class MachineDemo {

    private static final Logger logger = LoggerFactory.getLogger(MachineDemo.class);

    public static void main(String[] args) {
        PlcDriverManager driverManager = new PlcDriverManager();

        try (PlcConnection connection = driverManager.getConnection("s7://192.168.167.210/1/1")) {
            logger.info("Verbindung ist aufgebaut...");

            // Send Read Request
            CompletableFuture<? extends PlcReadResponse> requestFuture = connection.readRequestBuilder()
                .addItem("position", "%DB444:0.0:REAL")
                .addItem("rand_val", "%DB444:4.0:REAL")
                .addItem("motor-current", "%DB444:8.0:REAL")
                .addItem("not-existing-field", "%DB555:8.0:REAL")
                .build()
                .execute();
            logger.info("Anfrage gesendet...");

            PlcReadResponse response = requestFuture.get();
            logger.info("Antwort erhalten...");

            // Inspect response
            logger.info("Antwort der Steuerung:");
            for (String fieldName : response.getFieldNames()) {
                logger.info("{} - {}", fieldName, response.getResponseCode(fieldName));
                if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                    logger.info("{} - {}", fieldName, response.getObject(fieldName));
                }
            }
        } catch (Exception e) {
            logger.error("Konnte keine Verbindung aufbauen", e);
        }
    }
}
