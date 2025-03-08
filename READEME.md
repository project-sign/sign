# 사용자 회원 가입 기능

- 사용자는 인증된 이메일을 이용해 회원가입을 진행한다.
- 이미 가입된 이메일을 입력한 경우 거절한다.
- 이메일 인증 코드는 8자리의 영문 + 숫자로 구성된 키다.

# 인증 이메일 발송 API

## Endpoint
- **Method**: POST
- **URL**: `/email/code/sene`

## 설명
사용자가 회원가입 시 입력한 이메일 주소에 대해, 해당 이메일의 소유권을 확인하기 위해 인증 코드를 발송합니다.

## 요청 (Request)
- **Request Body (JSON)**
```json
{
  "email": "user@example.com"
}
```
## 응답 (Response)

```json
{
  "message": "Code email sent",
  "data" : null
}
```

# 이메일 인증 API

## Endpoint
- **Method**: POST
- **URL**: `/email/code/verify`

## 설명
이메일 인증 코드로 이메일을 인증한다.

## 요청 (Request)
- **Request Body (JSON)**
```json
{
  "email": "robin@example.com",
  "code" : "ASE126DF"
}
```
## 응답 (Response)
```json
{
  "message": "Email verified successfully",
  "data" : null
}
```

# 패스키 등록 시작 API

## Endpoint
- Method: POST
- URL: /webauthn/registry/start

## 설명

패스키를 등록하는 절차를 시작한다.

## 요청 (Request)

- **Request Body (JSON)**
```json
{
  "displayName" : "robin",
  "email": "robin@example.com"
}
```

## 응답 (Response)
```json
{
  "message" : "PassKey Register Start",
  "data" : {
    "challenge": "random-challenge-string",
    "rp": {
      "name": "Project Sign",
      "id": "{서비스 도메인 주소}"
    },
    "user": {
      "id": "Base64UrlEncodedUserId",
      "name": "robin@example.com",
      "displayName": "robin"
    },
    "pubKeyCredParams": [
      { "alg": -7, "type": "public-key" }
    ],
    "authenticatorSelection": {
      "authenticatorAttachment": "cross-platform",
      "userVerification": "required"
    },
    "timeout": 60000,
    "attestation": "direct"
  }
}
```
- challenge: 서버가 생성한 난수로, 클라이언트에서 사용.
-  서비스(Relying Party) 정보.
- user: 사용자의 식별 정보. id는 바이트 배열을 Base64Url로 인코딩한 값. 
- pubKeyCredParams: 사용 가능한 공개 키 알고리즘 목록.
- authenticatorSelection:
	- authenticatorAttachment: 클라이언트가 사용할 인증 장치 유형 (예: "cross-platform").
	- userVerification: "required"로 설정하여, 등록 시 반드시 사용자 인증(지문, 얼굴인식 등)을 수행하게 함.
- timeout: 등록 제한 시간(밀리초).
- attestation: https://www.corbado.com/glossary/attestation
# 패스키 등록 완료 API

## Endpoint
- Method: POST
- URL: /webauthn/registry/finish

## 설명

클라이언트가 WebAuthn API를 통해 생성한 패스키(credential) 정보를 서버에 전송하여, 사용자의 패스키 등록을 완료합니다. 서버는 전달받은 데이터를 검증한 후, 등록된 Credential을 저장합니다.

## 요청 (Request)

### Request Body (JSON)

```json
{
  "message" : "PassKey Register Start",
  "data" : {
    "email": "robin@example.com",
    "id": "credential-id-string",
    "rawId": "Base64UrlEncodedRawId",
    "type": "public-key",
    "response": {
      "clientDataJSON": "Base64UrlEncodedClientDataJSON",
      "attestationObject": "Base64UrlEncodedAttestationObject"
    }
  },
}
```

- email: 등록된 사용자의 이메일 주소. 
- id: 생성된 Credential의 식별자. 
- rawId: Credential의 원시 ID를 Base64Url로 인코딩한 값. 
- type: 항상 "public-key". 
- response
  - clientDataJSON: 클라이언트가 생성한 데이터 (challenge, origin 등 포함). 
  - attestationObject: 인증 장치에서 생성한 attestation 데이터 (CBOR 인코딩).