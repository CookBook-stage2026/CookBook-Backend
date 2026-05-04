package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.OllamaRequest;
import be.xplore.cookbook.ai.dto.OllamaResponse;
import be.xplore.cookbook.core.domain.exception.AiClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final OllamaProperties properties;
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 60;

    public OllamaClient(OllamaProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT));
        factory.setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT));

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }

    public String chat(String prompt) {
        OllamaRequest request = new OllamaRequest(
                properties.model(),
                List.of(new OllamaRequest.Message("user", prompt)),
                false
        );

        OllamaResponse response = restClient.post()
                .uri("/api/chat")
                .body(request)
                .retrieve()
                .body(OllamaResponse.class);

        if (response == null || response.message() == null || response.message().content() == null) {
            throw new AiClientException("Ollama returned invalid response");
        }

        return response.message().content();
    }
}
