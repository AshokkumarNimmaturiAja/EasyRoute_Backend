package com.logistics.platform.controller;

import com.logistics.platform.service.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*") 
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return aiService.getChatResponse(message, email);
    }
}
