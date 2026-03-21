$(document).ready(function(){
    myProfile(PAYLOAD.sub, function (profile) {

        const ROLE_TEXT = {
            USER: "일반",
            INFLUENCER: "인플루언서"
        };

        const { role, profileImg, nickname, phone, email } = profile;

        const MYPAGE_ASIDE = `
            <aside id="mypage_aside">
                <div class="my_profile_box">
                    <div class="my_role_data">
                        <div class="my_view">
                            <span class="profile_img">
                                <img src="${profileImg || '/img/noimg.jpg'}">
                            </span>
                            <span class="nickname">${nickname}</span>
                        </div>
                        <span class="role_tag">${ROLE_TEXT[role]}</span>
                    </div>

                    <div class="my_acount">
                        <div>
                            <span class="name">핸드폰</span>
                            <span class="data">${phone}</span>
                        </div>
                        <div>
                            <span class="name">이메일</span>
                            <span class="data">${email}</span>
                        </div>
                    </div>
                </div>

                <div class="aside_menu">
                    <div class="menu_section">
                        <p class="menu_name">주문 관리</p>
                        <ul class="menu_list">
                            <li>
                                <a href="/mypage/order_list.html">주문 내역 확인</a>
                            </li>
                        </ul>
                    </div>

                    <div class="menu_section">
                        <p class="menu_name">내 정보 관리</p>
                        <ul class="menu_list">
                            <li>
                                <a href="/mypage/edit_account.html">회원 정보 수정</a>
                            </li>

                            <li>
                                <a href="/mypage/user_withdrawal.html">회원 탈퇴</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </aside>
        `;

        $(".mypage_main .container").prepend(MYPAGE_ASIDE);

        const ASIDE_MENU = $("#mypage_aside .aside_menu");

        const MENU_MAP = {
            INFLUENCER: [
                {
                    menu: "상품",
                    list: {
                        "판매 상품 내역": "product_post_list.html",
                        "상품 등록": "product_post_create.html",
                        "공동 구매 게시물 등록": "group_purchase_post_create.html",
                    }
                }
            ],
            USER: [
                {
                    menu: "내 활동 정보 관리",
                    list: {
                        "공동 구매 게시물 팔로우 목록": "gp_post_follow_list.html",
                        "상품 판매 게시물 찜 목록": "ggim_list.html",
                        "범용 게시물 목록": ""
                    }
                }
            ]
        };

        let menuHtml = "";

        MENU_MAP[role]?.forEach(section => {

            let listHtml = "";

            Object.entries(section.list).forEach(([name, url]) => {
                listHtml += `
                    <li>
                        <a href="${url}">${name}</a>
                    </li>
                `;
            });

            menuHtml += `
                <div class="menu_section">
                    <p class="menu_name">${section.menu}</p>
                    <ul class="menu_list">
                        ${listHtml}
                    </ul>
                </div>
            `;
        });

        ASIDE_MENU.prepend(menuHtml);
    });

});