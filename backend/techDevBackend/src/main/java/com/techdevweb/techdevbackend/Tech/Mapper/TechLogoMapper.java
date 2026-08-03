package com.techdevweb.techdevbackend.Tech.Mapper;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TechLogoMapper {

    private static final Map<String, String> LOGO_MAP = Map.ofEntries(
            // Backend
            Map.entry("Java", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg"),
            Map.entry("C#", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/csharp/csharp-original.svg"),
            Map.entry("Python", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg"),
            Map.entry("Go", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/go/go-original.svg"),
            Map.entry("Rust", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/rust/rust-original.svg"),
            Map.entry("PHP", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/php/php-original.svg"),
            Map.entry("Ruby", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/ruby/ruby-original.svg"),
            Map.entry("Kotlin", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg"),
            Map.entry("Node.js", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nodejs/nodejs-original.svg"),
            Map.entry("Spring Boot", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg"),
            Map.entry(".NET", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/dot-net/dot-net-original.svg"),
            Map.entry("Django", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/django/django-plain.svg"),
            Map.entry("FastAPI", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/fastapi/fastapi-original.svg"),

            // Frontend
            Map.entry("JavaScript", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/javascript/javascript-original.svg"),
            Map.entry("TypeScript", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/typescript/typescript-original.svg"),
            Map.entry("React", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg"),
            Map.entry("Vue.js", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg"),
            Map.entry("Angular", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/angularjs/angularjs-original.svg"),
            Map.entry("Svelte", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/svelte/svelte-original.svg"),
            Map.entry("HTML/CSS", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/html5/html5-original.svg"),
            Map.entry("Next.js", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nextjs/nextjs-original.svg"),
            Map.entry("Tailwind CSS", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tailwindcss/tailwindcss-original.svg"),

            // Mobile
            Map.entry("Swift", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/swift/swift-original.svg"),
            Map.entry("Flutter", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flutter/flutter-original.svg"),
            Map.entry("React Native", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg"),
            Map.entry("Dart", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/dart/dart-original.svg"),
            Map.entry("Jetpack Compose", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg"),
            Map.entry("SwiftUI", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/swift/swift-original.svg"),

            // Database
            Map.entry("PostgreSQL", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg"),
            Map.entry("MySQL", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg"),
            Map.entry("MongoDB", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mongodb/mongodb-original.svg"),
            Map.entry("Redis", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redis/redis-original.svg"),
            Map.entry("SQLite", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/sqlite/sqlite-original.svg"),
            Map.entry("Oracle", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/oracle/oracle-original.svg"),
            Map.entry("Elasticsearch", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/elasticsearch/elasticsearch-original.svg"),
            Map.entry("Cassandra", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/cassandra/cassandra-original.svg"),
            Map.entry("Firebase", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/firebase/firebase-plain.svg"),

            // AI/ML
            Map.entry("TensorFlow", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tensorflow/tensorflow-original.svg"),
            Map.entry("PyTorch", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/pytorch/pytorch-original.svg"),
            Map.entry("Scikit-learn", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/scikitlearn/scikitlearn-original.svg"),
            Map.entry("Keras", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/keras/keras-original.svg"),
            Map.entry("OpenCV", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/opencv/opencv-original.svg"),
            Map.entry("Pandas", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/pandas/pandas-original.svg"),
            Map.entry("NumPy", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/numpy/numpy-original.svg"),

            // DevOps
            Map.entry("Docker", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg"),
            Map.entry("Kubernetes", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg"),
            Map.entry("Jenkins", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/jenkins/jenkins-original.svg"),
            Map.entry("Terraform", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/terraform/terraform-original.svg"),
            Map.entry("Ansible", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/ansible/ansible-original.svg"),
            Map.entry("Linux", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/linux/linux-original.svg"),
            Map.entry("Nginx", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nginx/nginx-original.svg"),

            // Cloud
            Map.entry("AWS", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/amazonwebservices/amazonwebservices-original-wordmark.svg"),
            Map.entry("Azure", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/azure/azure-original.svg"),
            Map.entry("Google Cloud Platform", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/googlecloud/googlecloud-original.svg"),
            Map.entry("Heroku", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/heroku/heroku-original.svg"),
            Map.entry("DigitalOcean", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/digitalocean/digitalocean-original.svg"),
            Map.entry("Vercel", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vercel/vercel-original.svg"),
            Map.entry("Netlify", "https://cdn.simpleicons.org/netlify"),
            Map.entry("Cloudflare", "https://cdn.simpleicons.org/cloudflare"),


            // Big Data
            Map.entry("Apache Spark", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apache/apache-original.svg"),
            Map.entry("Hadoop", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/hadoop/hadoop-original.svg"),
            Map.entry("Scala", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/scala/scala-original.svg"),
            Map.entry("Kafka", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apachekafka/apachekafka-original.svg"),
            Map.entry("Hive", "https://cdn.simpleicons.org/apachehive"),
            Map.entry("Flink", "https://cdn.simpleicons.org/apacheflink"),
            Map.entry("Airflow", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apacheairflow/apacheairflow-original.svg"),
            Map.entry("Databricks", "https://cdn.simpleicons.org/databricks"),

            // Cybersecurity
            Map.entry("Kali Linux", "https://cdn.simpleicons.org/kalilinux"),
            Map.entry("Metasploit", "https://cdn.simpleicons.org/metasploit"),
            Map.entry("Wireshark", "https://cdn.simpleicons.org/wireshark"),
            Map.entry("Burp Suite", "https://cdn.simpleicons.org/portswigger"),
            Map.entry("OWASP", "https://cdn.simpleicons.org/owasp"),
            Map.entry("Nmap", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Nessus", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),

            // Blockchain
            Map.entry("Solidity", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/solidity/solidity-original.svg"),
            Map.entry("Ethereum", "https://cdn.jsdelivr.net/npm/simple-icons@v13/icons/ethereum.svg"),
            Map.entry("Hyperledger", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Smart Contracts", "https://cdn.jsdelivr.net/npm/simple-icons@v13/icons/ethereum.svg"),

            // Sistem Programlama
            Map.entry("C", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/c/c-original.svg"),
            Map.entry("C++", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/cplusplus/cplusplus-original.svg"),
            Map.entry("Assembly x86", "https://cdn.simpleicons.org/assemblyscript"),
            Map.entry("Assembly ARM", "https://cdn.simpleicons.org/arm"),
            Map.entry("Assembly MIPS", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Embedded C", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/c/c-original.svg"),
            Map.entry("RTOS", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),

            // Formal Methods
            Map.entry("Dafny", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("TLA+", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Coq", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Isabelle", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg"),
            Map.entry("Lean", "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg")
    );

    private static final String DEFAULT_LOGO = "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/devicon/devicon-original.svg";

    public String resolve(String techName) {
        return LOGO_MAP.getOrDefault(techName, DEFAULT_LOGO);
    }
}
