package com.techdevweb.techdevbackend.Tech.Mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WikipediaTitleMapper {

    private static final Map<String, String> TITLE_MAP = Map.<String, String>ofEntries(

            // Backend
            Map.entry("Java", "Java (programming language)"),
            Map.entry("C#", "C Sharp (programming language)"),
            Map.entry("Python", "Python (programming language)"),
            Map.entry("Go", "Go (programming language)"),
            Map.entry("Rust", "Rust (programming language)"),
            Map.entry("PHP", "PHP"),
            Map.entry("Ruby", "Ruby (programming language)"),
            Map.entry("Kotlin", "Kotlin (programming language)"),
            Map.entry("Node.js", "Node.js"),
            Map.entry("Spring Boot", "Spring Boot"),
            Map.entry(".NET", ".NET"),
            Map.entry("Django", "Django (web framework)"),
            Map.entry("FastAPI", "FastAPI"),

            // Frontend
            Map.entry("JavaScript", "JavaScript"),
            Map.entry("TypeScript", "TypeScript"),
            Map.entry("React", "React (software)"),
            Map.entry("Vue.js", "Vue.js"),
            Map.entry("Angular", "Angular (web framework)"),
            Map.entry("Svelte", "Svelte"),
            Map.entry("HTML/CSS", "HTML"),
            Map.entry("Next.js", "Next.js"),
            Map.entry("Tailwind CSS", "Tailwind CSS"),

            // Mobile
            Map.entry("Swift", "Swift (programming language)"),
            Map.entry("Flutter", "Flutter (software)"),
            Map.entry("React Native", "React Native"),
            Map.entry("Dart", "Dart (programming language)"),
            Map.entry("Jetpack Compose", "Jetpack Compose"),
            Map.entry("SwiftUI", "SwiftUI"),

            // Database
            Map.entry("PostgreSQL", "PostgreSQL"),
            Map.entry("MySQL", "MySQL"),
            Map.entry("MongoDB", "MongoDB"),
            Map.entry("Redis", "Redis"),
            Map.entry("SQLite", "SQLite"),
            Map.entry("Oracle", "Oracle Database"),
            Map.entry("Elasticsearch", "Elasticsearch"),
            Map.entry("Cassandra", "Apache Cassandra"),
            Map.entry("Firebase", "Firebase"),

            // AI / ML
            Map.entry("TensorFlow", "TensorFlow"),
            Map.entry("PyTorch", "PyTorch"),
            Map.entry("Scikit-learn", "Scikit-learn"),
            Map.entry("Keras", "Keras"),
            Map.entry("OpenCV", "OpenCV"),
            Map.entry("Hugging Face", "Hugging Face"),
            Map.entry("LangChain", "LangChain"),
            Map.entry("Pandas", "Pandas (software)"),
            Map.entry("NumPy", "NumPy"),

            // DevOps
            Map.entry("Docker", "Docker (software)"),
            Map.entry("Kubernetes", "Kubernetes"),
            Map.entry("Jenkins", "Jenkins (software)"),
            Map.entry("GitHub Actions", "GitHub Actions"),
            Map.entry("Terraform", "Terraform (software)"),
            Map.entry("Ansible", "Ansible (software)"),
            Map.entry("Linux", "Linux"),
            Map.entry("Nginx", "Nginx"),

            // Cloud
            Map.entry("AWS", "Amazon Web Services"),
            Map.entry("Azure", "Microsoft Azure"),
            Map.entry("Google Cloud Platform", "Google Cloud Platform"),
            Map.entry("DigitalOcean", "DigitalOcean"),
            Map.entry("Heroku", "Heroku"),
            Map.entry("Cloudflare", "Cloudflare"),
            Map.entry("Vercel", "Vercel"),
            Map.entry("Netlify", "Netlify"),

            // Big Data
            Map.entry("Apache Spark", "Apache Spark"),
            Map.entry("Hadoop", "Apache Hadoop"),
            Map.entry("Kafka", "Apache Kafka"),
            Map.entry("Hive", "Apache Hive"),
            Map.entry("Flink", "Apache Flink"),
            Map.entry("Airflow", "Apache Airflow"),
            Map.entry("Scala", "Scala (programming language)"),
            Map.entry("Databricks", "Databricks"),

            // Cybersecurity
            Map.entry("Kali Linux", "Kali Linux"),
            Map.entry("Metasploit", "Metasploit"),
            Map.entry("Wireshark", "Wireshark"),
            Map.entry("Burp Suite", "Burp Suite"),
            Map.entry("OWASP", "OWASP"),
            Map.entry("Nmap", "Nmap"),
            Map.entry("Nessus", "Nessus (software)"),

            // Blockchain
            Map.entry("Solidity", "Solidity"),
            Map.entry("Ethereum", "Ethereum"),
            Map.entry("Hyperledger", "Hyperledger"),
            Map.entry("Smart Contracts", "Smart contract"),

            // Sistem Programlama
            Map.entry("C", "C (programming language)"),
            Map.entry("C++", "C++"),
            Map.entry("Assembly x86", "X86 assembly language"),
            Map.entry("Assembly ARM", "ARM architecture family"),
            Map.entry("Assembly MIPS", "MIPS architecture"),
            Map.entry("Embedded C", "Embedded C"),
            Map.entry("RTOS", "Real-time operating system"),

            // Formal Methods
            Map.entry("Dafny", "Dafny"),
            Map.entry("TLA+", "TLA+"),
            Map.entry("Coq", "Coq (software)"),
            Map.entry("Isabelle", "Isabelle (proof assistant)"),
            Map.entry("Lean", "Lean (proof assistant)")
    );

    public String resolve(String techName) {
        return TITLE_MAP.getOrDefault(techName, techName);
    }
}
