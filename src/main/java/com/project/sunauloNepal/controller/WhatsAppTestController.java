package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.services.WhatsAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhatsAppTestController {

    private final WhatsAppService whatsappService;

    public WhatsAppTestController(WhatsAppService whatsappService) {
        this.whatsappService = whatsappService;
    }

    // Example: test sending WhatsApp message
    @GetMapping("/api/test-whatsapp")
    public String testWhatsApp(@RequestParam String to, @RequestParam String msg) {
        try {
            whatsappService.sendWhatsAppMessage(to, msg);
            return "✅ Message sent to " + to;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send message: " + e.getMessage();
        }
    }
}
