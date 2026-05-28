package com.ucb.proyecto.analysis.domain;

import java.util.List;
import java.util.stream.Collectors;

public class CriticalityEvaluator {

    public List<Vulnerability> filterCriticalVulnerabilities(List<Vulnerability> allIssues) {
        return allIssues.stream()
                .filter(v -> isHighOrCritical(v.getSeverity()))
                .map(this::assignCvssScore)
                .collect(Collectors.toList());
    }

    private boolean isHighOrCritical(String severity) {
        if (severity == null || severity.trim().isEmpty()) {
            return false;
        }
        
        String upperSeverity = severity.toUpperCase();
        return upperSeverity.equals("HIGH") || 
               upperSeverity.equals("CRITICAL") || 
               upperSeverity.equals("BLOCKER");
    }

    private Vulnerability assignCvssScore(Vulnerability vulnerability) {
        String severity = vulnerability.getSeverity().toUpperCase();
        
        if (severity.equals("CRITICAL") || severity.equals("BLOCKER")) {
            vulnerability.setCvssScore(9.5);
        } else if (severity.equals("HIGH")) {
            vulnerability.setCvssScore(8.0);
        }
        
        return vulnerability;
    }
}