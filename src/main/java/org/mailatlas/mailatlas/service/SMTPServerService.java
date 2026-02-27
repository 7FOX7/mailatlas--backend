package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@Service
@Data
public class SMTPServerService {
    private static final int SMTP_PORT = 25;
    // timeout when connecting to a socket
    private static final int TIMEOUT_MS = 5000;

    private boolean isConnected;
    private String server;      // a server from a MX record
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public boolean connect() {
        try {
            socket = new Socket();
            // connect to a mail server
            socket.connect(new InetSocketAddress(server, SMTP_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            isConnected = socket.isConnected();
            log.info("Successfully connected to the mail server");
            return isConnected;
        }
        catch (Exception e) {
            log.error("Failed to connect to the mail server: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyEmail(String domain, @Email String email) {
        try {
            // greet the server
            sendCommand("EHLO " + domain);
            // verify the email
            String response = sendCommand("VRFY " + email);

            return response != null && response.startsWith("250");
        }
        catch (Exception e) {
            log.error("Failed to execute a command: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (writer != null) {
                sendCommand("QUIT");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        catch (Exception e) {
            log.warn("Error during disconnect: " + e.getMessage());
        }
        finally {
            isConnected = false;
        }
    }

    private String sendCommand(String command) throws Exception {
        writer.println(command);
        String response = reader.readLine();
        log.debug("CMD: {} | RESP: {}", command, response);
        return response;
    }
}