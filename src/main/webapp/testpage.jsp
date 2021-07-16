<%-- 
    Document   : getdata
    Created on : 2015/8/26, 上午 11:23:38
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <c:set var="userSitefloor" value="${param.sitefloor}" />
    <c:if test="${(userSitefloor == null) || (userSitefloor == '' || userSitefloor < 1 || userSitefloor > 7)}">
        <c:redirect url="/" />
    </c:if>
    <head> 
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>測試 ${userSitefloor} 樓 - ${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />"/>
        <style>
            #titleAlert{
                background-color: green;
                color: white;
                text-align: center;
            }
            .Div0{
                float:left; width:100%; 
                border-bottom-style:dotted;
                background-color: window;
            }
            .Div1{
                float:left; width:50%; 
                padding: 10px 10px;
            }
            .Div2{
                float:right;width:50%;
                padding-left: 10px;
            }

        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/webjars/momentjs/2.18.1/moment.js" /> "></script>
        <script src="<c:url value="/js/jquery.cookie.js" /> "></script>
        <script src="<c:url value="/webjars/jquery-blockui/2.70/jquery.blockUI.js" /> "></script>
        <script src="<c:url value="/js/cookie.check.js" /> "></script>
        <script src="<c:url value="/js/param.check.js" /> "></script>
        <script src="<c:url value="/js/cookie.generator.js"/>"></script>
        
        <script>

            var userInfoCookieName = "userInfo", testLineTypeCookieName = "testLineTypeCookieName", cellCookieName = "cellCookieName";
            var STATION_LOGIN = "LOGIN", STATION_LOGOUT = "LOGOUT", CHANGE_DECK = "CHANGE_DECK";
            var savedTable, savedJobnumber;
            var tabreg = /^[0-9a-zA-Z-]+$/;//Textbox check regex.
            
            shift_retrieve_url = "<c:url value="/UserShiftController/findDateShiftInfo" />";

            $(document).ready(function () {
                $(document).ajaxSend(function () {
                    block();//Block the screen when ajax is sending, Prevent form submit repeatly.
                });
                $(document).ajaxComplete(function () {
                    $.unblockUI();//Unblock the ajax when success
                });

                if (!are_cookies_enabled()) {
                    alert(cookie_disabled_message);
                    return;
                }

                var lines = getLine();
                for (var i = 0; i < lines.length; i++) {
                    setLineOptions(lines[i]);
                }

                findAndSetUserNotLogin();

                //Add class to transform the button type to bootstrap.
                $(":button").addClass("btn btn-default");
                $(":text,select,input[type='number']").addClass("form-control");

                console.log($.cookie(testLineTypeCookieName));
                var cookieCheckStatus = checkExistCookies();
                if (cookieCheckStatus == false) {
                    return false;
                }

                //Checking if user is login the babpage.jsp or not.(Get the cookie generate by babpage.jsp)
                //If not, login and check the user input values.
                $("#begin").click(function () {
                    if (confirm("確定登入?\n※MES系統將同步進行上線。")) {
                        modifyTestTable(STATION_LOGIN);
                    }
                });

                //TestTable logout.(Delete data from database)
                $("#end").click(function () {
                    if (confirm("確定登出?\n※MES系統將同步進行下線。")) {
                        modifyTestTable(STATION_LOGOUT);
                    }
                });

                $("#changeDeck").click(function () {
                    if (confirm("確定換桌?\n※MES系統\"不會\"進行下線。")) {
                        modifyTestTable(CHANGE_DECK);
                    }
                });

                $(document).on("keyup", "#user_number", function () {
                    textBoxToUpperCase($(this));
                });

            });
            
            function block() {
                $.blockUI({
                    css: {
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '10px',
                        '-moz-border-radius': '10px',
                        opacity: .5,
                        color: '#fff'
                    },
                    fadeIn: 0,
                    overlayCSS: {
                        backgroundColor: '#FFFFFF',
                        opacity: .3
                    }
                });
            }

            function checkExistCookies() {
                $("#cookieinfo").html("尚無資料");
                var testLineTypeCookie = $.cookie(testLineTypeCookieName);
                var babLineTypeCookie = $.cookie(userInfoCookieName);
                var cellCookie = $.cookie(cellCookieName);

                if (babLineTypeCookie || cellCookie) {
                    lockAllUserInput();
                    showMsg("您已經登入組包裝或Cell桌");
                    return false;
                }

                //Get values from cookie and setting html objects.
                if (testLineTypeCookie != null) {
                    var cookieInfo = $.parseJSON(testLineTypeCookie);
                    if (cookieInfo.floor == $("#userSitefloorSelect").val()) {
                        $("#user_number").val(cookieInfo.jobnumber);
                        $("#table").val(cookieInfo.tableNo);
                        lockWhenUserIsLogin();
                        $("#cookieinfo").html("測試 cookie 已經儲存");
                        $("#userInfo").html("<td>" + $("#table option:selected").text() + "</td>" + "<td>" + cookieInfo.jobnumber + "</td>");
                        return true;
                    } else {
                        lockAllUserInput();
                        showMsg("您已經登入其他樓層");
                        return false;
                    }
                } else {
                    unlockLoginInput();
                    return true;
                }
            }

            function setLineOptions(line) {
                $("#table").append("<option value=" + line.id + " " + (line.lock == 1 ? "disabled style='opacity:0.5'" : "") + ">線別 " + line.name + "</option>");
            }

            function getLine() {
                var result;
                $.ajax({
                    type: "Get",
                    url: "<c:url value="/TestTableController/findBySitefloor" />",
                    data: {
                        sitefloor: $("#userSitefloorSelect").val()
                    },
                    dataType: "json",
                    async: false,
                    success: function (response) {
                        result = response;
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
                return result;
            }

            function findAndSetUserNotLogin() {
                $.ajax({
                    type: "Get",
                    url: "<c:url value="/TestTableController/findUserNotLogin" />",
                    dataType: "json",
                    async: false,
                    success: function (response) {
                        var usersNotLoginMessageArea = $("#userNotLoginArea");
                        usersNotLoginMessageArea.append("<p>");
                        var i = 1;
                        Object.keys(response).forEach(function (key, index) {
                            usersNotLoginMessageArea.append(key);
                            usersNotLoginMessageArea.append("、");
                            if (i++ % 9 == 0) {
                                usersNotLoginMessageArea.append("<br />");
                            }
                        });
                        usersNotLoginMessageArea.append("</p>");
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function lockAllUserInput() {
                $(":input,select").not("#redirectBtn").attr("disabled", "disabled");
            }

            function modifyTestTable(action) {
                var jobnumber = $("#user_number").val();
                var tableNo = $("#table").val();
                var data = {
                    jobnumber: jobnumber,
                    tableNo: tableNo
                };

                if (!checkVal(data.tableNo) || !data.jobnumber.match(tabreg)) {
                    showMsg("error input value");
                    return false;
                }

                data.floor = $("#userSitefloorSelect").val();

                if (action == STATION_LOGIN) {
                    login(data);
                } else if (action == STATION_LOGOUT) {
                    logout(data);
                } else if (action == CHANGE_DECK) {
                    changeDesk(data);
                }
            }

            function login(data) {
                $.ajax({
                    type: "Post",
                    url: "<c:url value="/TestLineTypeController/login" />",
                    data: data,
                    dataType: "html",
                    success: function (response) {
                        if (response == "success") {
                            generateCookie(testLineTypeCookieName, JSON.stringify(data));
                            showMsg(response);
                            reload();
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function logout(data) {
                $.ajax({
                    type: "Post",
                    url: "<c:url value="/TestLineTypeController/logout" />",
                    data: data,
                    dataType: "html",
                    success: function (response) {
                        if (response == "success") {
                            $("#user_number,#table,#begin").removeAttr("disabled");
                            $(":text").val("");
                            removeTestLineTypeCookie();
                            showMsg(response);
                            reload();
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function changeDesk(data) {
                $.ajax({
                    type: "Post",
                    url: "<c:url value="/TestLineTypeController/changeDesk" />",
                    data: data,
                    dataType: "html",
                    success: function (response) {
                        if (response == "success") {
                            $("#user_number,#table,#begin").removeAttr("disabled");
                            $(":text").val("");
                            removeTestLineTypeCookie();
                            showMsg(response);
                            reload();
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function lockWhenUserIsLogin() {
                $("#user_number, #table, #begin").attr("disabled", true);
                $("#end, #changeDeck, #clearcookies").removeAttr("disabled");
            }

            function unlockLoginInput() {
                $("#user_number, #table, #begin").removeAttr("disabled");
                $("#end, #changeDeck, #clearcookies").attr("disabled", true);
            }

            function showTestInfo() {
                console.log($.cookie(testLineTypeCookieName));
            }

            //auto uppercase the textbox value(PO, ModelName)
            function textBoxToUpperCase(obj) {
                obj.val(obj.val().trim().toLocaleUpperCase());
            }

            //Logout the user saving cookie.
            function removeTestLineTypeCookie() {
                $.removeCookie(testLineTypeCookieName);
            }

            function reload() {
                window.location.reload();
            }

            function showMsg(msg) {
//                alert(msg);
                $("#servermsg").html(msg);
            }
        </script>
    </head>
    <body>
        <input id="userSitefloorSelect" type="hidden" value="${userSitefloor}">
        <div id="titleAlert">
            <c:out value="您所選擇的樓層是: ${userSitefloor}" />
            <a href="${pageContext.request.contextPath}">
                <button id="redirectBtn">不是我的樓層?</button>
            </a>
        </div>
        <jsp:include page="temp/head.jsp" />
        <c:forEach items="${cookie}" var="currentCookie">  
            <c:if test="${currentCookie.value.name eq 'user_number'}">
                <c:set var="key" value="${currentCookie.value.value}" />
            </c:if>
            <c:if test="${currentCookie.value.name eq 'table'}">
                <c:set var="tb" value="${currentCookie.value.value}" />
            </c:if>
        </c:forEach>

        <div class="Div0" id="inputpannel">
            <div class="Div1 form-inline">
                <input type="text" placeholder="刷入工號" id="user_number" ${key == null ?"":"disabled"} value="${key == null ?"":key}" maxlength="10">
                <select id="table" ${key == null ?"":"disabled"} value="${tb == null ?"":tb}">
                    <option value="-1">請選擇桌次</option>
                </select>
                <input type="button" value="開始" id="begin">
                <input type="button" value="結束" id="end">
                <input type="button" value="換桌" id="changeDeck">
            </div>
            <div class="Div2">
                <p><h3>步驟1:</h3>先在此處輸入您的<code>桌次</code>以及<code>工號</code>，確認之後按下開始。</p>
            </div>
        </div>
        <div class="Div0"> 
            <div class="Div1">
                <font id="smsg"><伺服器訊息:></font>
                <div id="servermsg"></div>  
            </div>
            <div class="Div2">
                <p><h3>步驟2:</h3>觀看伺服器訊息。</p>
            </div>
        </div>
        <div style="clear:both"></div>
        <div id="totaldata">
            <div class="Div0">
                <div class="Div1">
                    <table class="table table-bordered" style="text-align: center">
                        <tr>
                            <th>桌號</th>
                            <th>使用者代號</th>
                        </tr>
                        <tr id="userInfo">
                        </tr>
                        <c:remove var="key" />
                        <c:remove var="tb" />
                    </table>
                </div>
                <div class="Div2">
                    <h4>此處會紀錄您刷入的工號以及桌號。</h3>
                </div>
            </div>
            <div class="Div0">
                <div id="userNotLoginArea" class="Div1">未刷入資料庫的使用者:
                </div>
                <div class="Div2">
                    <p><h3>此處會顯示未刷入資料庫的使用者。</h3>刷入成功者請再次確認您的<code>姓名</code>不在這欄位中。<p/>
                </div>
            </div>
            <div class="Div0">
                <div id="cookieinfo" class="Div1"></div>
                <div class="Div2">
                    <p><h3>完成:</h3>恭喜完成資料儲存</p>
                </div>
            </div>
        </div>
        <jsp:include page="temp/footer.jsp" />
    </body>
</html>
