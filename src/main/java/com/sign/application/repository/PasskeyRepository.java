package com.sign.application.repository;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;

public interface PasskeyRepository {

    Optional<ByteArray> findUserHandleByEmail(String email);

    void save(String email, RegisteredCredential credential);

    void updateSignatureCount(String email, ByteArray credentialId, long newSignatureCount);
}
