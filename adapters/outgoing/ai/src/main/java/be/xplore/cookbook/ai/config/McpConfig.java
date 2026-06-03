package be.xplore.cookbook.ai.config;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(FirecrawlProperties.class)
public class McpConfig {
    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "firecrawl.enabled", havingValue = "true")
    public McpClient firecrawlMcpClient(FirecrawlProperties properties) {
        StreamableHttpMcpTransport transport = new StreamableHttpMcpTransport.Builder()
                .url(properties.mcpUrl())
                .customHeaders(Map.of("Authorization", "Bearer " + properties.apiKey()))
                .build();

        return new DefaultMcpClient.Builder()
                .key("firecrawl")
                .transport(transport)
                .build();
    }

    @Bean
    public ToolProvider mcpToolProvider(Optional<McpClient> mcpClient) {
        return _ -> {
            if (mcpClient.isEmpty()) {
                return ToolProviderResult.builder().build();
            }

            ToolProviderResult.Builder builder = ToolProviderResult.builder();
            mcpClient.get().listTools().forEach(tool ->
                    builder.add(tool, (req, _) -> String.valueOf(mcpClient.get().executeTool(req)))
            );

            return builder.build();
        };
    }
}
