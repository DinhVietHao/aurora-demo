package com.group01.aurora_demo.auth.model;

public record OtpResponse(boolean ok, String message, int expiresIn) {
}