package com.techdevweb.techdevbackend.Tech.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tech_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TechField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String icon;

    @Column(columnDefinition = "TEXT")
    private String description;

    // JsonIgnore: TechField -> techStacks -> TechStack -> techField -> ... sonsuz donguyu onler.
    // Bu alan artik JSON response'larda hic gorunmeyecek (mesela /api/tech-fields cevabinda).
    // Eger frontend'in bir tech field'in stack listesine ihtiyaci varsa, ayri bir
    // GET /api/tech-fields/{id}/stacks gibi endpoint ile saglanmali.
    @JsonIgnore
    @OneToMany(mappedBy = "techField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TechStack> techStacks;
}