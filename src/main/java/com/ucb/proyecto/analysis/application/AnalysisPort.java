package com.ucb.proyecto.analysis.application;

import com.ucb.proyecto.analysis.domain.Vulnerability;
import java.util.List;

public interface AnalysisPort {
    List<Vulnerability> scanProject(String projectKey);
}
