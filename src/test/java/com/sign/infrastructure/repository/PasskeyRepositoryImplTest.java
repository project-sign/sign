package com.sign.infrastructure.repository;

import com.sign.application.repository.PasskeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;


@DataJpaTest
@Import(PasskeyRepositoryImpl.class)
class PasskeyRepositoryImplTest extends PasskeyRepositoryTest {

    @Autowired
    private PasskeyRepository repository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.passkeyRepository = repository;
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
}