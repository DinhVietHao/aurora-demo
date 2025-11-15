package com.group01.aurora_demo.cart.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class GHNService {
    private static final String API_BASE = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order";
    private static final String TOKEN = "33f886ad-a5fc-11f0-bda8-6e91abd5be0d";
    private static final String SHOP_ID = "6056594";

    public Integer getAvailableServiceId(int fromDistrict, int toDistrict) {
        try {
            URL url = new URL(API_BASE + "/available-services");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Token", TOKEN);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject()
                    .put("shop_id", Integer.parseInt(SHOP_ID))
                    .put("from_district", fromDistrict)
                    .put("to_district", toDistrict);

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

            JSONObject json = new JSONObject(response.toString());
            JSONArray data = json.optJSONArray("data");

            if (data != null && data.length() > 0) {
                return data.getJSONObject(0).getInt("service_id");
            } else {
                System.err
                        .println("Không tìm thấy service_id hợp lệ giữa quận " + fromDistrict + " và " + toDistrict);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double calculateFee(
            int fromDistrict, String fromWard,
            int toDistrict, String toWard, double weight,
            JSONArray items, Integer serviceId, Integer serviceTypeId) {

        try {
            if (serviceId == null) {
                serviceId = getAvailableServiceId(fromDistrict, toDistrict);
                if (serviceId == null) {
                    System.err.println("Không thể tính phí vì không tìm thấy service_id hợp lệ.");
                    return -1;
                }
            }
            JSONObject body = new JSONObject()
                    .put("from_district_id", fromDistrict)
                    .put("from_ward_code", fromWard)
                    .put("to_district_id", toDistrict)
                    .put("to_ward_code", toWard)
                    .put("weight", weight)
                    .put("service_id", serviceId != null ? serviceId : JSONObject.NULL)
                    .put("service_type_id", serviceTypeId != null ? serviceTypeId : JSONObject.NULL)
                    .put("items", items);

            URL url = new URL(API_BASE + "/fee");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

            JSONObject json = new JSONObject(response.toString());
            JSONObject data = json.optJSONObject("data");

            return (data != null && data.has("total")) ? data.getDouble("total") : -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}