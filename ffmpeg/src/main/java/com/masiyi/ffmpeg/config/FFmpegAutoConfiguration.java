package com.masiyi.ffmpeg.config;

import com.masiyi.ffmpeg.template.FFmpegTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = FFmpegProperties.class)
public class FFmpegAutoConfiguration {

    @Autowired
    private FFmpegProperties fFmpegProperties;


    @Bean
    public FFmpegTemplate getFFmpegTemplate() {
        return new FFmpegTemplate(fFmpegProperties.getFfmpegPath());
    }

}
