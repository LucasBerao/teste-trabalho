package com.inkids.service;

import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por se comunicar com a API do Google (Vertex AI)
 * para gerar imagens a partir de um prompt de texto.
 */
public class GeminiImageService {

    // --- Configuração para a API Vertex AI ---
    // Substitua pelos detalhes do seu projeto no Google Cloud.
    private static final String PROJECT_ID = "seu-gcp-project-id"; // Ex: "my-first-project"
    private static final String LOCATION = "us-central1"; // Região onde o modelo está hospedado
    private static final String PUBLISHER = "google";
    private static final String MODEL = "imagegeneration@005"; // Verifique o nome do modelo mais recente na documentação

    /**
     * Gera uma URL de imagem com base no prompt fornecido, usando o Vertex AI.
     * Se a chamada de API falhar, retorna uma URL de imagem de placeholder.
     *
     * @param prompt O texto a ser usado para gerar a imagem.
     * @return Uma URL de imagem (seja a imagem real em Base64 ou uma de placeholder).
     */
    public String generateImageUrl(String prompt) {
        System.out.println("Solicitando geração de imagem para o prompt: \"" + prompt + "\"");

        try {
            String endpoint = String.format("%s-aiplatform.googleapis.com:443", LOCATION);
            PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
                .setEndpoint(endpoint)
                .build();

            try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {
                EndpointName endpointName = EndpointName.ofProjectLocationPublisherModelName(PROJECT_ID, LOCATION, PUBLISHER, MODEL);

                // Monta o payload da requisição para a API
                String instanceJson = String.format("{\"prompt\": \"%s\", \"sampleCount\": 1}", prompt);
                Value.Builder instanceBuilder = Value.newBuilder();
                JsonFormat.parser().merge(instanceJson, instanceBuilder);
                List<Value> instances = new ArrayList<>();
                instances.add(instanceBuilder.build());

                // Chama a API do Vertex AI
                PredictResponse response = client.predict(endpointName, instances, Value.newBuilder().build());
                
                // Extrai a imagem em formato Base64 da resposta
                // O caminho exato pode variar dependendo da versão da API
                String base64Image = response.getPredictions(0)
                                             .getStructValue()
                                             .getFieldsMap()
                                             .get("bytesBase64Encoded")
                                             .getStringValue();
                
                System.out.println("Imagem gerada com sucesso!");
                // Retorna a imagem como uma Data URL, que pode ser usada diretamente no HTML/CSS
                return "data:image/png;base64," + base64Image;

            }
        } catch (IOException e) {
            // Este erro geralmente acontece se as credenciais do Google Cloud não estiverem configuradas
            System.err.println("Erro de E/S ao chamar a API Vertex AI. Verifique suas credenciais e configuração do projeto.");
            System.err.println("Mensagem: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao gerar imagem com Vertex AI: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }

        // --- Fallback ---
        // Se a API falhar (por exemplo, credenciais não configuradas), retorna uma imagem de placeholder.
        System.out.println("Usando imagem de placeholder como fallback.");
        return "https://via.placeholder.com/600x400.png?text=" + prompt.replace(" ", "+");
    }
}