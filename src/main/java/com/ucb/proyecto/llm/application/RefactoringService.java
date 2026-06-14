package com.ucb.proyecto.llm.application;

import com.ucb.proyecto.analysis.domain.Vulnerability;
import org.springframework.stereotype.Service;

@Service
public class RefactoringService {

    private final LlmPort llmPort;

    public RefactoringService(LlmPort llmPort) {
        this.llmPort = llmPort;
    }

    public String refactorVulnerability(Vulnerability vulnerability) {
        String prompt = String.format(
            "Task: SecureCodeRefactoring\n" +
            "Language: Java/SpringBoot\n" +
            "OutputConstraint: RETURN ONLY RAW CODE. NO MARKDOWN (no ```java). NO EXPLANATIONS. NO GREETINGS.\n" +
            "---\n" +
            "GraphContext:\n" +
            "  VulnerabilityNode:\n" +
            "    RuleKey: %s\n" +
            "    Detail: \"%s\"\n" +
            "  CodeNode:\n" +
            "    TargetLine: %d\n" +
            "---\n" +
            "SourceCode:\n" +
            "%s\n" +
            "---\n" +
            "Action: Output the corrected SourceCode.",
            vulnerability.getRuleKey(),
            vulnerability.getMessage(),
            vulnerability.getLine(),
            vulnerability.getCodeSnippet()
        );

        System.out.println("Procesando prompt estructurado de " + prompt.length() + " caracteres...");
        
        // Enviamos el prompt al adaptador de Hugging Face
        return llmPort.generateCodeFix(prompt);
    }
}