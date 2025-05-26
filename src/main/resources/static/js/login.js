
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
            console.log(data.data);
            // data.data 로 "2. passkey 추출" 구현
        })
        .catch(err => {
            console.error("API 실패:", err);
        });
})();

// 2. passkey 추출
// 3. assertion finish request(post /assertion)
// 4. TODO: post /assertion이 access_token을 반환하도록 변경



