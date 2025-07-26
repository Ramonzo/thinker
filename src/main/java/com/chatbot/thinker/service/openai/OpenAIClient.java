package com.chatbot.thinker.service.openai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.StreamHandler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chatbot.thinker.service.openai.dto.request.CreateRunRequestDto;
import com.chatbot.thinker.service.openai.dto.request.CreateThreadAndRunRequestDto;
import com.chatbot.thinker.service.openai.dto.request.CreateThreadRequestDto;
import com.chatbot.thinker.service.openai.dto.request.RetrieveThreadRequestDto;
import com.chatbot.thinker.service.openai.dto.response.CreateThreadResponseDto;
import com.chatbot.thinker.service.openai.dto.response.RetrieveThreadResponseDto;
import com.chatbot.thinker.service.openai.interfaces.CreateRun;
import com.chatbot.thinker.service.openai.interfaces.ThreadRun;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenAIClient {

    private static WebSocketSession _session;
    private static String _apiKey = new String();
    private static String _assistantId = new String("");
    private final HttpClient _httpClient;
    private final ObjectMapper _objectMapper;

    public OpenAIClient(WebSocketSession session, String apiKey) {
        _session = session;
        _apiKey = apiKey;

        _httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        _objectMapper = new ObjectMapper();
    }

    public void sendSession(String message) throws IOException {
        _session.sendMessage(new TextMessage(message));
    }

    public CreateThreadResponseDto createThread(CreateThreadRequestDto requestDto)
            throws IOException, InterruptedException {
        String requestBodyJson = _objectMapper.writeValueAsString(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/threads"))
                .header("Authorization", "Bearer " + _apiKey)
                .header("Content-Type", "application/json")
                .header("OpenAI-Beta", "assistants=v2")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return _objectMapper.readValue(response.body(), CreateThreadResponseDto.class);
    }

    public RetrieveThreadResponseDto retrieveThread(RetrieveThreadRequestDto requestDto)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/threads/" + requestDto.id))
                .header("Authorization", "Bearer " + _apiKey)
                .header("Content-Type", "application/json")
                .header("OpenAI-Beta", "assistants=v2")
                .GET()
                .build();

        HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return _objectMapper.readValue(response.body(), RetrieveThreadResponseDto.class);
    }

    /**
     * Método para consumir a rota Create Run com stream habilitado.
     *
     * @param requestDTO    Objeto com os parâmetros da requisição.
     * @param streamHandler Função para processar cada fragmento de dados do stream.
     * @throws OpenAIException Caso ocorra algum erro na requisição.
     */
    public void createRunWithStream(CreateRunRequestDto requestDTO, StreamHandler streamHandler)
            throws OpenAIException {
        try {

            CreateRun createRunBody = new CreateRun();
            createRunBody.assistant_id = requestDTO.assistant_id;
            createRunBody.stream = true;

            String requestBodyJson = _objectMapper.writeValueAsString(createRunBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/threads/" + requestDTO.thread_id + "/runs"))
                    .header("Authorization", "Bearer " + _apiKey)
                    .header("Content-Type", "application/json")
                    .header("OpenAI-Beta", "assistants=v2")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenApply(HttpResponse::body)
                    .thenAccept(lines -> lines.forEach(line -> {
                        if (!line.isBlank() && line.contains("data:") && !line.contains("[DONE]")) {
                            try {
                                var objectString = line.replace("data: ", "").trim();
                                ThreadRun newThread = _objectMapper.readValue(objectString, ThreadRun.class);

                                streamHandler.handle(newThread);
                            } catch (Exception e) {
                                System.err.println("Erro ao processar linha do stream: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }))
                    .exceptionally(e -> {
                        throw new RuntimeException("Erro ao processar stream da API OpenAI", e);
                    });
        } catch (IOException e) {
            throw new OpenAIException("Erro ao serializar o corpo da requisição para JSON", e);
        }
    }

    public void createThreadAndRunWithStream(CreateThreadAndRunRequestDto requestDTO, StreamHandler streamHandler)
            throws OpenAIException {
        try {
            CreateRun createRunBody = new CreateRun();
            createRunBody.assistant_id = requestDTO.assistant_id;
            createRunBody.stream = true;
            createRunBody.thread = new CreateRun.Thread();
            createRunBody.thread.messages = requestDTO.messages;

            String requestBodyJson = _objectMapper.writeValueAsString(createRunBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/threads/runs"))
                    .header("Authorization", "Bearer " + _apiKey)
                    .header("Content-Type", "application/json")
                    .header("OpenAI-Beta", "assistants=v2")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenApply(HttpResponse::body)
                    .thenAccept(lines -> {
                        lines.forEach(line -> {
                            if (!line.isBlank() && line.contains("data:") && !line.contains("[DONE]")) {
                                try {
                                    var objectString = line.replace("data: ", "").trim();
                                    ThreadRun newThread = _objectMapper.readValue(objectString, ThreadRun.class);

                                    streamHandler.handle(newThread);
                                } catch (Exception e) {
                                    System.err.println("Erro ao processar linha do stream: " + line);
                                    e.printStackTrace();
                                }
                            }
                        });
                    })
                    .exceptionally(e -> {
                        throw new RuntimeException("Erro ao processar stream da API OpenAI", e);
                    });
        } catch (IOException e) {
            throw new OpenAIException("Erro ao serializar o corpo da requisição para JSON", e);
        }
    }

    /**
     * Interface para lidar com callbacks do stream.
     */
    @FunctionalInterface
    public interface StreamHandler {
        void handle(ThreadRun thread) throws Exception;
    }
}
