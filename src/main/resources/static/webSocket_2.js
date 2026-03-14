const userId = 2;

const socket = new SockJS("http://localhost:8080/ws_zero9");

const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {

    console.log("User2 WebSocket 연결 성공");

    stompClient.subscribe("/queue/chat/" + userId, function (msg) {

        console.log("User2 수신:", JSON.parse(msg.body));

    });

});

function sendMessage() {

    const text = document.getElementById("msg").value;

    const message = {
        senderId: 2,
        receiverId: 1,
        message: text
    };

    stompClient.send("/app/chat", {}, JSON.stringify(message));

}