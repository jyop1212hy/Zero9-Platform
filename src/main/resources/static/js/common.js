const accessToken = localStorage.getItem("accessToken");

let existToken;
let isExpired;

if (!accessToken) {
    existToken = false;
    isExpired = true; // 토큰 없음 = 만료
} else {
    existToken = true;

    try {
        const payloadBase64 = accessToken.split('.')[1];
        const payloadJson = atob(payloadBase64);
        const payload = JSON.parse(payloadJson);

        const now = Math.floor(Date.now() / 1000);

        isExpired = payload.exp < now;
    } catch (e) {
        console.error("JWT 디코딩 실패", e);

        isExpired = true; // 실패 = 무효
    }
}

if (existToken && isExpired) {
    fetch("http://localhost:8080/zero9/auth/reissue", {
        method: "POST",
        credentials: "include",
    })
        .then(async response => {
            if (!response.ok) {
                throw new Error("Refresh token 재발급 실패");
            }
            return response.json();
        })
        .then(data => {
            const newAccessToken = data?.data?.accessToken || data?.data?.token;

            if (!newAccessToken) {
                throw new Error("재발급 토큰 없음");
            }

            localStorage.setItem("accessToken", newAccessToken);

            console.log("Access Token 재발급 완료");
        })
        .catch(() => {
            console.log("다시 로그인 필요");
            // location.href = "/login.html";
        });
}