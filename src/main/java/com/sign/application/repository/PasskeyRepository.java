package com.sign.application.repository;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;

public interface PasskeyRepository extends CredentialRepository {
    void save(String email, RegisteredCredential credential);

    void updateSignatureCount(String email, ByteArray credentialId, long newSignatureCount);
}
