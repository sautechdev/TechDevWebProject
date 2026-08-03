package com.techdevweb.techdevbackend.Tech.Seeder;

import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import com.techdevweb.techdevbackend.Tech.Entity.TechStack;
import com.techdevweb.techdevbackend.Tech.Mapper.TechLogoMapper;
import com.techdevweb.techdevbackend.Tech.Repository.TechFieldRepository;
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
@Order(1)
public class DataSeeder implements ApplicationRunner {

    private final TechFieldRepository techFieldRepository;
    private final TechStackRepository techStackRepository;
    private final TechLogoMapper logoMapper;

    @Override
    public void run(ApplicationArguments args) {
        if (techFieldRepository.count() > 0) {
            log.info("Seed data zaten mevcut, atlanıyor...");
            return;
        }

        log.info("Seed data yükleniyor...");
        seedAll();
        log.info("Seed data başarıyla yüklendi!");
    }

    private void seedAll() {
        seedField("Backend", "💻", "Sunucu tarafı uygulama geliştirme alanı.",
                List.of("Java", "C#", "Python", "Go", "Rust", "PHP", "Ruby",
                        "Kotlin", "Node.js", "Spring Boot", ".NET", "Django", "FastAPI"));

        seedField("Frontend", "🌐", "Kullanıcı arayüzü ve web geliştirme alanı.",
                List.of("JavaScript", "TypeScript", "React", "Vue.js", "Angular",
                        "Svelte", "HTML/CSS", "Next.js", "Tailwind CSS"));

        seedField("Mobile", "📱", "iOS ve Android mobil uygulama geliştirme alanı.",
                List.of("Swift", "Kotlin", "Flutter", "React Native",
                        "Dart", "Jetpack Compose", "SwiftUI"));

        seedField("Database", "🗄️", "Veritabanı tasarımı ve yönetimi alanı.",
                List.of("PostgreSQL", "MySQL", "MongoDB", "Redis", "SQLite",
                        "Oracle", "Elasticsearch", "Cassandra", "Firebase"));

        seedField("AI / ML", "🤖", "Yapay zeka ve makine öğrenmesi alanı.",
                List.of("Python", "TensorFlow", "PyTorch", "Scikit-learn", "Keras",
                        "OpenCV", "Hugging Face", "LangChain", "Pandas", "NumPy"));

        seedField("DevOps", "⚙️", "Yazılım geliştirme ve operasyon süreçleri alanı.",
                List.of("Docker", "Kubernetes", "Jenkins", "GitHub Actions",
                        "Terraform", "Ansible", "Linux", "Nginx"));

        seedField("Cloud", "☁️", "Bulut bilişim ve altyapı yönetimi alanı.",
                List.of("AWS", "Azure", "Google Cloud Platform", "DigitalOcean",
                        "Heroku", "Cloudflare", "Vercel", "Netlify"));

        seedField("Big Data", "📊", "Büyük veri işleme ve analiz alanı.",
                List.of("Apache Spark", "Hadoop", "Kafka", "Hive",
                        "Flink", "Airflow", "Scala", "Databricks"));

        seedField("Cybersecurity", "🔒", "Siber güvenlik ve etik hacking alanı.",
                List.of("Kali Linux", "Metasploit", "Wireshark", "Burp Suite",
                        "OWASP", "Nmap", "Nessus"));

        seedField("Blockchain", "🔗", "Blok zinciri ve Web3 geliştirme alanı.",
                List.of("Solidity", "Ethereum", "Hyperledger", "Smart Contracts"));

        seedField("Sistem Programlama", "⚡", "Düşük seviyeli sistem ve donanım programlama alanı.",
                List.of("C", "C++", "Assembly x86", "Assembly ARM",
                        "Assembly MIPS", "Rust", "Embedded C", "RTOS"));

        seedField("Formal Methods", "🔬", "Yazılımın matematiksel olarak doğruluğunu kanıtlamaya odaklanan biçimsel doğrulama alanı.",
                List.of("Dafny", "TLA+", "Coq", "Isabelle", "Lean"));
    }

    private void seedField(String name, String icon, String description, List<String> stacks) {
        TechField field = TechField.builder()
                .name(name)
                .icon(icon)
                .description(description)
                .build();

        techFieldRepository.save(field);

        stacks.forEach(stackName -> {
            TechStack stack = TechStack.builder()
                    .name(stackName)
                    .logoUrl(logoMapper.resolve(stackName))
                    .techField(field)
                    .build();
            techStackRepository.save(stack);
        });
    }
}
