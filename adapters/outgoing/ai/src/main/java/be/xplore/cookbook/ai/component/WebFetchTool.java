package be.xplore.cookbook.ai.component;

import be.xplore.cookbook.core.domain.exception.RecipeImportException;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WebFetchTool {

    private static final String USER_AGENT = "Mozilla/5.0 (compatible; CookbookBot/1.0)";

    private final RestClient restClient;

    public WebFetchTool() {
        this.restClient = RestClient.builder()
                .baseUrl("google.com")
                .build();
    }

    @Tool("Fetches the raw HTML content of a web page given its URL.")
    public String fetchPage(String url) {
        try {
            return restClient.get()
                    .uri(url)
                    .header("User-Agent", USER_AGENT)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new RecipeImportException("Failed to fetch page: " + url, e);
        }
    }
}
