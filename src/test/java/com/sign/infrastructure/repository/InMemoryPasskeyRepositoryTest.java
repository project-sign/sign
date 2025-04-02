package com.sign.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryPasskeyRepositoryTest extends PasskeyRepositoryTest {

    @Override
    @BeforeEach
    public void cleanUp() {
        passkeyRepository = new InMemoryPasskeyRepository();
    }
}
