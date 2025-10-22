package com.ade.labnetai;

// import org.springframework.ai.embedding.EmbeddingModel;
// import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LabnetaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabnetaiApplication.class, args);
	}

	// @Bean
	// public EmbeddingModel embeddingModel() {
    // Can be any other EmbeddingModel implementation.
	// return new OllamaEmbeddingModel(null, null, null, null);
    // }
}