package com.techdevweb.techdevbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techdevweb.techdevbackend.Enum.UserRole;
import com.techdevweb.techdevbackend.Skill.Entity.Skill;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// hibernateLazyInitializer/handler: Hibernate proxy nesnelerinin JSON'a sizmasini onler
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // WRITE_ONLY: sifre JSON ile kaydedilebilir (register/login isteklerinde) ama
    // hicbir response'ta (Project, ProjectMember vs. icinde bile) geri donmez.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    // Global rol: ADMIN veya USER. Register endpoint'i her zaman USER atar -
    // ADMIN sadece mevcut bir admin tarafindan (Admin modulu) veya veritabanina
    // elle mudahale ile atanabilir. Boylece kimse kendini admin yapamaz.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    // Kullanicinin yetkinlikleri (Python, Docker vs.). JsonIgnore: bu koleksiyon
    // User her serialize edildiginde (Application.applicant, ProjectMember.user gibi
    // yerlerde) otomatik gorunmesin diye - skill listesi sadece Profile endpoint'lerinden
    // ozel olarak donuyor (bkz. ProfileResponse).
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    // E-posta dogrulama: hesap olusturulunca false baslar, dogru kod girilince true olur.
    @Column(nullable = false)
    private boolean emailVerified = false;

    // Kullaniciya mailde gonderilen 6 haneli kod. Dogrulama basarili olunca (veya
    // yeni kod istenince eskisi gecersiz kilinirken) null'a cekilir.
    private String verificationCode;

    private LocalDateTime verificationCodeExpiresAt;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
