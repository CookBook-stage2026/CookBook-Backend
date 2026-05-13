package be.xplore.cookbook.ai.config;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class McpConfig {

    @Bean(destroyMethod = "close")
    public McpClient firecrawlMcpClient(@Value("${firecrawl.api-key}") String apiKey) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        List<String> command = isWindows
                ? List.of("cmd", "/c", "npx", "-y", "firecrawl-mcp")
                : List.of("npx", "-y", "firecrawl-mcp");

        StdioMcpTransport transport = new StdioMcpTransport.Builder()
                .command(command)
                .environment(Map.of("FIRECRAWL_API_KEY", apiKey))
                .logEvents(true)
                .build();

        return new DefaultMcpClient.Builder()
                .key("firecrawl")
                .transport(transport)
                .build();
    }

    @Bean
    public ToolProvider mcpToolProvider(McpClient mcpClient) {
        return _ -> {
            ToolProviderResult.Builder builder = ToolProviderResult.builder();

            mcpClient.listTools().stream()
                    .filter(tool -> tool.name().equals("firecrawl_scrape"))
                    .forEach(tool -> builder.add(
                            tool,
                            (req, _) -> String.valueOf(mcpClient.executeTool(req))
                    ));

            return builder.build();
        };
    }
}
