package com.ucb.proyecto;

import com.ucb.proyecto.analysis.application.AnalysisPort;
import com.ucb.proyecto.analysis.domain.CriticalityEvaluator;
import com.ucb.proyecto.analysis.domain.Vulnerability;
import com.ucb.proyecto.analysis.infrastructure.LocalCodeExtractor;
import com.ucb.proyecto.llm.application.LlmPort;
import com.ucb.proyecto.llm.application.RefactoringService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ProyectoApplication implements CommandLineRunner {

    private final AnalysisPort analysisPort;
    private final LlmPort llmPort;
    private final LocalCodeExtractor codeExtractor;
    private final RefactoringService refactoringService;

    @Value("${sonar.token}")
    private String sonarToken;

    public ProyectoApplication(AnalysisPort analysisPort, LlmPort llmPort, LocalCodeExtractor codeExtractor, RefactoringService refactoringService) {
        this.analysisPort = analysisPort;
        this.llmPort = llmPort;
        this.codeExtractor = codeExtractor;
        this.refactoringService = refactoringService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("Iniciando escaner de SonarQube de forma automatizada (Shift-Left)...");
        
        
        ProcessBuilder pb = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            pb.command("cmd.exe", "/c", "mvnw.cmd sonar:sonar -Dsonar.token=" + sonarToken);
        } else {
            pb.command("bash", "-c", "./mvnw sonar:sonar -Dsonar.token=" + sonarToken);
        }
        pb.inheritIO(); 
        Process process = pb.start();
        process.waitFor();
        
        System.out.println("\nEscaneo finalizado. Esperando al motor asincrono de SonarQube...");
        
        Thread.sleep(5000); 

        System.out.println("Obteniendo telemetria...");

        List<Vulnerability> allIssues = analysisPort.scanProject("com.ucb:proyecto");
        CriticalityEvaluator evaluator = new CriticalityEvaluator();
        List<Vulnerability> criticalVulnerabilities = evaluator.filterCriticalVulnerabilities(allIssues);

        // 3. Filtro Estricto: Solo dejar pasar el CWE-611 (Regla java:S2755 de SonarQube)
        List<Vulnerability> targetVulnerabilities = criticalVulnerabilities.stream()
            .filter(v -> v.getRuleKey() != null && v.getRuleKey().contains("S2755"))
            .toList();

        System.out.println("Total de vulnerabilidades objetivo (CWE-611) encontradas: " + targetVulnerabilities.size());

        // 4. Extracción de contexto
        for (Vulnerability v : targetVulnerabilities) {
            System.out.println("- [" + v.getSeverity() + " | CVSS: " + v.getCvssScore() + "] " + v.getRuleKey());
            
            String snippet = codeExtractor.extractContext(v.getComponent(), v.getLine());
            v.setCodeSnippet(snippet);
            
            System.out.println("\n--- FRAGMENTO DE CODIGO EXTRAIDO ---");
            System.out.println(snippet);
            System.out.println("------------------------------------");

            System.out.println("\n[IA] Iniciando conexión con Hugging Face (Refactorizacion)");
            String refactoredCode = refactoringService.refactorVulnerability(v);
            
            System.out.println("\n========= CÓDIGO REFACTORIZADO =========");
            System.out.println(refactoredCode);
            System.out.println("=========================================\n");
        }

        // System.out.println("=========================================");
        // System.out.println("Iniciando prueba de conexion con Hugging Face (Qwen2.5-Coder)...");
        // System.out.println("Por favor espera, el modelo podria estar despertando (Cold Boot)...");

        // String promptDePrueba = "Escribe una clase en Java que imprima 'Hola Mundo'. Solo devuelve el codigo fuente, sin explicaciones.";
        // String respuestaLLM = llmPort.generateCodeFix(promptDePrueba);

        // System.out.println("Respuesta recibida del LLM:");
        // System.out.println(respuestaLLM);
        // System.out.println("=========================================");
    }
}