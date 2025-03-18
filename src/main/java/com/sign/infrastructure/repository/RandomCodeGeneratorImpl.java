package com.sign.infrastructure.repository;

import com.sign.application.repository.RandomCodeGenerator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RandomCodeGeneratorImpl implements RandomCodeGenerator {

    private final Random random = new Random();

    @Override
    public Character generateCharacter() {
        return (char) ('0' + random.nextInt(10));
    }
}
