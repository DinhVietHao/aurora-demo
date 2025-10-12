package com.group01.aurora_demo.common.util;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public final class Json {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    private Json() {
    }

    public static void send(HttpServletResponse resp, int status, Object body) {
        resp.setStatus(status);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        try (PrintWriter out = resp.getWriter()) {
            out.print(GSON.toJson(body));
        } catch (IOException e) {
            System.err.println("[Json] Lỗi ghi JSON: " + e.getMessage());
        }
    }

    public static void sendOk(HttpServletResponse resp, Object body) {
        send(resp, HttpServletResponse.SC_OK, body);
    }

    public static void sendError(HttpServletResponse resp, int status, String message) {
        send(resp, status, Map.of("ok", false, "message", message));
    }
}