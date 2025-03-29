package com.sign.application.repository;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface RandomCodeGenerator {

    default <T> String generate(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> generateCharacter())
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    Character generateCharacter();
}
