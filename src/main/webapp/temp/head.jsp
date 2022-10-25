<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.7/css/bootstrap.min.css" />">
<link rel="stylesheet" href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css" />" >
<link rel="stylesheet" href="<c:url value="/webjars/bootstrap-switch/3.3.4/dist/css/bootstrap3/bootstrap-switch.min.css" />">
<style>
    a[disabled] {
        pointer-events: none;
    }
    body{
        font-family: 微軟正黑體;
    }
</style>
<script src="<c:url value="/webjars/bootstrap/3.3.7/js/bootstrap.min.js" />"></script>
<script src="<c:url value="/webjars/bootstrap-switch/3.3.4/dist/js/bootstrap-switch.min.js" />"></script>

<div style="text-align:center; color: red">
    <noscript>For full functionality of this page it is necessary to enable JavaScript. Here are the <a href="http://www.enable-javascript.com" target="_blank"> instructions how to enable JavaScript in your web browser</a></noscript>
</div>
<div id="jquery-require-message" style="text-align:center; color: red">
</div>
<script>
    if (!window.jQuery) {
        document.getElementById("jquery-require-message").innerHTML =
                "Sorry, this page require jquery plugin\
                , please check your system environment or contact system administrator";
    }
</script>
<c:if test="${initParam.betaMode == 1}">
    <jsp:include page="_beta.jsp" />
</c:if>
<div class="text-right">
    <h5>
        <a href="<c:url value="?locale=en_US" />">
            <fmt:message key="label.lang.en" />
        </a>
        <a href="<c:url value="?locale=zh_TW" />">
            <fmt:message key="label.lang.tw" />
        </a>
    </h5>
</div>
<!-- 為了省略include所造成多餘的<html><body>標籤而簡化，encoding會有問題還是要加上開頭 -->
