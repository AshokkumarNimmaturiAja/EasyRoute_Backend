package com.logistics.platform.service;

import com.logistics.platform.dto.response.ShipmentResponse;
import com.logistics.platform.entity.User;
import com.logistics.platform.repository.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final ShipmentService shipmentService;

    public AiService(ChatClient.Builder chatClientBuilder, UserRepository userRepository, ShipmentService shipmentService) {
        this.chatClient = chatClientBuilder.build();
        this.userRepository = userRepository;
        this.shipmentService = shipmentService;
    }

    public String getChatResponse(String message, String email) {
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("You are an expert Logistics and Supply Chain Assistant. You help users with shipping, warehousing, inventory management, and transportation queries. Provide concise, professional, and accurate answers.");

        if (email != null) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                List<ShipmentResponse> shipments = shipmentService.getMyShipments(email, null);

                long pending = shipments.stream().filter(s -> s.getStatus().name().equals("PENDING")).count();
                long completed = shipments.stream().filter(s -> s.getStatus().name().equals("DELIVERED")).count();

                String recentShipments = shipments.stream()
                        .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()))
                        .limit(10)
                        .map(s -> String.format("- Tracking #%s: %s (From: %s To: %s)", s.getTrackingNumber(), s.getStatus(), s.getPickupCity(), s.getDropCity()))
                        .collect(Collectors.joining("\n"));

                systemPrompt.append("\n\n--- CURRENT USER CONTEXT ---\n");
                systemPrompt.append("You are currently talking directly to a registered user of the logistics platform. Always use this context to answer their personal questions. DO NOT ask them for their details, you already have them below:\n\n");
                systemPrompt.append(String.format("Name: %s\nRole: %s\nContact: %s\n\n", user.getName(), user.getRole(), user.getPhone()));
                systemPrompt.append(String.format("Shipment Statistics:\n- Total Shipments: %d\n- Pending Shipments: %d\n- Delivered Shipments: %d\n\n", shipments.size(), pending, completed));
                systemPrompt.append("10 Most Recent Shipments:\n");
                systemPrompt.append(recentShipments.isEmpty() ? "No recent shipments." : recentShipments);
                systemPrompt.append("\n----------------------------\n");
            }
        }

        return chatClient.prompt()
                .system(systemPrompt.toString())
                .user(message)
                .call()
                .content();
    }
}
