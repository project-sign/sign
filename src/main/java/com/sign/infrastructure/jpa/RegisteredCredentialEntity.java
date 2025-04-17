package com.sign.infrastructure.jpa;

import com.sign.infrastructure.jpa.converter.ByteArrayBase64Converter;
import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Table(name = "registered_credential")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisteredCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Convert(converter = ByteArrayBase64Converter.class)
    private ByteArray userHandle;

    @Convert(converter = ByteArrayBase64Converter.class)
    @Column(unique = true)
    private ByteArray credentialId;

    @Convert(converter = ByteArrayBase64Converter.class)
    private ByteArray publicKeyCose;

    private Long signatureCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private PasskeyEntity passkey;

    public void updateSignatureCount(long newSignatureCount) {
        signatureCount = newSignatureCount;
    }
}
