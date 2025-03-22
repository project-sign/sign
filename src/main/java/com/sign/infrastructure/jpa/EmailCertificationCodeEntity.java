package com.sign.infrastructure.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationCodeEntity {
    @Id
    private Long id;

    private String email;

    private String certificationCode;

    private LocalDateTime expiredAt;
}
