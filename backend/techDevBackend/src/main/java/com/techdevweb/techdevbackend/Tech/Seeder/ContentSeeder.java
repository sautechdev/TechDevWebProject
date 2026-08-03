package com.techdevweb.techdevbackend.Tech.Seeder;

import com.techdevweb.techdevbackend.Tech.APIService.DevToService;
import com.techdevweb.techdevbackend.Tech.APIService.GitHubService;
import com.techdevweb.techdevbackend.Tech.APIService.WikipediaService;
import com.techdevweb.techdevbackend.Tech.Entity.TechContent;
import com.techdevweb.techdevbackend.Tech.Entity.TechStack;
import com.techdevweb.techdevbackend.Tech.Repository.TechContentRepository;
import com.techdevweb.techdevbackend.Tech.Repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentSeeder implements ApplicationRunner {

    private final TechStackRepository techStackRepository;
    private final TechContentRepository techContentRepository;
    private final WikipediaService wikipediaService;
    private final DevToService devToService;
    private final GitHubService gitHubService;

    @Override
    @Order(2) // DataSeeder'dan sonra çalışsın
    public void run(ApplicationArguments args) {
        List<TechStack> stacks = techStackRepository.findAll();

        if (stacks.isEmpty()) {
            log.warn("TechStack bulunamadı, önce DataSeeder çalışmalı!");
            return;
        }

        long alreadySeeded = stacks.stream()
                .filter(s -> techContentRepository.existsByTechStackId(s.getId()))
                .count();

        if (alreadySeeded == stacks.size()) {
            log.info("İçerikler zaten mevcut, atlanıyor...");
            return;
        }

        log.info("API içerikleri yükleniyor... ({} teknoloji)", stacks.size());

        stacks.forEach(stack -> {
            // Zaten varsa atla
            if (techContentRepository.existsByTechStackId(stack.getId())) {
                return;
            }

            log.info("Çekiliyor: {}", stack.getName());

            String wikipedia = wikipediaService.fetchSummary(stack.getName());
            String devto = devToService.fetchArticles(stack.getName());
            String github = gitHubService.fetchRepos(stack.getName());

            TechContent content = TechContent.builder()
                    .techStack(stack)
                    .wikipediaSummary(wikipedia)
                    .devtoArticles(devto)
                    .githubRepos(github)
                    .build();

            techContentRepository.save(content);

            // API rate limit için kısa bekleme
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        log.info("API içerikleri başarıyla yüklendi!");
    }
}
