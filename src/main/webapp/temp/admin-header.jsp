<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isAuthenticated" />
<sec:authorize access="hasRole('ADMIN')"  var="isAdmin" />
<sec:authorize access="hasRole('OPER_MFG_LINEOWNER')"  var="isMfgLineOwner" />
<sec:authorize access="hasRole('OPER_MFG')"  var="isMfgOper" />
<sec:authorize access="hasRole('OPER_FQC')"  var="isFqcOper" />
<sec:authorize access="hasRole('BACKDOOR_4876_')"  var="isBackDoor4876" />
<sec:authorize access="hasRole('OPER_IE')"  var="isIeOper" />
<sec:authorize access="hasRole('DATA_DEBUGGER')"  var="isDebugger" />
<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.7/css/bootstrap.min.css" />">
<link rel="stylesheet" href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css" />" >
<style>
    a[disabled] {
        pointer-events: none;
    }
    span{
        display: inline;
    }
    #logoImg{
        width: 40px;
        height: 25px;
    }
    body{
        font-family: 微軟正黑體;
    }
</style>
<script src="<c:url value="/webjars/bootstrap/3.3.7/js/bootstrap.min.js" />"></script>

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
    <fmt:bundle basename="options">
    $(function () {
        var mode = "<fmt:message key="bab.data.collect.mode" />".toLowerCase();

        $.getJSON("../../json/sitefloor.json", function (data) {
            var sitefloors = data.sitefloors;
            var navbar = $("#bs-example-navbar-collapse-1");
            for (var i = 0, j = sitefloors.length; i < j; i++) {
                var sitefloor = sitefloors[i].floor;
                navbar.find(".totalMapSelect").append("<li><a href='TotalMap?sitefloor=" + sitefloor + "'>狀態平面圖" + sitefloor + "F</a></li>");
//                navbar.find(".totalMapSelect").append("<li><a href='changeover.jsp?sitefloor=" + sitefloor + "'>換線" + sitefloor + "F</a></li>");
                navbar.find(".sensorAdjustSelect").append("<li><a href='" + (mode == "auto" ? "Sensor" : "Barcode") +
                        "Adjust?sitefloor=" + sitefloor + "'>" + sitefloor + "樓感應器狀態(校正用)</a></li>");
            }
        });

        $(".hide-when-" + mode).remove();
    });
    </fmt:bundle>
</script>
<!-- Navigation -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="SysInfo">
                <span><img id="logoImg" src="<c:url value="/images/bulb.png" />" alt="sysIcon" /></span>
                    ${initParam.pageTitle}
            </a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li>
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                        測試
                        <span class="caret" />
                    </a>
                    <ul class="dropdown-menu">
                        <c:if test="${isIeOper || isBackDoor4876 || isAdmin || isMfgOper}">
                            <li><a href="TestTotalDetail">測試線別狀態</a></li>
                            </c:if>
                        <li><a href="TestTotal">測試線別紀錄</a></li>
                            <c:if test="${isIeOper || isBackDoor4876 || isAdmin}">
                            <li><a href="testPassStationProductivity.jsp">MES測試過站查詢</a></li>
                            </c:if>
                    </ul>
                </li>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                        組裝
                        <span class="caret" />
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="BabTotal?lineType=ASSY">線平衡資訊查詢</a></li>
                        <li class="divider"></li>
                        <li class="hide-when-manual"><a href="BabDetailInfo">各站機台時間查詢</a></li>
                        <li><a href="BabDetailInfo2?lineType=ASSY">各站時間查詢(報表格式)</a></li>
                        <li class="divider"></li>
                        <li><a href="BabLineProductivity">線體效率查詢</a></li>
                        <li><a href="babLineUsageRate.jsp">線體使用率統計</a></li>
                        <li class="divider"></li>
                            <c:if test="${isIeOper || isBackDoor4876 || isAdmin || isMfgLineOwner || isMfgOper}">
                            <li><a href="ModelSopRemark">Sop維護</a></li>
                            <li class="divider"></li>
                            <li><a href="PreAssyModuleStandardTime">前置模組工時維護</a></li>
                            </c:if>
                        <li><a href="BabPreAssyDetail">前置資料查詢</a></li>
                        <li><a href="babModuleUsageRate.jsp?lineType=ASSY">前置機種模組使用狀態</a></li>
                        <li><a href="PreAssyPercentage">前置完程度查詢</a></li>
                        <li class="divider"></li>
                        <li class="hide-when-auto"><a href="BabPassStationRecord?lineType=ASSY">Barcode過站紀錄</a></li>
                        <li class="hide-when-auto"><a href="BabPassStationExceptionReport?lineType=ASSY">異常資料統計</a></li>
                        <li><a href="lineUserReference.jsp?lineType=ASSY">組裝當日線別人員維護</a></li>
                        <li><a href="prepareSchedule.jsp?lineType=ASSY">組裝當日自動排站</a></li>
                        <li class="divider"></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                        包裝
                        <span class="caret" />
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="BabTotal?lineType=Packing">線平衡資訊查詢</a></li>
                        <li class="divider"></li>
                        <li class="hide-when-manual"><a href="BabDetailInfo">各站機台時間查詢</a></li>
                        <li><a href="BabDetailInfo2?lineType=Packing">各站時間查詢(報表格式)</a></li>
                        <li class="divider"></li>
                            <c:if test="${isIeOper || isBackDoor4876 || isAdmin || isMfgLineOwner || isMfgOper}">
                            <li><a href="modelSopRemark.jsp">Sop維護</a></li>
                            <li class="divider"></li>
                            </c:if>
                        <li class="hide-when-auto"><a href="BabPassStationRecord?lineType=Packing">Barcode過站紀錄</a></li>
                        <li><a href="BabPreAssyProductivity?lineType=Packing">前置資料查詢</a></li>
                        <li><a href="babModuleUsageRate.jsp?lineType=Packing">前置機種模組使用狀態</a></li>
                        <li class="divider"></li>
                        <li><a href="babLineUsageRate.jsp">線體使用率統計</a></li>
                        <li class="divider"></li>
                        <li><a href="lineUserReference.jsp?lineType=Packing">附件盒當日線別人員維護</a></li>
                        <li><a href="prepareSchedule.jsp?lineType=Packing">附件盒當日自動排站</a></li>
                    </ul>
                </li>
                <li>
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                        FQC
                        <span class="caret" />
                    </a>
                    <ul class="dropdown-menu">
                        <!--<li><a href="FqcDashBoard?sitefloor=6">FQC效率</a></li>-->
                        <li><a href="FqcRecord">FQC效率記錄查詢</a></li>
                            <c:if test="${isFqcOper || isAdmin}">
                            <li><a href="FqcModelStandardTime">FQC標工維護</a></li>
                            </c:if>
                    </ul>
                </li>
                <li>
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                        平面圖
                        <span class="caret" />
                    </a>
                    <ul class="dropdown-menu totalMapSelect"></ul>
                </li>
                <c:if test="${isIeOper || isAdmin || isDebugger}">
                    <li>
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                            <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                            感應器
                            <span class="caret" />
                        </a>
                        <ul class="dropdown-menu sensorAdjustSelect">
                            <c:if test="${isAdmin || isMfgOper || isDebugger}">
                                <li><a href="SensorTest">Sensor檢測</a></li>
                                </c:if>
                        </ul>
                    </li>
                </c:if>
                <c:if test="${isIeOper || isAdmin || isDebugger}">
                    <li>
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                            <span class="glyphicon glyphicon-list-alt" aria-hidden="true" /> 
                            設定
                            <span class="caret" />
                        </a>
                        <ul class="dropdown-menu">
                            <c:if test="${isAdmin || isMfgOper || isDebugger}">
                                <li><a href="babSensorLoginRecord.jsp">Sensor登入登出設定</a></li>
                                </c:if>
                        </ul>
                    </li>
                </c:if>
            </ul>
            <ul class="nav navbar-nav navbar-right pull-right">
                <c:choose>
                    <c:when test="${!isAuthenticated}">
                        <li>
                            <a href="<c:url value="/login" />">
                                <span class="glyphicon glyphicon-log-in" />
                                login
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="dropdown">
                            <a href="" class="dropdown-toggle" data-toggle="dropdown">
                                <c:out value="${user.username}" />
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="ChangePassword">
                                        <span class="glyphicon glyphicon-lock" />
                                        更換密碼
                                    </a>
                                    <a href="<c:url value="/logout" />">
                                        <span class="glyphicon glyphicon-log-out" />
                                        logout
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container -->
</nav>
<!-- /.container -->

<!-- 為了省略include所造成多餘的<html><body>標籤而簡化，encoding會有問題還是要加上開頭 -->
