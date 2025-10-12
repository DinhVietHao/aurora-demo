<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${pageTitle != null ? pageTitle : 'Aurora'}</title>

<!-- Bootstrap & Icons -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

<!-- Project CSS -->
<link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
<link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" >
<link rel="stylesheet" href="${ctx}/assets/css/auth/auth_form.css?v=1.0.1" >

<!-- Admin -->
<link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css">
<link rel="stylesheet" href="${ctx}/assets/css/admin/adminDashboard.css">