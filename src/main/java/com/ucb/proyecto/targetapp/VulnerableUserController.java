package com.ucb.proyecto.targetapp;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VulnerableUserController {
    @PostMapping("/api/v1/xml/parse")
    public String parseXml(@RequestBody String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            
            return "XML parseado exitosamente: " + doc.getDocumentElement().getNodeName();
        } catch (Exception e) {
            return "Error parseando XML";
        }
    }
}