
// queryParam으로 ?appId=번호 형태로 받아 인증 할 앱에 대한 키를 발급할 예정
const params = new URLSearchParams(window.location.search);
const appId = params.get('appId');
console.log(appId);

// passkey login
// 1. assertion challenge request (get /assertion)
(() => {
    fetch("/passkey/assertion")
        .then(response => {
            if (!response.ok) throw new Error("서버 오류");
            return response.json();
        })
        .then(data => {
            const challenge = data.data.publicKeyCredentialRequestOptions.challenge;
            loginPasskey(challenge);
        })
        .catch(err => {
            console.error("API 실패:", err);
        });
})();

// 2. passkey 추출

function toBase64Url(base64) {
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function base64UrlToBase64(base64Url) {
    // URL-safe 형식을 표준 base64로 변환
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

    // 패딩 추가 (길이가 4의 배수가 아닐 경우 '=' 추가)
    while (base64.length % 4) {
        base64 += '=';
    }
    return base64;
}

async function loginPasskey(challenge) {
    try {
        const publicKey = {
            challenge: Uint8Array.from(atob(base64UrlToBase64(challenge)), c => c.charCodeAt(0)),
            userVerification: "preferred",
        };
        // 브라우저 passkey 인증 기능 호출
        const credential = await navigator.credentials.get({
              publicKey: publicKey
        });

        console.log(credential);
    } catch (error) {
        console.error("패스키 로그인이 실패했습니다.", error);
    }
}
// 3. assertion finish request(post /assertion)
// 4. TODO: post /assertion이 access_token을 반환하도록 변경



