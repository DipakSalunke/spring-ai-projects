package com.ai.dipak.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecipeService {
    private final ChatModel chatModel;

    public RecipeService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getResponse(String ingredients,
                              String cuisine,
                              String dietaryRestrictions) {
        var template = """
                I want to create a recipe using the following ingredients: {ingredients}.
                The cuisine type I prefer is {cuisine}.
                Please consider the following dietary restrictions: {dietaryRestrictions}.
                Please provide me with a detailed recipe including title, list of ingredients, and cooking instructions.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "ingredients", ingredients,
                "cuisine", cuisine,
                "dietaryRestrictions", dietaryRestrictions
        );

        Prompt prompt = promptTemplate.create(params, OllamaOptions.builder()
                .model("mistral")
                .temperature(0.4)
                .build());
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

}
