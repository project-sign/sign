package com.sign.infrastructure.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class EmailCertification {
    @Id
    private Integer id;

    private String emailAddress;

    private String certificationCode;

    public EmailCertification(String emailAddress, String certificationCode) {
        this.emailAddress = emailAddress;
        this.certificationCode = certificationCode;
    }
}
