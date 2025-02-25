package com.ai.dipak.controller;

import com.ai.dipak.service.ChatService;
import com.ai.dipak.service.ImageService;
import com.ai.dipak.service.RecipeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class GenAIController {
    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    ChatService chatService;
    ImageService imageService;
    RecipeService recipeService;
    OpenAiAudioApi audioApi;

    public GenAIController(ChatService chatService, ImageService imageService, RecipeService recipeService, @Value("${spring.ai.openai.api-key}") String key) {
        this.chatService = chatService;
        this.imageService = imageService;
        this.recipeService = recipeService;
        this.audioApi = OpenAiAudioApi.builder().apiKey(key).build();
        this.audioTranscriptionModel = new OpenAiAudioTranscriptionModel(audioApi);
    }

    @GetMapping("ask-ai")
    public String getResponse(@RequestParam String prompt) {
        return chatService.getResponse(prompt);
    }

    @GetMapping("ask-ai-options")
    public String getResponseWithOptions(@RequestParam String prompt, @RequestParam String model) {
        return chatService.getResponseOptions(prompt, model);
    }

    @GetMapping("generate-image")
    public void generateImages(HttpServletResponse response, @RequestParam String prompt) throws IOException {
        ImageResponse imageResponse = imageService.generateImage(prompt);
        String imgUrl = imageResponse.getResult().getOutput().getUrl();
        response.sendRedirect(imgUrl);

    }

    @GetMapping("generate-image-options")
    public List<String> generateImagesMultiple(@RequestParam String prompt,
                                               @RequestParam(defaultValue = "hd") String quality,
                                               @RequestParam(defaultValue = "1") Integer n,
                                               @RequestParam(defaultValue = "1024") Integer height,
                                               @RequestParam(defaultValue = "1024") Integer width) {
        ImageResponse imageResponse = imageService.generateImageMultiple(prompt, quality, n, width, height);
        List<String> imageUrls = imageResponse.getResults().stream().map(r -> r.getOutput().getUrl()).toList();
        return imageUrls;

    }

    @GetMapping("recipe-creator")
    public String recipeCreator(
            @RequestParam String ingredients,
            @RequestParam(defaultValue = "any") String cuisine,
            @RequestParam(defaultValue = "") String dietaryRestrictions) {
        return recipeService.getResponse(ingredients, cuisine, dietaryRestrictions);
    }

    @PostMapping("/api/transcribe")
    public ResponseEntity<String> transcribe(
            @RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("audio", ".wav");
        file.transferTo(tempFile);
        OpenAiAudioTranscriptionOptions transcription = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .language("en")
                .temperature(0f)
                .build();
        FileSystemResource audioFile = new FileSystemResource(tempFile);
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(audioFile, transcription);
        AudioTranscriptionResponse audioTranscriptionResponse = audioTranscriptionModel.call(transcriptionPrompt);
        tempFile.delete();
        return new ResponseEntity<>(audioTranscriptionResponse.getResult().getOutput(), HttpStatus.OK);
    }
}
