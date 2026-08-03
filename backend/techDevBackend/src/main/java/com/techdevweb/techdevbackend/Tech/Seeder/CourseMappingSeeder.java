package com.techdevweb.techdevbackend.Tech.Seeder;

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
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3) // DataSeeder(1) ve ContentSeeder(2)'dan sonra çalışsın
public class CourseMappingSeeder implements ApplicationRunner {

    private final TechStackRepository techStackRepository;
    private final TechContentRepository techContentRepository;

    private static final Map<String, String> COURSE_MAP = Map.ofEntries(
            Map.entry("Java", "Distributed Information and Management Systems, Big Data, Software Development Processes (DevOps)"),
            Map.entry("C#", "Object Oriented Programming, Web Programming"),
            Map.entry("Node.js", "Distributed Information and Management Systems"),
            Map.entry("Spring Boot", "Distributed Information and Management Systems, Big Data, Software Development Processes (DevOps)"),
            Map.entry(".NET", "Web Programming"),
            Map.entry("React", "Distributed Information and Management Systems"),
            Map.entry("HTML/CSS", "User Interface Design and Testing, Web Programming"),
            Map.entry("Flutter", "Mobile Application Development"),
            Map.entry("Dart", "Mobile Application Development"),
            Map.entry("PostgreSQL", "Database Management Systems, Distributed Information and Management Systems, Big Data, Software Development Processes (DevOps)"),
            Map.entry("MySQL", "Web Programming, Distributed Information and Management Systems, Big Data, Software Development Processes (DevOps)"),
            Map.entry("MongoDB", "Big Data"),
            Map.entry("Redis", "Big Data"),
            Map.entry("Cassandra", "Big Data"),
            Map.entry("Docker", "Software Development Processes (DevOps)"),
            Map.entry("Kubernetes", "Software Development Processes (DevOps)"),
            Map.entry("Jenkins", "Software Development Processes (DevOps)"),
            Map.entry("GitHub Actions", "Software Development Processes (DevOps)"),
            Map.entry("Nginx", "Software Development Processes (DevOps)"),
            Map.entry("AWS", "Big Data, Software Development Processes (DevOps)"),
            Map.entry("Azure", "Big Data, Software Development Processes (DevOps)"),
            Map.entry("Apache Spark", "Big Data"),
            Map.entry("Hadoop", "Big Data"),
            Map.entry("Kafka", "Big Data"),
            Map.entry("Hive", "Big Data"),
            Map.entry("Scala", "Big Data"),
            Map.entry("Wireshark", "Computer Networks"),
            Map.entry("OWASP", "Software Verification and Validation"),
            Map.entry("C", "Problem Solving in Software Engineering, System Programming, Operating Systems"),
            Map.entry("C++", "Problem Solving in Software Engineering, Data Structures and Algorithms"),
            Map.entry("Assembly x86", "System Programming"),
            Map.entry("Assembly MIPS", "Logical Design and Computer Architecture"),
            Map.entry("Dafny", "Formal Methods in Software Engineering")
    );

    @Override
    public void run(ApplicationArguments args) {
        List<TechStack> allStacks = techStackRepository.findAll();

        COURSE_MAP.forEach((techName, courses) -> {
            Optional<TechStack> stackOpt = allStacks.stream()
                    .filter(s -> s.getName().equals(techName))
                    .findFirst();

            if (stackOpt.isEmpty()) {
                log.warn("Teknoloji bulunamadı, ders eşleştirmesi atlandı: {}", techName);
                return;
            }

            TechStack stack = stackOpt.get();

            techContentRepository.findByTechStackId(stack.getId()).ifPresent(content -> {
                // Sadece boşsa doldur, elle girilmiş farklı bir veriyi asla ezme
                if (content.getRelatedCourses() == null || content.getRelatedCourses().isBlank()) {
                    content.setRelatedCourses(courses);
                    techContentRepository.save(content);
                    log.info("Ders bilgisi eklendi: {} -> {}", techName, courses);
                }
            });
        });
    }
}