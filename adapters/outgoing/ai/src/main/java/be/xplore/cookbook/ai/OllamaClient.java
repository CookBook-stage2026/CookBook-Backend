package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.OllamaRequest;
import be.xplore.cookbook.ai.dto.OllamaResponse;
import be.xplore.cookbook.core.domain.exception.AiConnectionException;
import be.xplore.cookbook.core.domain.exception.AiResponseParsingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final OllamaProperties properties;

    @Value("${ollama.connect-timeout-seconds}")
    private static final int CONNECT_TIMEOUT = 10;

    @Value("${ollama.read-timeout-seconds:60}")
    private static final int READ_TIMEOUT = 60;

    public OllamaClient(OllamaProperties properties) {
        this.properties = properties;

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT));

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public String chat(String prompt) {
        OllamaRequest request = new OllamaRequest(
                properties.model(),
                List.of(new OllamaRequest.Message("user", prompt)),
                false
        );

        try {
            OllamaResponse response = restClient.post()
                    .uri("/api/chat")
                    .body(request)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response == null || response.message() == null || response.message().content() == null) {
                throw new AiResponseParsingException("AI returned invalid response");
            }

            return response.message().content();
        } catch (AiResponseParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new AiConnectionException("AI service is unreachable", e);
        }
    }
}
