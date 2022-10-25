<%-- 
    Document   : internationalization
    Created on : 2022/9/5, 下午 02:18:43
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>

<fmt:setLocale value="zh_TW" scope="application" />
<fmt:setBundle basename="classpath:i18n/messages" scope="session"/>
