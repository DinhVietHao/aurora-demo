package com.group01.aurora_demo.cart.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderShopUtils {
    private static final String PREFIX = "AUR";
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_PART_LENGTH = 4;

    public String generateGroupOrderCode() {
        LocalDateTime now = LocalDateTime.now();
        String timePart = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < RANDOM_PART_LENGTH; i++) {
            randomPart.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        return PREFIX + timePart + randomPart.toString();
    }
}
