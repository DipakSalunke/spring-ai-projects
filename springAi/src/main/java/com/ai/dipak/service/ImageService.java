package com.ai.dipak.service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
    ImageModel imageModel;

    public ImageService(@Qualifier("openAiImageModel") ImageModel imageModel) {
        this.imageModel = imageModel;

    }

    public ImageResponse generateImage(String prompt) {
        return imageModel.call(new ImagePrompt(prompt));
    }

    public ImageResponse generateImageMultiple(String prompt, String quality, Integer n, Integer width, Integer height) {
        return imageModel.call(new ImagePrompt(prompt, OpenAiImageOptions.builder()
                .withQuality(quality)
                .withModel("dall-e-2")
                .withN(n)
                .withHeight(height)
                .withWidth(width).build()));
    }
}
