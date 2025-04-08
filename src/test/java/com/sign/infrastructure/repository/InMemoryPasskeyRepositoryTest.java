package com.sign.infrastructure.repository;

public class InMemoryPasskeyRepositoryTest extends PasskeyRepositoryTest {

    public InMemoryPasskeyRepositoryTest() {
        passkeyRepository = new InMemoryPasskeyRepository();
    }

    @Override
    public void cleanUp() {
        passkeyRepository = new InMemoryPasskeyRepository();
    }
}
