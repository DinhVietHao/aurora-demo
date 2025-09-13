package com.group01.aurora_demo.auth.service;

import com.group01.aurora_demo.auth.dao.RememberMeTokenDAO;
import com.group01.aurora_demo.auth.dao.UserDAO;
import com.group01.aurora_demo.auth.model.User;
import jakarta.servlet.http.*;

import java.time.temporal.ChronoUnit;
import java.security.MessageDigest;
import java.util.function.Supplier;
import java.security.SecureRandom;
import java.util.Optional;
import java.time.Instant;
import java.util.Base64;

public class RememberMeService {
    public static final String COOKIE_NAME = "AURORA_RM";
    private static final int DAYS = 30;

    private final Supplier<Boolean> secureFlagSupplier;
    private final RememberMeTokenDAO tokenDAO;
    private final UserDAO userDAO;

    public RememberMeService(RememberMeTokenDAO tokenDAO, UserDAO userDAO, Supplier<Boolean> secureFlagSupplier) {
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
        this.secureFlagSupplier = secureFlagSupplier;
    }

    public void issueToken(User user, HttpServletResponse resp) throws Exception {
        SecureRandom rnd = new SecureRandom();
        byte[] selectorBytes = new byte[9];
        byte[] validatorBytes = new byte[32];
        rnd.nextBytes(selectorBytes);
        rnd.nextBytes(validatorBytes);

        String selector = b64(selectorBytes);
        String validator = b64(validatorBytes);
        byte[] validatorHash = sha256(validatorBytes);
        Instant expires = Instant.now().plus(DAYS, ChronoUnit.DAYS);

        tokenDAO.insert(user.getId(), selector, validatorHash, expires);

        String cookieValue = selector + ":" + validator;
        int maxAge = (int) ChronoUnit.SECONDS.between(Instant.now(), expires);
        addCookie(resp, COOKIE_NAME, cookieValue, maxAge);
    }

    public Optional<User> tryAutoLogin(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String raw = readCookie(req, COOKIE_NAME);
            if (raw == null || !raw.contains(":"))
                return Optional.empty();
            String[] parts = raw.split(":", 2);
            String selector = parts[0];
            String validatorB64 = parts[1];

            RememberMeTokenDAO.TokenRow row = tokenDAO.findBySelector(selector);
            if (row == null) {
                deleteCookie(resp, COOKIE_NAME);
                return Optional.empty();
            }
            if (Instant.now().isAfter(row.expiresAt)) {
                tokenDAO.deleteByTokenId(row.tokenId);
                deleteCookie(resp, COOKIE_NAME);
                return Optional.empty();
            }

            byte[] validatorBytes = Base64.getUrlDecoder().decode(validatorB64);
            byte[] validatorHash = sha256(validatorBytes);

            if (!MessageDigest.isEqual(validatorHash, row.validatorHash)) {
                tokenDAO.deleteByTokenId(row.tokenId);
                deleteCookie(resp, COOKIE_NAME);
                return Optional.empty();
            }

            User u = userDAO.findById(row.userId);
            if (u == null) {
                tokenDAO.deleteByTokenId(row.tokenId);
                deleteCookie(resp, COOKIE_NAME);
                return Optional.empty();
            }
            return Optional.of(u);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void clearAllForUser(long userId, HttpServletResponse resp) {
        tokenDAO.deleteAllForUser(userId);
        deleteCookie(resp, COOKIE_NAME);
    }

    private static byte[] sha256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    private static String b64(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private void addCookie(HttpServletResponse resp, String name, String value, int maxAgeSeconds) {
        boolean secure = secureFlagSupplier.get() != null && secureFlagSupplier.get();
        Cookie ck = new Cookie(name, value);
        ck.setHttpOnly(true);
        ck.setPath("/");
        ck.setMaxAge(maxAgeSeconds);
        ck.setSecure(secure);
        resp.addCookie(ck);
        resp.addHeader("Set-Cookie", name + "=" + value + "; Path=/; Max-Age=" + maxAgeSeconds +
                (secure ? "; Secure" : "") + "; HttpOnly; SameSite=Lax");
    }

    private void deleteCookie(HttpServletResponse resp, String name) {
        boolean secure = secureFlagSupplier.get() != null && secureFlagSupplier.get();
        Cookie ck = new Cookie(name, "");
        ck.setHttpOnly(true);
        ck.setPath("/");
        ck.setMaxAge(0);
        ck.setSecure(secure);
        resp.addCookie(ck);
        resp.addHeader("Set-Cookie", name + "=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] arr = req.getCookies();
        if (arr == null)
            return null;
        for (Cookie c : arr)
            if (name.equals(c.getName()))
                return c.getValue();
        return null;
    }
}