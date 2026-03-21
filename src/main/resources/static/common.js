const categoryMap = {
    // 식품
    DESSERT: "디저트",
    INSTANT: "인스턴트",
    BEVERAGE: "음료",
    COFFEE_TEA: "커피/차",
    SNACK: "과자",
    NOODLE: "면류",
    MEAT: "육류",
    SEAFOOD: "해산물",
    FRUIT: "과일",
    HEALTH_FOOD: "건강식품",

    // 생활 / 주방
    KITCHEN: "주방용품",
    HOUSEHOLD: "생활용품",
    CLEANING: "청소용품",
    BATHROOM: "욕실용품",
    STORAGE: "수납/정리",

    // 패션 / 뷰티
    FASHION: "패션",
    SHOES: "신발",
    BAG: "가방",
    ACCESSORY: "액세서리",
    BEAUTY: "뷰티",
    COSMETIC: "화장품",

    // 디지털 / 가전
    DIGITAL: "디지털",
    APPLIANCE: "가전제품",
    MOBILE_ACCESSORY: "모바일 액세서리",

    // 취미 / 기타
    HOBBY: "취미/여가",
    PET: "반려동물",
    BABY: "유아/아동",
    BOOK: "도서",
    ETC: "기타"
};

const PROJECT_NAME = "/Zero9-Platform/Zero9-Platform.main/static"; // 로컬 개발 시 폴더명
// const PROJECT_NAME = ""
const ORIGIN_URL = window.location.origin + PROJECT_NAME;
const SPRING_BOOT_URL = "http://localhost:8080";
// let ACCESS_TOKEN = localStorage.getItem("accessToken");
// let PAYLOAD = parseJwt(ACCESS_TOKEN);

Object.defineProperty(window, 'ACCESS_TOKEN', {
    get: function() { return localStorage.getItem("accessToken"); },
    configurable: true
});

Object.defineProperty(window, 'PAYLOAD', {
    get: function() { return parseJwt(localStorage.getItem("accessToken")); },
    configurable: true
});

// function refreshAuth() {
//     ACCESS_TOKEN = localStorage.getItem("accessToken");
//     PAYLOAD = parseJwt(ACCESS_TOKEN);
// }

function isTokenExpired(token) {

    const targetToken = token || ACCESS_TOKEN;

    if (!targetToken) return true; // 토큰 없으면 만료로 처리

    try {
        // JWT payload 디코딩
        const payloadBase64 = targetToken.split('.')[1];
        const payloadJson = atob(payloadBase64); // base64 -> 문자열
        const payload = JSON.parse(payloadJson);

        // 현재 시간과 exp 비교
        const now = Math.floor(Date.now() / 1000); // 초 단위
        return payload.exp < now; // true면 만료
    } catch (err) {
        console.error("JWT 디코딩 실패:", err);
        return true; // 디코딩 오류는 만료로 간주
    }
}

/*
function parseJwt(token) {
    if(token == ''){
        return;
    }

    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
        atob(base64)
            .split('')
            .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
    );

    return JSON.parse(jsonPayload);
}
*/

// JWT 파싱 함수
function parseJwt(token) {

    if (!token || typeof token !== "string") {
        return null;
    }

    try {
        const parts = token.split(".");

        if (parts.length !== 3) {
            return null;
        }

        const base64Url = parts[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");

        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map(c =>
                    "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)
                )
                .join("")
        );

        return JSON.parse(jsonPayload);

    } catch (error) {
        console.error("JWT 파싱 실패:", error);
        return null;
    }
}

function getQueryParam(key) {
    const params = new URLSearchParams(window.location.search);
    return params.get(key);
}

function popupLayer(id = '', close_btn = false) {

    const popupId = id ? `id="${id}"` : '';
    let closeButton = "";

    if (close_btn) {
        closeButton = `
            <div class="wrapper">
                <button type="button" id="popup_close">
                    x
                </button>
                <div class="inner_wrapper"></div>
            </div>
        `;
    } else {
        closeButton = `<div class="wrapper">`;
    }


    const layer = `
        <div ${popupId} class="popup_layer">
            ${closeButton}            
        </div>
    `;

    const $layer = $(layer);
    $('body').append($layer);

    // 자기 자신만 닫기
    $layer.on("click", "#popup_close", function () {
        $layer.remove();
    });

    //return $layer; // 필요하면 외부에서 제어 가능
}

function myProfile(userId, callback) {

    $.ajax({
        url: `${SPRING_BOOT_URL}/zero9/users/${Number(userId)}/profile`,
        method: "GET",
        contentType: "application/json",
        headers: {
            Authorization: ACCESS_TOKEN
        },
        success: function (res) {
            callback(res.data);
        },
        error: function (jqXHR) {
            console.error("프로필 조회 실패:", jqXHR.responseJSON || jqXHR);

            if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
                alert(jqXHR.responseJSON.message);
            } else {
                alert("프로필 조회 중 오류가 발생했습니다.");
            }
        }
    });
}

// 페이징
function renderPagination(pageData) {
    const PAGING_WRAP = $(".page_nation");

    const totalPages = pageData.totalPages;
    const current = pageData.number;

    PAGING_WRAP.empty();

    if (totalPages <= 1) {
        return;
    }

    let html = "";

    // 이전
    if (current > 0) {
        html += `<button class="prev" type="button" data-page="${current - 1}"><i class="fa-solid fa-angle-left"></i></button>`;
    }

    // 페이지 번호
    for (let i = 0; i < totalPages; i++) {
        let active = i === current ? " active" : "";

        html += `<button class="page${active}" type="button" data-page="${i}">${i + 1}</button>`;
    }

    // 다음
    if (current < totalPages - 1) {
        html += `<button class="next" type="button" data-page="${current + 1}"><i class="fa-solid fa-angle-right"></i></button>`;
    }

    PAGING_WRAP.append(html);
}

// 년 월 일 시 분 초
function formatDate(dateStr) {
    const date = new Date(dateStr);

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');

    const hh = String(date.getHours()).padStart(2, '0');
    const min = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');

    return `${yyyy}.${mm}.${dd} ${hh}:${min}:${ss}`;
}

// --- [웹소켓 로직만 하단에 추가] ---
let stompClient = null;

function connectWebSocket() {

    const liveToken = localStorage.getItem("accessToken");
    const livePayload = parseJwt(liveToken);

    // 1. 카카오/일반 로그인 통합 ID 추출 (userId, id, sub 순서로 체크)
    const userId = livePayload ? (livePayload.userId || livePayload.id || livePayload.sub) : null;

    if (!userId || isTokenExpired(liveToken)) {
        console.warn("아이디가 없거나 토큰이 만료되어 연결을 중단합니다.");
        return;
    }

    // 이미 연결된 경우 중복 실행 방지
    if (stompClient && (stompClient.connected || stompClient.active)) return;

    try {
        const socket = new SockJS(`${SPRING_BOOT_URL}/ws_zero9`);

        // A. 구형 라이브러리 (Stomp) 대응
        if (typeof Stomp !== 'undefined') {
            stompClient = Stomp.over(socket);

            // 지저분한 PING/PONG 로그 끄기
            stompClient.debug = null;

            stompClient.connect({}, (frame) => {
                console.log(`Zero9 WebSocket 연결 성공! (ID: ${userId})`);

                // 1. 유저용: 본인 ID로 오는 메시지 구독
                stompClient.subscribe(`/queue/chat/${userId}`, (message) => {
                    console.log("새 메시지 도착:", message.body);
                    const data = JSON.parse(message.body);
                    showNotification(data);
                });

            // // 2. 관리자용: 만약 관리자 페이지라면 1번방 구독 필요
            //     if (userId == 1) {
            //         stompClient.subscribe(`/queue/chat/1`, (message) => {
            //             console.log("📢 관리자 알림 도착:", message.body);
            //         });
            //     }
            }, (err) => {
                console.error("STOMP Error:", err);
            });

            // B. 최신 라이브러리 (StompJs) 대응
        } else if (typeof StompJs !== 'undefined') {
            stompClient = new StompJs.Client({
                webSocketFactory: () => socket,
                reconnectDelay: 5000,
                // debug: (str) => console.log(str), // 필요하면 켬
                onConnect: (frame) => {
                    console.log(`Zero9 WebSocket 연결 성공! (ID: ${userId})`);
                    stompClient.subscribe(`/queue/chat/${userId}`, (m) => {
                        showNotification(JSON.parse(m.body));
                    });
                }
            });
            stompClient.activate();
        }
    } catch (e) {
        console.error("⚠웹소켓 초기화 중 에러 발생:", e);
    }
}

function showNotification(data) {
    const toastHtml = `
        <div class="chat_notification" style="position: fixed; top: 20px; right: 20px; background: #fff; border-left: 5px solid #007bff; padding: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 9999; border-radius: 4px; min-width: 250px;">
            <div style="font-weight: bold; margin-bottom: 5px; color: #333;">새 알림</div>
            <div style="font-size: 14px; color: #666;">${data.message || '새로운 메시지가 도착했습니다.'}</div>
        </div>
    `;
    $('body').append(toastHtml);
    setTimeout(() => {
        $(".chat_notification").fadeOut(500, function () {
            $(this).remove();
        });
    }, 4000);
}