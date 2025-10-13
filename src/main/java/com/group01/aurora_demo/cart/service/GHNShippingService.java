package com.group01.aurora_demo.cart.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class GHNShippingService {

    private static final String API_URL = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    private static final String TOKEN = "33f886ad-a5fc-11f0-bda8-6e91abd5be0d";
    private static final String SHOP_ID = "6056594";

    public double calculateFee(
            int fromDistrict, String fromWard,
            int toDistrict, String toWard, double weight,
            JSONArray items, Integer serviceId, Integer serviceTypeId) {

        try {
            JSONObject body = new JSONObject()
                    .put("from_district_id", fromDistrict)
                    .put("from_ward_code", fromWard)
                    .put("to_district_id", toDistrict)
                    .put("to_ward_code", toWard)
                    .put("weight", weight)
                    .put("items", items);

            body.put("service_id", serviceId != null ? serviceId : JSONObject.NULL);
            body.put("service_type_id", serviceTypeId != null ? serviceTypeId : JSONObject.NULL);

            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Token", TOKEN);
            conn.setRequestProperty("ShopId", SHOP_ID);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes("UTF-8"));
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                response.append(line);

            System.out.println("GHN Response: " + response);

            JSONObject json = new JSONObject(response.toString());
            JSONObject data = json.optJSONObject("data");

            return (data != null && data.has("total")) ? data.getDouble("total") : -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
