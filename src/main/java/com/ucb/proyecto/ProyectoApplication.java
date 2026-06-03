package com.ucb.proyecto;

import com.ucb.proyecto.analysis.application.AnalysisPort;
import com.ucb.proyecto.analysis.domain.CriticalityEvaluator;
import com.ucb.proyecto.analysis.domain.Vulnerability;
import com.ucb.proyecto.llm.application.LlmPort;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ProyectoApplication implements CommandLineRunner {

    private final AnalysisPort analysisPort;
    private final LlmPort llmPort;

    public ProyectoApplication(AnalysisPort analysisPort, LlmPort llmPort) {
        this.analysisPort = analysisPort;
        this.llmPort = llmPort;
    }

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
        System.out.println("=========================================");
        System.out.println("Iniciando prueba de conexion con Hugging Face (Qwen2.5-Coder)...");
        System.out.println("Por favor espera, el modelo podria estar despertando (Cold Boot)...");

        String promptDePrueba = "Escribe una clase en Java que imprima 'Hola Mundo'. Solo devuelve el codigo fuente, sin explicaciones.";
        String respuestaLLM = llmPort.generateCodeFix(promptDePrueba);

        System.out.println("Respuesta recibida del LLM:");
        System.out.println(respuestaLLM);
        System.out.println("=========================================");
    }
}