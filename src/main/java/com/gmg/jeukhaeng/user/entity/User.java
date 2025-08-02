package com.gmg.jeukhaeng.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    private String provider;
    private String providerId;

    @Column(name = "linked_providers")
    @Convert(converter = StringSetConverter.class)
    private Set<String> linkedProviders;

    public void addLinkedProvider(String provider) {
        if (this.linkedProviders == null) {
            this.linkedProviders = new HashSet<>();
        }
        this.linkedProviders.add(provider);
    }
}