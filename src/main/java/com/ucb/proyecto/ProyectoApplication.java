package com.ucb.proyecto;

import com.ucb.proyecto.analysis.application.AnalysisPort;
import com.ucb.proyecto.analysis.domain.CriticalityEvaluator;
import com.ucb.proyecto.analysis.domain.Vulnerability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ProyectoApplication implements CommandLineRunner {

    @Autowired
    private AnalysisPort analysisPort;

    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("Iniciando escaneo estático en SonarQube...");

        List<Vulnerability> allIssues = analysisPort.scanProject("proyecto");

        CriticalityEvaluator evaluator = new CriticalityEvaluator();
        List<Vulnerability> criticalVulnerabilities = evaluator.filterCriticalVulnerabilities(allIssues);

        System.out.println("Total de vulnerabilidades CRITICAS encontradas: " + criticalVulnerabilities.size());

        for (Vulnerability v : criticalVulnerabilities) {
            System.out.println("- [" + v.getSeverity() + " | CVSS: " + v.getCvssScore() + "] " + v.getRuleKey() + " en línea " + v.getLine());
            System.out.println("  Detalle: " + v.getMessage());
        }
        System.out.println("=========================================");
    }
}