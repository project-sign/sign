package com.sign.application.usecase;

import com.sign.application.repository.DeveloperRepository;
import com.sign.domain.Developer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeveloperUseCase {

    private final DeveloperRepository developerRepository;

    public Optional<Developer> saveDeveloper(String email) {
        if (developerRepository.findDeveloperByEmail(email).isPresent()) {
            return Optional.empty();
        }

        return developerRepository.saveDeveloper(new Developer(email));
    }
}
