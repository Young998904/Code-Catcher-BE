package com.lion.codecatcherbe.infra.gpt.prompt;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.client.AzureOpenAiClient;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.client.Generation;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Primary
@Getter
public class CustomAzureOpenAiClient implements AiClient {
    private static final Logger logger = LoggerFactory.getLogger(AzureOpenAiClient.class);
    private final OpenAIClient msoftOpenAiClient;
    private Double temperature = 0.7;
    private String model = "code-catcher-ai-gpt35";
//    private String model = "code-catcher-ai-gpt4-preview";

    public CustomAzureOpenAiClient(OpenAIClient msoftOpenAiClient) {
        Assert.notNull(msoftOpenAiClient, "com.azure.ai.openai.OpenAIClient must not be null");
        this.msoftOpenAiClient = msoftOpenAiClient;
    }

    public AiResponse generate(Prompt prompt) {
        List<Message> messages = prompt.getMessages();
        List<ChatMessage> azureMessages = new ArrayList();
        Iterator var4 = messages.iterator();

        while(var4.hasNext()) {
            Message message = (Message)var4.next();
            String messageType = message.getMessageTypeValue();
            ChatRole chatRole = ChatRole.fromString(messageType);
            azureMessages.add(new ChatMessage(chatRole, message.getContent()));
        }

        ChatCompletionsOptions options = new ChatCompletionsOptions(azureMessages);
        options.setTemperature(this.getTemperature());
        options.setModel(this.getModel());
//        options.setStream(true); // Stream 설정 가능
        logger.trace("Azure ChatCompletionsOptions: ", options);
        ChatCompletions chatCompletions = this.msoftOpenAiClient.getChatCompletions(this.getModel(), options);
        logger.trace("Azure ChatCompletions: ", chatCompletions);
        List<Generation> generations = new ArrayList();
        Iterator var14 = chatCompletions.getChoices().iterator();

        while(var14.hasNext()) {
            ChatChoice choice = (ChatChoice)var14.next();
            ChatMessage choiceMessage = choice.getMessage();
            Generation generation = new Generation(choiceMessage.getContent());
            generations.add(generation);
        }

        return new AiResponse(generations);
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    public void setModel(String model) {
        this.model = model;
    }
}
