<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<form action="${ctx}/books" method="get" class="filter">
    <div class="mb-3">
        <label class="form-label">Price</label>
        <div class="d-flex gap-2">
            <input type="number" min="0" class="form-control" name="min" value="${param.min}" placeholder="VNĐ từ">
            <input type="number" min="0" class="form-control" name="max" value="${param.max}" placeholder="VNĐ đến">
        </div>
    </div>

    <div class="mb-3">
        <label class="form-label">English Books</label>
        <select class="form-select" name="enCat">
            <option value="">Tất cả</option>
            <option value="fiction"   ${param.enCat=='fiction'?'selected':''}>Fiction</option>
            <option value="nonfiction"${param.enCat=='nonfiction'?'selected':''}>Non-fiction</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Sách tiếng Việt</label>
        <select class="form-select" name="vnCat">
            <option value="">Tất cả</option>
            <option value="vanhoc" ${param.vnCat=='vanhoc'?'selected':''}>Văn học</option>
            <option value="kinhte" ${param.vnCat=='kinhte'?'selected':''}>Kinh tế</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Thương hiệu</label>
        <select class="form-select" name="brand">
            <option value="">Tất cả</option>
            <option value="thienlong" ${param.brand=='thienlong'?'selected':''}>Thiên Long</option>
            <option value="kb"        ${param.brand=='kb'?'selected':''}>K&B Handmade</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Nhà cung cấp</label>
        <select class="form-select" name="vendor">
            <option value="">Tất cả</option>
            <option value="vinhthuy" ${param.vendor=='vinhthuy'?'selected':''}>Nhà Sách Vĩnh Thụy</option>
            <option value="abc"      ${param.vendor=='abc'?'selected':''}>HỆ THỐNG NHÀ SÁCH ABC</option>
        </select>
    </div>

    <button type="submit" class="button-three">Áp dụng</button>
</form>