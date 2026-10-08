# 🛡️ Sistema SAST Impulsado por IA y Refactorización Automática de Código

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-Community-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)
![Hugging Face](https://img.shields.io/badge/Hugging_Face-Qwen2.5--Coder-FFD21E?style=for-the-badge&logo=huggingface&logoColor=black)
![Architecture](https://img.shields.io/badge/Arquitectura-Hexagonal-blueviolet?style=for-the-badge)

## 📋 Descripción General

**Sistema-SAST-LLM** es un motor de seguridad automatizado bajo el paradigma **Shift-Left** y **DevSecOps**. Su objetivo principal es cerrar la brecha entre la **detección de vulnerabilidades** mediante Análisis Estático de Seguridad de Aplicaciones (SAST) y su **remediación efectiva** en el código fuente utilizando Modelos de Lenguaje de Gran Escala (LLMs).

El sistema orquesta de manera autónoma el escaneo estático con **SonarQube**, evalúa y filtra los hallazgos bajo el estándar **CVSS v3.1**, extrae quirúrgicamente el contexto físico del archivo local afectado y emplea técnicas avanzadas de **Ingeniería de Prompts Estructurada (*Graph Context*)** para generar parches de código seguro listos para compilar a través de **Qwen2.5-Coder** (vía Hugging Face API).

---

## 🏗️ Arquitectura del Sistema

El proyecto está construido como un **Monolito Modular** siguiendo estrictamente los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)**. Esto garantiza el desacoplamiento total entre las reglas de negocio de ciberseguridad (Dominio) y las herramientas tecnológicas externas (SonarQube, Sistema de Archivos Local y API de Inferencia LLM).

<!-- Arrastra y suelta aquí tu imagen de Diseño de la Arquitectura (Monolítico Modular) en GitHub -->
<img width="990" height="665" alt="image" src="https://github.com/user-attachments/assets/4c53ee3f-ebde-4de3-90dd-5172f5fce7de" />


### Flujo de Análisis Estático y Clasificación de Criticidad

El mecanismo de análisis estático y evaluación opera mediante contratos de interfaz (`AnalysisPort`), delegando la comunicación HTTP REST al adaptador de infraestructura (`SonarQubeAdapter`) y manteniendo el mapeo y filtrado CVSS dentro del núcleo de dominio (`CriticalityEvaluator` y `Vulnerability`):

<!-- Arrastra y suelta aquí tu segunda imagen (Implementación del Mecanismo de Análisis Estático) en GitHub -->
<img width="986" height="369" alt="image" src="https://github.com/user-attachments/assets/28594974-76e9-4b3d-a402-e7c313f0877c" />


---

## ✨ Características Principales

* **🚀 Orquestación SAST Automatizada (Shift-Left):** Ejecución transparente del escáner de Maven/SonarQube mediante subprocesos del sistema sincronizados con el *Compute Engine* asíncrono del servidor.
* **⚖️ Evaluador de Criticidad Basado en CVSS v3.1:** Mapeo y filtrado en la capa de dominio para priorizar vulnerabilidades críticas (ej. **OWASP Top 10 / CWE-611: Inyección de Entidades Externas XML - XXE**).
* **🔍 Extracción Quirúrgica de Contexto Local:** Resolución de la clave del componente reportado por SonarQube hacia el sistema de archivos local, aislando únicamente el bloque de código circundante a la línea vulnerable con su numeración exacta.
* **🧠 Ingeniería de Prompts con *Graph Context*:** Construcción de prompts basados en estructuras jerárquicas clave-valor (`VulnerabilityNode`, `CodeNode`, `SourceCode`) acompañadas de restricciones estrictas de salida (`OutputConstraint`), logrando respuestas deterministas y reduciendo el consumo de tokens.
* **💰 Optimización "Zero-Token Waste":** Filtrado de estado activo (`statuses=OPEN`) en las consultas a la API de SonarQube. Si las vulnerabilidades ya fueron mitigadas o el proyecto está limpio, el sistema omite por completo las llamadas a la API del LLM.

---

## 📂 Estructura del Proyecto

```text
├── src/main/java/com/ucb/proyecto
│   ├── ProyectoApplication.java              # Orquestador CLI (Shift-Left Pipeline)
│   ├── analysis
│   │   ├── application
│   │   │   └── AnalysisPort.java             # Puerto de comunicación para SAST
│   │   ├── domain
│   │   │   ├── CriticalityEvaluator.java     # Clasificador de severidad y CVSS v3.1
│   │   │   └── Vulnerability.java            # Entidad de dominio de vulnerabilidad
│   │   └── infrastructure
│   │       ├── LocalCodeExtractor.java       # Extractor de fragmentos de código local
│   │       └── SonarQubeAdapter.java         # Adaptador REST para la API de SonarQube
│   ├── llm
│   │   ├── application
│   │   │   ├── LlmPort.java                  # Contrato de comunicación con IA
│   │   │   └── RefactoringService.java       # Constructor de Graph Context Prompt
│   │   └── infrastructure
│   │       └── HuggingFaceAdapter.java       # Cliente HTTP para Hugging Face API
│   └── targetapp
│       └── VulnerableUserController.java     # Entorno de pruebas (Caso de estudio CWE-611)
└── src/main/resources
    ├── application.properties                # Configuraciones generales
    └── application-secret.properties         # Credenciales y Tokens (Ignorado en Git)
```

## ⚙️ Requisitos y Configuración
Requisitos del entorno:

* Java JDK: 17 o superior.
* Maven: 3.8+ (o mediante el wrapper ./mvnw incluido).
* Docker: Instancia activa de sonarqube:community en el puerto 9000.
* Tokens de acceso: User Token de SonarQube y API Token de Hugging Face configurados en src/main/resources/application-secret.properties.
