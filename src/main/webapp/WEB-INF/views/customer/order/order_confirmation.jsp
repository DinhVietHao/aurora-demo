<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Xác nhận đơn hàng - Aurora</title>
            </head>

            <body style="margin:0; padding:0; background-color:#F3F3F5; font-family:Arial, sans-serif;">
                <table width="100%" cellpadding="0" cellspacing="0" border="0" bgcolor="#F3F3F5"
                    style="padding:0; margin:0;">
                    <tr>
                        <td align="center" valign="top">

                            <table width="640" cellpadding="0" cellspacing="0" border="0" bgcolor="#ffffff"
                                style="width:640px; max-width:640px; border-collapse:collapse;">

                                <!-- Header -->
                                <tr>
                                    <td align="center" bgcolor="#164e3f" style="padding:10px 0;">
                                        <img src="https://res.cloudinary.com/drdvkcf6m/image/upload/v1761456484/logo-footer.png"
                                            alt="Aurora Logo" style="width:230px; height:auto; display:block;">
                                        <div style="font-size:14px; line-height:18px; color:#FCC800;">
                                            Beyond Books, Into Everything
                                        </div>
                                    </td>
                                </tr>

                                <!-- Title + Intro -->
                                <tr>
                                    <td style="padding:20px 32px 0 32px; color:#000000;">
                                        <h4 style="margin:0; font-size:23px; font-weight:600; color:#164e3f;">
                                            🎉 Đơn hàng của bạn đã được xác nhận!
                                        </h4>
                                        <p style="margin:10px 0; font-size:15px;">
                                            Cảm ơn bạn đã mua sắm tại
                                            <strong style="color:#164e3f; font-size:20px;">Aurora</strong>.
                                            Chúng tôi đang chuẩn bị đơn hàng cho bạn.
                                        </p>
                                    </td>
                                </tr>

                                <!-- Order Info + Items -->
                                <tr>
                                    <td style="padding:0 32px;">
                                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                                            style="border:1px solid #eee; border-collapse:collapse; margin-top:16px;">
                                            <tr>
                                                <td style="padding:16px; font-size:14px; color:#000;">
                                                    <strong>Mã đơn hàng:</strong> #AURORA${orderShops[0].paymentId}<br>
                                                    <strong>Ngày đặt:</strong>
                                                    <fmt:formatDate value="${orderShops[0].createdAt}"
                                                        pattern="dd/MM/yyyy" />
                                                </td>
                                            </tr>

                                            <!-- List Items -->
                                            <c:forEach var="item" items="${orderShops}">
                                                <tr>
                                                    <td style="border-top:1px solid #eee; padding:12px;">
                                                        <table width="100%" cellpadding="0" cellspacing="0" border="0">
                                                            <tr>
                                                                <td width="70" valign="top">
                                                                    <img src="https://res.cloudinary.com/drdvkcf6m/image/upload/v1761456484/${item.imageUrl}"
                                                                        alt="${item.productName}"
                                                                        style="width:60px; height:80px; object-fit:cover; display:block;">
                                                                </td>
                                                                <td valign="center" style="font-size:14px; color:#000;">
                                                                    ${item.productName}
                                                                </td>
                                                                <td align="right" valign="center"
                                                                    style="font-size:14px;">
                                                                    SL: ${item.quantity}<br>
                                                                    <c:if
                                                                        test="${item.originalPrice != item.salePrice}">
                                                                        <span
                                                                            style="text-decoration:line-through; opacity:0.6; color:#6A7282; font-size:12px;">
                                                                            <fmt:formatNumber
                                                                                value="${item.originalPrice}"
                                                                                type="currency" currencySymbol="₫" />
                                                                        </span>
                                                                    </c:if>
                                                                    <strong style="color:#164e3f;">
                                                                        <fmt:formatNumber value="${item.salePrice}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </strong>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Price Summary -->
                                <tr>
                                    <td style="padding:16px 32px;">
                                        <table width="100%" cellpadding="4" cellspacing="0" border="0"
                                            style="border:1px solid #eee; border-collapse:collapse;">

                                            <tr>
                                                <td style="font-size:14px;">Tạm tính :</td>
                                                <td align="right" style="font-size:14px;">
                                                    <fmt:formatNumber value="${totalItems}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                            </tr>

                                            <tr>
                                                <td style="font-size:14px;">Tổng tiền phí vận chuyển :</td>
                                                <td align="right" style="font-size:14px;">
                                                    <fmt:formatNumber value="${totalShipping}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                            </tr>

                                            <tr>
                                                <td style="font-size:14px; color:#ef4444;">Tổng cộng Voucher giảm giá :
                                                </td>
                                                <td align="right" style="font-size:14px; color:#ef4444;">
                                                    -
                                                    <fmt:formatNumber value="${totalVoucherDiscount}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                            </tr>

                                            <tr>
                                                <td style="font-size:14px; color:#ef4444;">Giảm giá phí vận chuyển :
                                                </td>
                                                <td align="right" style="font-size:14px; color:#ef4444;">
                                                    -
                                                    <fmt:formatNumber value="${totalShipDiscount}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                            </tr>

                                            <tr>
                                                <td colspan="2" style="border-top:1px solid #eee;"></td>
                                            </tr>

                                            <tr>
                                                <td style="font-size:16px; font-weight:bold; color:#164e3f;">Tổng cộng:
                                                </td>
                                                <td align="right"
                                                    style="font-size:18px; font-weight:bold; color:#164e3f;">
                                                    <fmt:formatNumber value="${grandTotal}" type="currency"
                                                        currencySymbol="₫" maxFractionDigits="0" />
                                                </td>
                                            </tr>

                                        </table>
                                    </td>
                                </tr>

                                <!-- Footer -->
                                <tr>
                                    <td align="center" bgcolor="#f8f9fa"
                                        style="font-size:13px; color:#888; padding:20px;">
                                        Aurora - Beyond Books, Into Everything<br>
                                        © 2025 Aurora. All rights reserved.
                                    </td>
                                </tr>

                            </table>

                        </td>
                    </tr>
                </table>

            </body>

            </html>