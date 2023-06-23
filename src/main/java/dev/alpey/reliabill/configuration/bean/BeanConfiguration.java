package dev.alpey.reliabill.configuration.bean;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableAsync
@EnableCaching
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }

    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(webPageTemplateResolver());
        return templateEngine;
    }

    @Bean
    public ClassLoaderTemplateResolver webPageTemplateResolver() {
        ClassLoaderTemplateResolver webPageTemplateResolver = new ClassLoaderTemplateResolver();
        webPageTemplateResolver.setPrefix("templates/");
        webPageTemplateResolver.setSuffix(".html");
        webPageTemplateResolver.setTemplateMode(TemplateMode.HTML);
        webPageTemplateResolver.setCharacterEncoding("UTF-8");
        webPageTemplateResolver.setOrder(1);
        return webPageTemplateResolver;
    }
}
