// chat.js (채팅 페이지용)
// 1. 메시지 수신 시 실행될 콜백 함수 (common.js에서 호출함)
function onMessageReceived(data) {
    console.log("채팅 페이지 수신 데이터:", data);
    appendMessageToChat(data);
}

// 2. 메시지를 화면에 추가하는 함수
function appendMessageToChat(data) {
    const $chatArea = $('#chat_area'); // 채팅창 컨테이너 ID
    if ($chatArea.length === 0) return;

    // 내가 보낸 건지 확인 (PAYLOAD는 common.js에 선언됨)
    const isMine = data.senderId === PAYLOAD.userId;
    const msgClass = isMine ? 'sent' : 'received';

    // 시간 포맷팅 (common.js의 formatDate 활용)
    const timeStr = formatDate(new Date());

    const msgHtml = `
        <div class="message_wrapper ${msgClass}">
            <div class="bubble">${data.message}</div>
            <div class="time">${timeStr}</div>
        </div>
    `;

    $chatArea.append(msgHtml);

    // 메시지 추가 후 자동 스크롤 하단 이동
    $chatArea.scrollTop($chatArea[0].scrollHeight);
}

// 3. 메시지 전송 함수 (버튼 클릭 시 호출)
function sendMessage() {
    const $msgInput = $("#msg");
    const text = $msgInput.val();
    const receiverId = getQueryParam("targetId"); // URL 파라미터에서 상대방 ID 추출

    if (!text.trim()) return;

    if (stompClient && stompClient.connected) {
    const message = {
        senderId: PAYLOAD.userId, // 공통 코드의 PAYLOAD 활용!
        receiverId: Number(receiverId),
        message: text
    };

    stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify(message)
    });

    // 내가 보낸 메시지도 화면에 즉시 표시 (서버에서 다시 안 보내줄 경우 대비)
    appendMessageToChat(message);

        // 입력창 비우기
        $msgInput.val("");
    } else {
        alert("서버와 연결이 끊겨 있습니다. 잠시 후 다시 시도해주세요.");
    }
}

// 엔터키 전송 이벤트 추가
$(document).on('keypress', '#msg', function(e) {
    if (e.which == 13) {
        sendMessage();
    }
});