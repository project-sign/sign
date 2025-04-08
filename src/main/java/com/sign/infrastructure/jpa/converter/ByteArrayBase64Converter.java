package com.sign.infrastructure.jpa.converter;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.exception.Base64UrlException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ByteArrayBase64Converter implements AttributeConverter<ByteArray, String> {

    @Override
    public String convertToDatabaseColumn(ByteArray attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getBase64Url();
    }

    @Override
    public ByteArray convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return ByteArray.fromBase64("");
        }
        try {
            return ByteArray.fromBase64Url(dbData);
        } catch (Base64UrlException e) {
            throw new RuntimeException(e);
        }
    }
}
