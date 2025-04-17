package com.sign.infrastructure.yubico.repository;

import com.sign.infrastructure.jpa.PasskeyEntity;
import com.sign.infrastructure.jpa.RegisteredCredentialEntity;
import com.sign.infrastructure.jpa.repository.PasskeyJpaRepository;
import com.sign.infrastructure.jpa.repository.RegisteredCredentialJpaRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import(CredentialRepositoryImpl.class)
class CredentialRepositoryImplTest extends CredentialRepositoryTest {

    @Autowired
    private CredentialRepository repository;

    @Autowired
    private RegisteredCredentialJpaRepository registeredCredentialJpaRepository;

    @Autowired
    private PasskeyJpaRepository passkeyJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setRepository() {
        this.credentialRepository = repository;
    }

    @Override
    public void cleanUp() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("DELETE FROM registered_credential");
        jdbcTemplate.execute("ALTER TABLE registered_credential ALTER COLUMN id RESTART");
        jdbcTemplate.execute("DELETE FROM passkey");
        jdbcTemplate.execute("ALTER TABLE passkey ALTER COLUMN id RESTART");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Override
    public void saveData(String email, RegisteredCredential registeredCredential) {
        PasskeyEntity passkey = passkeyJpaRepository.findByEmail(email)
                .orElseGet(() -> PasskeyEntity.builder()
                        .email(email)
                        .userHandle(registeredCredential.getUserHandle())
                        .build());
        PasskeyEntity savedPasskey = passkeyJpaRepository.save(passkey);
        RegisteredCredentialEntity registeredCredentialEntity = RegisteredCredentialEntity.builder()
                .credentialId(registeredCredential.getCredentialId())
                .userHandle(registeredCredential.getUserHandle())
                .publicKeyCose(registeredCredential.getPublicKeyCose())
                .signatureCount(registeredCredential.getSignatureCount())
                .passkey(savedPasskey)
                .build();
        registeredCredentialJpaRepository.save(registeredCredentialEntity);
    }
}