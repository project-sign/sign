
// queryParam으로 ?appId=번호 형태로 받아 인증 할 앱에 대한 키를 발급할 예정
const params = new URLSearchParams(window.location.search);
const appId = params.get('appId');
console.log(appId);

const passKeyLoginButton = document.querySelector("#login-button");
const registrationButton = document.querySelector("#registration");
registrationButton.href = `/registration?appId=${appId}`;
// passkey login
// 1. assertion challenge request (get /assertion)
passKeyLoginButton.addEventListener("click", async (event) => {
    event.preventDefault();
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
});

// 2. passkey 추출
function toBase64Url(base64) {
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function base64UrlToBase64(base64Url) {
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

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

        const credential = await navigator.credentials.get({
              publicKey: publicKey
            });
        console.log(credential);
        if (credential) {
            await loginRequest(credential);
        } else {
            window.location.href = `/registration?appId=${appId}`;
        }
    } catch (error) {
        console.error("패스키 로그인이 실패했습니다.", error);
    }
}

async function loginRequest(credential) {
     const assertionResponse = {
                id: credential.id,
                rawId: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.rawId)))),
                type: credential.type,
                response: {
                    authenticatorData: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.authenticatorData)))),
                    clientDataJSON: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.clientDataJSON)))),
                    signature: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.signature)))),
                    userHandle: credential.response.userHandle ? toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.userHandle)))) : null
                },
                clientExtensionResults: credential.getClientExtensionResults()
            };

    const passkeyResponse = await fetch(`/passkey/assertion`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(assertionResponse)
    });

    if (passkeyResponse.ok) {
        console.log(passkeyResponse)
    } else {
        alert("Login failed.");
    }
}
// 4. TODO: post /assertion이 access_token을 반환하도록 변경




