package be.xplore.cookbook.ai.dto;

import java.util.List;

public record OllamaRequest(
        String model,
        List<Message> messages,
        boolean stream
) {
    public record Message(String role, String content) {
    }
}
