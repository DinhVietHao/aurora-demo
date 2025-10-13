package com.group01.aurora_demo.profile.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.group01.aurora_demo.profile.config.GHNConfig;

@WebServlet("/ghn")
public class GHNLocationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String type = request.getParameter("type"); // province | district | ward
        String provinceId = request.getParameter("province_id");
        String districtId = request.getParameter("district_id");

        String apiUrl;
        switch (type) {
            case "province" -> apiUrl = GHNConfig.BASE_URL + "/province";
            case "district" -> apiUrl = GHNConfig.BASE_URL + "/district?province_id=" + provinceId;
            case "ward" -> apiUrl = GHNConfig.BASE_URL + "/ward?district_id=" + districtId;
            default -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid type\"}");
                return;
            }
        }

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Token", GHNConfig.TOKEN);

            int status = conn.getResponseCode();
            InputStream inputStream = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            reader.close();

            response.getWriter().write(result.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
