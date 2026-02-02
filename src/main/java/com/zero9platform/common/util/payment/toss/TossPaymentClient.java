package com.zero9platform.common.util.payment.toss;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Component
public class TossPaymentClient {

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String SECRET_KEY = "test_sk_5OWRapdA8dQk67BBBmKn3o1zEqZK"; // 환경 변수로 지정 해야함.

    public void tossPayment(String paymentKey, String orderId, int amount) {

        String body = String.format("""
            {
              "paymentKey": "%s",
              "orderId": "%s",
              "amount": %d
            }
        """, paymentKey, orderId, amount);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIRM_URL))
                .header("Authorization", "Basic " + base64SecretKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("토스 결제 승인 실패: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("토스 결제 승인 중 오류", e);
        }
    }

    private String base64SecretKey() {
        return Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
    }
}


