package com.ucb.proyecto;

import com.ucb.proyecto.analysis.application.AnalysisPort;
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
        
        List<Vulnerability> vulnerabilidades = analysisPort.scanProject("proyecto-prueba");
        
        System.out.println("Total de vulnerabilidades encontradas: " + vulnerabilidades.size());
        
        for (Vulnerability v : vulnerabilidades) {
            System.out.println("- Regla: " + v.getRuleKey());
            System.out.println("  Mensaje: " + v.getMessage());
            System.out.println("  Archivo: " + v.getComponent() + " (Línea: " + v.getLine() + ")");
            System.out.println("  Severidad: " + v.getSeverity());
            System.out.println("-----------------------------------------");
        }
        
        System.out.println("Escaneo finalizado exitosamente.");
        System.out.println("=========================================");
    }
}