package com.gmg.jeukhaeng.user.service;

import com.gmg.jeukhaeng.user.entity.User;
import com.gmg.jeukhaeng.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreate(String provider, String providerId, String email, String nickname) {
        return findUser(provider, providerId, email)
                .orElseGet(() -> createUser(provider, providerId, email, nickname));
    }

    private Optional<User> findUser(String provider, String providerId, String email) {
        // 1. provider + providerId 우선 조회
        Optional<User> byProviderId = userRepository.findByProviderAndProviderId(provider, providerId);
        if (byProviderId.isPresent()) {
            return byProviderId;
        }

        // 2. email로 조회 후 provider 연결
        return userRepository.findByEmail(email).map(user -> {
            user.addLinkedProvider(provider);
            return userRepository.save(user); // provider만 추가
        });
    }

    private User createUser(String provider, String providerId, String email, String nickname) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .nickname(nickname)
                        .provider(provider)
                        .providerId(providerId)
                        .linkedProviders(new HashSet<>(Set.of(provider)))
                        .build()
        );
    }
}
