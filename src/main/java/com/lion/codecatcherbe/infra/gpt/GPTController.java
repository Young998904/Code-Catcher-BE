package com.lion.codecatcherbe.infra.gpt;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.client.AiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GPTController {
    private final AiClient client;

    @GetMapping("")
    public String gptTest (@RequestParam (defaultValue = "코딩테스트 문제 아무거나") String prompt) {
        return client.generate(prompt);
    }
}
