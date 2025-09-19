package com.project.sunauloNepal.controller;


import org.springframework.web.bind.annotation.*;

import com.project.sunauloNepal.services.WhatsAppService;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String to, @RequestParam String message) {
        whatsAppService.sendWhatsAppMessage(to, message);
        return "Message sent to " + to;
    }
}
