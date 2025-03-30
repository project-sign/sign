package com.sign.infrastructure.repository;

import com.sign.application.repository.HandleGenerator;
import com.yubico.webauthn.data.ByteArray;
import java.security.SecureRandom;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HandleGeneratorImpl implements HandleGenerator {

    private static final SecureRandom random = new SecureRandom();
    private final int handlerLength;

    @Override
    public ByteArray generateHandle() {
        byte[] bytes = new byte[handlerLength];
        random.nextBytes(bytes);
        return new ByteArray(bytes);
    }
}
