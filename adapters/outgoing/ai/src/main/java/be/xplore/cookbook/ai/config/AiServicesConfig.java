package be.xplore.cookbook.ai.config;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.context.annotation.Configuration;

@Configuration
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "selectedChatModel")
public class AiServicesConfig {
}
