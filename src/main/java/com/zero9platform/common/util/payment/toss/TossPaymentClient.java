package com.zero9platform.common.util.payment.toss;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${toss.payment.secret-key}")
    private String SECRET_KEY; // 환경 변수로 지정 해야함.

    /**
     * 토스페이먼츠 결제 승인
     */
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
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new CustomException(ExceptionCode.TOSS_PAYMENT_CONFIRM_FAIL);
            }
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.TOSS_PAYMENT_CONFIRM_ERROR);
        }
    }

    /**
     * 토스페이먼츠 결제 취소
     */
    public void cancelPayment(String paymentKey, String cancelReason) {

        String body = String.format("""
        {
          "cancelReason": "%s"
        }
        """, cancelReason);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", "Basic " + base64SecretKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new CustomException(ExceptionCode.TOSS_PAYMENT_CANCEL_FAIL);
            }
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.TOSS_PAYMENT_CANCEL_ERROR);
        }
    }

    private String base64SecretKey() {
        return Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
    }
}