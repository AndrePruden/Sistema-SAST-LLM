package com.ucb.proyecto.analysis.infrastructure;

import com.ucb.proyecto.analysis.application.AnalysisPort;
import com.ucb.proyecto.analysis.domain.Vulnerability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class SonarQubeAdapter implements AnalysisPort{
    private final RestTemplate restTemplate;

    @Value("${sonar.url}")
    private String sonarQubeUrl;

    @Value("${sonar.token}")
    private String sonarToken;

    public SonarQubeAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<Vulnerability> scanProject(String projectKey) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        String url = sonarQubeUrl + "/api/issues/search?componentKeys=" + projectKey + "&types=VULNERABILITY";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonarToken, ""); 
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode issues = root.path("issues");

            for (JsonNode issue : issues) {
                vulnerabilities.add(Vulnerability.builder()
                        .id(issue.path("key").asText())
                        .ruleKey(issue.path("rule").asText())
                        .message(issue.path("message").asText())
                        .component(issue.path("component").asText())
                        .line(issue.path("line").asInt(-1))
                        .severity(issue.path("severity").asText())
                        .build());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return vulnerabilities;
    }
}
