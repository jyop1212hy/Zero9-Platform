$(document).ready(function(){
    if(PAYLOAD.userRole !== "ADMIN"){
        alert("관리자만 접근 가능 합니다.");

        location.href = ORIGIN_URL + "/main.html";
    }

    const ADMIN_URL = ORIGIN_URL + '/admin';

    const HEADER_LAYER = `
        <header id="admin_header">
            <div class="header_top">
                <a href="${ORIGIN_URL}/main.html">메인으로</a>
                <a href="${ORIGIN_URL}/auth/logout.html">로그아웃</a>
            </div>

            <nav>
            </nav>
        </header>
    `;

    const ADMIN_MENU = {
        payment: {
            title: '결제',
            items: {
                '결제 조회': ADMIN_URL + '/payment_list.html'
            }
        },
        member: {
            title: '회원',
            items: {
                '인플루언서 가입 승인': ADMIN_URL + '/influencer_aproved.html'
            }
        }
    };

    $("body").prepend(HEADER_LAYER);

    const ADMIN_HEADER = $('#admin_header');

    $.each(ADMIN_MENU, function(groupKey, group) {

        ADMIN_HEADER.find('nav').append(`
            <div id="${groupKey}_menu_box" class="menu_box">
                <p class="menu_title">${group.title}</p>

                <ul class="list"></ul>
            </div>
        `);

        $.each(group.items, function(menuName, menuUrl) {
            $(`#${groupKey}_menu_box .list`).append(`
                <li>
                    <a href="${menuUrl}">${menuName}</a>
                </li>
            `);
        });
    });
});