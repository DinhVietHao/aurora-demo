<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />

        <form action="${ctx}/home" method="GET" class="filter">
            <input type="hidden" name="action" value="filter" />

            <div class="mb-3">
                <label class="form-label">Giá (VNĐ)</label>
                <div class="d-flex gap-2">
                    <input type="number" min="0" class="form-control" name="minPrice" value="${param.minPrice}"
                        placeholder="Từ">
                    <input type="number" min="0" class="form-control" name="maxPrice" value="${param.maxPrice}"
                        placeholder="Đến">
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Thể loại</label>
                <select class="form-select" name="category">
                    <option value="">Tất cả</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${param.category==cat ? 'selected' : '' }>${cat}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">Tác giả</label>
                <select class="form-select" name="author">
                    <option value="">Tất cả</option>
                    <c:forEach var="auth" items="${authors}">
                        <option value="${auth}" ${param.author==auth ? 'selected' : '' }>${auth}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">Nhà xuất bản</label>
                <select class="form-select" name="publisher">
                    <option value="">Tất cả</option>
                    <c:forEach var="pub" items="${publishers}">
                        <option value="${pub}" ${param.publisher==pub ? 'selected' : '' }>${pub}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">Ngôn ngữ</label>
                <select class="form-select" name="language">
                    <option value="">Tất cả</option>
                    <c:forEach var="lang" items="${languages}">
                        <option value="${lang}" ${param.language==lang ? 'selected' : '' }>${lang}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="button-three">Áp dụng</button>
        </form>