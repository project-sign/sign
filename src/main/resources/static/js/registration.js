const EMAIL_TIME_OUT = 180;
let currentIndex = 0;
let timerId;
let remaining = EMAIL_TIME_OUT;

function hideElement(element) {
    element.classList.remove("active");
    element.classList.add("hidden");
}

function activateElement(element) {
    element.classList.remove("hidden");
    element.classList.add("active");
}

function startTimer() {
    remaining = EMAIL_TIME_OUT;
    clearInterval(timerId);
    updateTimerDisplay(remaining);

    timerId = setInterval(() => {
        remaining--;
        updateTimerDisplay(remaining);
        if (remaining <= 0) {
            clearInterval(timerId);
        }
    }, 1000);
}

function updateTimerDisplay(seconds) {
    const minutes = String(Math.floor(seconds / 60)).padStart(2, '0');
    const secs = String(seconds % 60).padStart(2, '0');
    document.querySelector(".timer").textContent = `${minutes}:${secs}`;
}

function goToStep(index) {
    currentIndex = index;
    const container = document.querySelector('.step-container');
    container.style.transform = `translateX(-${index * 100}%)`;
}
const backButton = document.querySelector("#back-button");
const emailSendInput = document.querySelector("input.email-send");
const emailVerify = document.querySelector("#emailVerify");
const emailVerifyInput = document.querySelector("input.email-verify");
const emailSendButton = document.querySelector("button.email-send");
const emailVerifyButton = document.querySelector("button.email-verify");
const resendButton = document.querySelector("button.resend-button");
const registerPasskeyButton = document.querySelector("button.passkey-register");

const connectMap = new Map();

function connectInputButton(input, button) {
    connectMap.set(input, button);
    input.addEventListener("input", () => {
        button.disabled = input.value.trim() === "";
    });
}
connectInputButton(emailSendInput,emailSendButton);
connectInputButton(emailVerifyInput,emailVerifyButton);

emailSendButton.disabled = true;

emailSendButton.addEventListener("click", async (event) => {
    event.preventDefault();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const email = emailSendInput.value.trim();
    const isValid = validateInput(emailSendInput, email, emailRegex, "email 양식이 올바르지 않습니다.");
    if (!isValid) return;
    fetch("/email/certification", {
       method: "POST",
       headers: { "Content-Type": "application/json" },
       body: JSON.stringify({ email: email })
    })
    .then(response => {
       if (!response.ok) throw new Error("서버 오류");
       return response.json();
    })
    .then(data => {
       hideElement(emailSendButton);
       activateElement(emailVerify);
       emailVerifyButton.disabled = true;
       activateElement(emailVerifyButton);
       startTimer();
    })
    .catch(err => {
       console.error("API 실패:", err);
    });
});

emailVerifyButton.addEventListener("click", async (event) => {
    event.preventDefault();
    const verifyRegex = /^[0-9]+$/;
    const email = emailSendInput.value.trim();
    const code = emailVerifyInput.value.trim();
    const inputHolder = emailVerifyInput.closest('.email-input');
    const isValid = validateInput(inputHolder, code, verifyRegex, "인증 번호 형식이 올바르지 않습니다.")
    if (!isValid) return;
    fetch("/email/certification/validation", {
       method: "POST",
       headers: { "Content-Type": "application/json" },
       body: JSON.stringify({email: email, code: code})
    })
    .then(response => {
       if (!response.ok) throw new Error("서버 오류");
       return response.json();
    })
    .then(data => {
        hideElement(emailVerifyButton);
        goToStep(1);
    })
    .catch(err => {
       console.error("API 실패:", err);
    });
});

resendButton.addEventListener("click", async (event) => {
    event.preventDefault();
    remaining = EMAIL_TIME_OUT;
    const email = emailSendInput.value.trim();
    fetch("/email/certification", {
       method: "POST",
       headers: { "Content-Type": "application/json" },
       body: JSON.stringify({ email: email })
    })
    .then(response => {
       if (!response.ok) throw new Error("서버 오류");
       return response.json();
    })
    .catch(err => {
       console.error("API 실패:", err);
    });
    startTimer();
});

registerPasskeyButton.addEventListener("click", async (event) => {
    fetch("/passkey/registration")
    .then(response => {
        if (!response.ok) throw new Error("서버 오류");
        return response.json();
    })
    .then(data => {
        if(data.data.status !== "SUCCESS") throw new Error("데이터 전송 중 오류가 발생했습니다");
        registerPasskey(data.data.options);
    })
     .catch(err => {
        console.error("API 실패:", err);
     });
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      const activeElement = document.activeElement;
      const button = connectMap.get(activeElement);
      if(!button) return;
      button.click();
    }
});

function printErrorMessage(element,message) {
    element.textContent = message;
    element.classList.add('visible');
}
function clearErrorMessage(element) {
    element.textContent = '';
    element.classList.remove('visible');
}

function validateInput(input, message, regex, errorMessage) {
    errorElement = input.closest('.validate-input').querySelector('.error-message')
    if (!regex.test(message)) {
      input.classList.add('error');
      input.style.animation = 'none';
      input.offsetHeight;
      input.style.animation = null;
      printErrorMessage(errorElement, errorMessage)
      return false;
    }
    input.classList.remove('error');
    clearErrorMessage(errorElement);
    return true;
}

/* register passkey */

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

async function registerPasskey(options) {
    try {
        // 서버에서 받은 challenge와 user ID 값을 URL-safe base64에서 표준 base64로 변환 후 디코딩
        options.challenge = Uint8Array.from(atob(base64UrlToBase64(options.challenge)), c => c.charCodeAt(0));
        options.user.id = Uint8Array.from(atob(base64UrlToBase64(options.user.id)), c => c.charCodeAt(0));

        // excludeCredentials의 각 id를 ArrayBuffer 형식으로 변환
        if (options.excludeCredentials) {
            options.excludeCredentials = options.excludeCredentials.map(cred => ({
                ...cred,
                id: Uint8Array.from(atob(base64UrlToBase64(cred.id)), c => c.charCodeAt(0)).buffer,
                transports: Array.isArray(cred.transports) ? cred.transports : [cred.transports] // 배열로 변환
            }));
        }

        if (options.extensions && options.extensions.appidExclude !== undefined) {
                    delete options.extensions.appidExclude;
        }

        const credential = await navigator.credentials.create({
            publicKey: options
        });

        const attestationResponse = {
            id: credential.id,
            rawId: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.rawId)))),
            type: credential.type,
             response: {
                clientDataJSON: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.clientDataJSON)))),
                attestationObject: toBase64Url(btoa(String.fromCharCode(...new Uint8Array(credential.response.attestationObject))))
             },
             clientExtensionResults: {}
        };

        const registerResponse = await fetch(`/passkey/registration`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(attestationResponse)
        });
        if (registerResponse.ok) {
            alert("회원가입이 성공적으로 완료 되었습니다.");
        } else {
            alert("패스키 등록에 실패했습니다.", error);
        }
    } catch (error) {
        console.error("패스키 등록에 실패했습니다.", error);
        alert("패스키 등록 중 오류가 발생했습니다.");
    }
}