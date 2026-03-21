// 1. 초기 설정 및 사용자 ID 정의
const userId = 1;

// const socket = new SockJS("http://localhost:8080/ws_zero9");
//
// const stompClient = Stomp.over(socket);
//
// stompClient.connect({}, function () {
//
//     console.log("User1 WebSocket 연결 성공");
//
//     stompClient.subscribe("/queue/chat/" + userId, function (msg) {
//
//         console.log("User1 수신:", JSON.parse(msg.body));
//
//     });

// 2. STOMP 클라이언트 객체 생성 (재연결 및 하트비트 설정 포함)
const client = new StompJs.Client({

    // sock JS를 사용할 때는 brokerURL 대신 webSocketFactory를 사용한다.
    webSocketFactory: () => new SockJS("http://localhost:8080/ws_zero9"),

    reconnectDelay: 5000, // 연결 끊길 시 5초 후 자동 재연결 (안정성 핵심)
    heartbeatIncoming: 10000, // 서버로부터 받는 하트비트 주기 (10초)
    heartbeatOutgoing: 10000, // 서버로 보내는 하트비트 주기 (10초)

    // 디버그 로그 (개발 중 확인용, 운영 시 삭제 가능)
    debug: function (str) {
        console.log('STOMP Debug: ' + str);
    }
});

// 3. 연결 성공 시 실행될 콜백
client.onConnect = (frame) => {
    console.log('Zero9 WebSocket 연결 성공! 사용자 ID: ' + userId);

// 개인 채팅 메시지 구독 (서버의 /queue 접두사와 매칭)
    client.subscribe("/queue/chat/" + userId, (message) => {
        const receivedData = JSON.parse(message.body);
        console.log("메시지 수신:", receivedData);
        // 여기서 화면에 메시지를 뿌려주는 로직을 넣으면 됩니다.
    });
};

// 4. 오류 및 연결 종료 처리
client.onStompError = (frame) => {
    console.error('STOMP 프로토콜 오류:', frame.headers['message']);
};

client.onWebSocketClose = () => {
    console.log('웹소켓 연결이 닫혔습니다. (자동 재연결 대기 중...)');
};

// 5. 실제 연결 활성화
client.activate();

// 6. 메시지 전송 함수
function sendMessage() {
    const text = document.getElementById("msg").value;
    if (!text) return;

    const message = {
        senderId: userId,
        receiverId: 2, // 예시 상대방 ID
        message: text
    };

    // client.publish 방식을 권장합니다.
    client.publish({
        destination: "/app/chat",
        body: JSON.stringify(message)
    });

    document.getElementById("msg").value = ""; // 입력창 초기화
}