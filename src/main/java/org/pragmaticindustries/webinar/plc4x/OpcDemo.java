package org.pragmaticindustries.webinar.plc4x;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Simple Demo how to read OPC UA values.
 *
 * @author julian
 * Created by julian on 01.04.20
 */
public class OpcDemo {

    private static final Logger logger = LoggerFactory.getLogger(OpcDemo.class);

    public static void main(String[] args) {
        PlcDriverManager driverManager = new PlcDriverManager();

        try (PlcConnection connection = driverManager.getConnection("opcua:tcp://10.8.0.2:53530/opcua/SimulationServer?discovery=false&username=tester&password=test1234")) {
            logger.info("Verbindung ist aufgebaut...");

            // Send Read Request
            CompletableFuture<? extends PlcReadResponse> requestFuture = connection.readRequestBuilder()
                .addItem("Counter", "ns=5;s=Counter1")
                .addItem("Sinus", "ns=5;s=Sinusoid1")
                .addItem("Sägezahn", "ns=5;s=Sawtooth1")
                .addItem("Dreieck", "ns=5;s=Triangle1")
                .addItem("not-existing-field", "ns=5;s=Triangle2")
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
