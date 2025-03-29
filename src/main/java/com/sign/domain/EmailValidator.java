package com.sign.domain;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailValidator {

    private static final String EMAIL_REGEX = "^(?!\\.)[a-zA-Z0-9._%+-]+(?<!\\.)@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void validateEmailAddress(String emailAddress) {
        if (emailAddress != null && EMAIL_PATTERN.matcher(emailAddress).matches()) {
            return;
        }
        throw new IllegalArgumentException("잘못된 이메일 주소입니다.");
    }
}
