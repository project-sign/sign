package com.sign.application.repository;

import com.yubico.webauthn.data.ByteArray;

public interface HandleGenerator {
    ByteArray generateHandle();
}
