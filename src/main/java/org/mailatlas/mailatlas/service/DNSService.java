package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.mailatlas.mailatlas.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

@Slf4j
@Service
public class DNSService {
    private final String BASE_URL = "https://dns-lookup.com";
    private final String ENDPOINT = "/api/dns";

    @Autowired
    private SMTPServerService smtpServerService;

    public UserData getData(@Email String email) {
        try {
            ArrayList<Map<String, Object>> MXrecords = this.getMXRecords(email);
            if (MXrecords.isEmpty()) {
                return null;
            }
            // will be storing user data
            UserData userData = new UserData();
            // sort by priority
            MXrecords.sort(Comparator.comparingInt(r -> (Integer) r.get("priority")));
            // try to connect to each server
            for (Map<String, Object> record : MXrecords) {
                String server = record.get("exchange").toString();
                log.info("here is the server: " + server);
                smtpServerService.setServer(server);
                boolean isConnected = smtpServerService.connect();
                if (!isConnected) {
                    continue;
                }
                boolean isVerified = smtpServerService.verifyEmail(extractDomain(email), email);
                // TODO: find values for first/last name and other data
                userData.setEmail(email);
                userData.setFirstName(null);
                userData.setLastName(null);
                userData.setDateOfBirth(null);
                userData.setGender(null);
                userData.setIsVerified(isVerified);

                return userData;
            }
            return null;
        }
        catch (Exception e) {
            log.error("Something went wrong while getting user data: " + e.getMessage());
            return null;
        }
        finally {
            smtpServerService.disconnect();
        }
    }

    private ArrayList<Map<String, Object>> getMXRecords(@Email String email) {
        try {
            String domain = extractDomain(email);
            RestClient client = RestClient.builder().baseUrl(BASE_URL).build();
            Map<String, Object> body = client
                    .get()
                    .uri(ENDPOINT + "?" + "domain=" + domain + "&types=MX")
                    .retrieve()
                    .body(Map.class);

            // get records [{}, {}, {}]
            ArrayList<Map<String, Object>> records = (ArrayList<Map<String, Object>>) body.get("records");
            // will be storing extracted data from each record
            ArrayList<Map<String, Object>> dataRecords = new ArrayList<>();

            for (Map<String, Object> record : records) {
                dataRecords.add((Map<String, Object>) record.get("data"));
            }
            log.info("received MX records for " + domain + ": " + records);
            return dataRecords;
        }
        catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String extractDomain(@Email String email) throws Exception {
        // get the index of @
        int index = email.indexOf("@");
        if (index == -1) {
            throw new Exception("Could not extract '@' from email. Necessary to get a domain name.");
        }
        // get the domain name
        String domain = email.substring(index + 1);
        if (domain.isEmpty()) {
            throw new Exception("Could not extract the domain name. Check if indexes are valid");
        }
        return domain;
    }
}