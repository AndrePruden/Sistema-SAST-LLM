package com.ucb.proyecto.llm.application;

public interface LlmPort {
    String generateCodeFix(String prompt);
}