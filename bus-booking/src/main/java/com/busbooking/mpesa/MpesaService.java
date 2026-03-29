package com.busbooking.mpesa;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * M-Pesa Daraja API STK Push Integration
 * 
 * IMPORTANT: Replace the credentials below with your actual Safaricom Daraja API credentials.
 * Get them from https://developer.safaricom.co.ke/
 * 
 * For TESTING use sandbox: https://sandbox.safaricom.co.ke
 * For PRODUCTION use: https://api.safaricom.co.ke
 */
public class MpesaService {

    // ===== CONFIGURE THESE WITH YOUR DARAJA CREDENTIALS =====
    private static final String CONSUMER_KEY = "YOUR_CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "YOUR_CONSUMER_SECRET";
    private static final String BUSINESS_SHORT_CODE = "174379"; // Sandbox shortcode
    private static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"; // Sandbox passkey
    private static final String CALLBACK_URL = "https://yourdomain.com/mpesa/callback";
    
    // Use sandbox for testing, switch to api.safaricom.co.ke for production
    private static final String BASE_URL = "https://sandbox.safaricom.co.ke";
    // =========================================================

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Get OAuth access token from Daraja API
     */
    public static String getAccessToken() throws IOException {
        String credentials = Base64.getEncoder().encodeToString(
                (CONSUMER_KEY + ":" + CONSUMER_SECRET).getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(BASE_URL + "/oauth/v1/generate?grant_type=client_credentials")
                .addHeader("Authorization", "Basic " + credentials)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to get token: " + response.code());
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            return json.get("access_token").getAsString();
        }
    }

    /**
     * Initiate STK Push to customer's phone
     * @param phoneNumber - format: 2547XXXXXXXX (must start with 254)
     * @param amount - amount in KES
     * @param accountRef - booking reference
     * @return JSON response from M-Pesa
     */
    public static String initiateSTKPush(String phoneNumber, int amount, String accountRef) throws IOException {
        String accessToken = getAccessToken();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
                (BUSINESS_SHORT_CODE + PASSKEY + timestamp).getBytes(StandardCharsets.UTF_8));

        // Convert 07XX to 2547XX format
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "254" + phoneNumber.substring(1);
        }

        JsonObject body = new JsonObject();
        body.addProperty("BusinessShortCode", BUSINESS_SHORT_CODE);
        body.addProperty("Password", password);
        body.addProperty("Timestamp", timestamp);
        body.addProperty("TransactionType", "CustomerPayBillOnline");
        body.addProperty("Amount", amount);
        body.addProperty("PartyA", phoneNumber);
        body.addProperty("PartyB", BUSINESS_SHORT_CODE);
        body.addProperty("PhoneNumber", phoneNumber);
        body.addProperty("CallBackURL", CALLBACK_URL);
        body.addProperty("AccountReference", accountRef);
        body.addProperty("TransactionDesc", "Bus Ticket Payment - " + accountRef);

        RequestBody requestBody = RequestBody.create(
                body.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/mpesa/stkpush/v1/processrequest")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Query STK Push transaction status
     */
    public static String querySTKStatus(String checkoutRequestID) throws IOException {
        String accessToken = getAccessToken();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
                (BUSINESS_SHORT_CODE + PASSKEY + timestamp).getBytes(StandardCharsets.UTF_8));

        JsonObject body = new JsonObject();
        body.addProperty("BusinessShortCode", BUSINESS_SHORT_CODE);
        body.addProperty("Password", password);
        body.addProperty("Timestamp", timestamp);
        body.addProperty("CheckoutRequestID", checkoutRequestID);

        RequestBody requestBody = RequestBody.create(
                body.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/mpesa/stkpushquery/v1/query")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
