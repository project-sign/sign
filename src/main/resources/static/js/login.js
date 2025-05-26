
// queryParam으로 ?appId=번호 형태로 받아 인증 할 앱에 대한 키를 발급할 예정
const params = new URLSearchParams(window.location.search);
const appId = params.get('appId');
console.log(appId);

// passkey login
// 1. assertion challenge request (get /assertion)
// 2. passkey 추출
// 3. assertion finish request(post /assertion)
// 4. TODO: post /assertion이 access_token을 반환하도록 변경



