$(document).ready(function(){

    const HEADER_LAYER = `
        <header id="header">
            <div class="feed_container swiper">
                <ul class="swiper-wrapper"></ul>
            </div>
            <div class="container">
                <div class="header_top">
                    <div class="left">
                        <a href="${ORIGIN_URL}/main.html">홈</a>
                    </div>
                        
                    <div class="right">
                        
                    </div>
                </div>

                <form id="search_form">
                    <div class="search_wrapper">
                        <div class="search_border">
                            <input id="search_logs" type="text" placeholder="상품게시물 또는 인플루언서 이름으로 검색이 가능합니다.">
                            <button type="submit">
                                <i class="fa-solid fa-magnifying-glass"></i>
                            </button>
                        </div>

                        <div class="gpp_ranking swiper">
                            <ul class="swiper-wrapper">
                            </ul>
                        </div>
                    </div>
                </form>

                <div class="rank_log_wrapper">
                </div>

                <nav class="header_nav">
                    <a href="${ORIGIN_URL}/goods/list.html">상품판매 게시물</a>
                    <a href="${ORIGIN_URL}/gp_post/list.html">공동구매 게시물</a>
                    <a href="${ORIGIN_URL}/notice/list.html">범용 게시물</a>
                </nav>
            </div>
        </header>
    `;

    $("body").prepend(HEADER_LAYER);

    const HEADER_WRAP = $("header");

    // 로그인 상태에 따른 버튼 처리
    let right_element;

    if(isTokenExpired(ACCESS_TOKEN)){
        right_element = `
            <a href="${ORIGIN_URL}/auth/login.html">로그인</a>
            <a href="${ORIGIN_URL}/joinpage/user_type.html">회원가입</a>
        `;
    } else {
        // [로그인 된 상태일 때 실행되는 구간]
        if (PAYLOAD && (PAYLOAD.userRole === "ADMIN" || PAYLOAD.role === "ADMIN")) {
            right_element = `
                <a href="${ORIGIN_URL}/admin/main.html">관리자</a>
                <a href="${ORIGIN_URL}/auth/logout.html">로그아웃</a>
            `;
        } else if (PAYLOAD){
            const rawRole = PAYLOAD.userRole || PAYLOAD.role || "USER";
            const cleanRole = rawRole.replace("ROLE_", "");

            const ROLE_MAP = {
                USER: "user",
                INFLUENCER: "influencer",
                ADMIN: "admin"
            };

            const userPath = ROLE_MAP[cleanRole] || "user";

            right_element = `
                <a href="${ORIGIN_URL}/mypage/${userPath}/home.html">마이페이지</a>
                <a href="${ORIGIN_URL}/auth/logout.html">로그아웃</a>
            `;
        }
    }

    // 웹소켓 버튼 그리기
    HEADER_WRAP.find(".header_top .right").html(right_element);

    // 웹소켓 연결
    if(!isTokenExpired(ACCESS_TOKEN)){
        const tryConnect = () => {
            // SockJS와 connectWebSocket 함수가 모두 있는지 확인
            if (typeof SockJS !== 'undefined' && typeof connectWebSocket === 'function') {
                try {
                    connectWebSocket();
                } catch(err) {
                    console.error("웹소켓 실행 중 에러:", err);
                }
            } else {
                console.warn("라이브러리 또는 common.js 로딩 대기 중... 0.5초 후 재시도");
                setTimeout(tryConnect, 500); // 성공할 때까지 재시도
            }
        };
        tryConnect();
    }

    $(document).on("submit", "#search_form", function(e){
        e.preventDefault();

       location.href = ORIGIN_URL + "/search_log/search.html?keyword=" + $("#search_logs").val();
    });

     // CSS 추가
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = "https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.css";
    document.head.appendChild(link);

    // JS 추가
    const script = document.createElement("script");
    script.src = "https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.js";
    script.defer = true;

    document.head.appendChild(script);

    $.ajax({
        url: `${SPRING_BOOT_URL}/zero9/feeds/all`,
        method: "GET",
        dataType: "json",
        success: function(res){

            const FEED_CONTAINER = $(".feed_container");
            const SWIPER_WRAPPER = FEED_CONTAINER.find(".swiper-wrapper");

            const DATA = res.data.content || [];

            // 🔥 PAYMENT_COUNT 제외 후 10개 자르기
            const FILTERED = DATA.filter(item => item.type !== "PAYMENT_COUNT");
            const TOP_10 = FILTERED.slice(0, 10);

            // 🔥 데이터 없으면 숨기고 종료
            if (TOP_10.length === 0) {
                FEED_CONTAINER.hide();
                return;
            }

            let html = "";

            TOP_10.forEach(item => {
                html += `
                    <li class="swiper-slide">
                        ${item.message}
                    </li>
                `;
            });

            SWIPER_WRAPPER.html(html);

            // 🔥 Swiper 실행
            new Swiper(".feed_container", {
                direction: "vertical",
                slidesPerView: 1,
                loop: true,
                autoplay: {
                    delay: 2000,
                    disableOnInteraction: false
                },
                speed: 600
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("전체 피드 불러오기 실패:", textStatus, errorThrown);

            // 🔥 실패해도 숨김 처리
            $(".feed_container").hide();

            alert("데이터를 불러오지 못했습니다");
        }
    });

    $.ajax({
        url: `${SPRING_BOOT_URL}/zero9/ranking/searchLog`,
        method: "GET",
        dataType: "json",
        success: function(res){
            const RANK_LOG = $(".rank_log_wrapper");
            const DATA = res.data || [];

            let html = "";

            DATA.forEach(item => {
                html += `
                    <span class="search_log_list" data-keyword="${item.keyword}">
                        #${item.keyword}
                    </span>
                `;
            });

            RANK_LOG.append(html);

           $(document).on("click", ".search_log_list", function(){
                $("#search_logs").val($(this).data("keyword"));
           });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("검색 기록 불러오기 실패:", textStatus, errorThrown);
            alert("데이터를 불러오지 못했습니다");
        }
    });

    $.ajax({
        url: `${SPRING_BOOT_URL}/zero9/ranking/gpp/total`,
        method: "GET",
        dataType: "json",
        success: function(res){
            const GPP_RANKING = $(".gpp_ranking  .swiper-wrapper");
            const DATA = res.data || [];

            let html = "";

            DATA.forEach(item => {
                html += `
                    <li class="swiper-slide">
                        ${item.rank}. ${item.productName}
                    </li>
                `;
            });

            GPP_RANKING.append(html);

            new Swiper(".gpp_ranking", {
                direction: "vertical",
                slidesPerView: 1,
                loop: true,
                autoplay: {
                    delay: 2000,
                    disableOnInteraction: false
                },
                speed: 600
            });
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error("전체 피드 불러오기 실패:", textStatus, errorThrown);
            alert("데이터를 불러오지 못했습니다");
        }
    });
});