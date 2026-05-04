package be.xplore.cookbook.ai.dto;

public record OllamaResponse(
        Message message,
        boolean done
) {
    public record Message(String role, String content) {
    }
}
