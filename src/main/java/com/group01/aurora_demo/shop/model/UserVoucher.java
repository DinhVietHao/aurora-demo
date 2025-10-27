package com.group01.aurora_demo.shop.model;

public class UserVoucher {
    private Long userVoucherId;
    private Long voucherId;
    private Long userId;

    public Long getUserVoucherId() {
        return userVoucherId;
    }

    public void setUserVoucherId(Long userVoucherId) {
        this.userVoucherId = userVoucherId;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
