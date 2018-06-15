<%-- 
    Document   : babpage1
    Created on : 2016/8/1, 上午 08:58:43
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
        <title>組包裝 ${userSitefloor} 樓 - ${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="images/favicon.ico"/>
        <link rel="stylesheet" href="css/jquery-ui.css">
        <style>
            #titleAlert{
                background-color: green;
                color: white;
                text-align: center;
            }
            .step{
                float:left; 
                width:100%; 
                /*border-bottom-style:dotted;*/
                border-left: solid 2px;
                border-bottom: solid 2px;
                border-right: solid 2px;
            }
            #step1{
                background-color: rosybrown;
            }
            #step2{
                background-color: bisque;
            }
            #step3{
                background-color: peachpuff;
            }
            #step4{
                background-color: gainsboro;
            }
            .userWiget{
                float:left; 
                width:60%; 
                padding: 10px 10px;
            }
            .wigetInfo{
                float:right;
                width:40%;
            }
            .userWiget > .alarm{
                padding-top: 5px;
            }
            .stepAlarm{
                border-color: red;
            }
            li{
                line-height: 20px;
            }
            .importantMsg{
                color: red;
            }
            .alarm{
                color: red;
            }
            #firstStationWiget table tr td{
                padding: 5px;
            }
            #publicWiget table tr td{
                padding: 5px;
            }
            #otherStationWiget table tr td{
                padding: 5px;
            }
        </style>
        <script src="js/jquery-1.11.3.min.js"></script>
        <script src="js/jquery-ui.min.js"></script>
        <script src="js/jquery-ui-1.10.0.custom.min.js"></script>
        <script src="js/jquery.blockUI.js"></script>
        <script src="js/jquery.cookie.js"></script>
        <script src="js/param.check.js"></script>
        <script src="js/moment.js"></script>

        <script>
            var hnd;//鍵盤輸入間隔
            var hnd2;//鍵盤輸入間隔
            var serverErrorConnMessage = "Error, the textbox can't connect to server now.";
            var userNotFoundMessage = "使用者不存在，請重新確認，如有問題請通知系統管理人員。",
                    paramNotVaildMessage = "輸入資料有誤，請重新再確認。";

            var userInfoCookieName = "userInfo", testLineTypeCookieName = "testLineTypeCookieName", cellCookieName = "cellCookieName";
            var STATION_LOGIN = "LOGIN", STATION_LOGOUT = "LOGOUT", CHANGE_USER = "changeUser";
            var BAB_END = "stationComplete";

            var serverMsgTimeout;

            var firstStation = 1;

            var tabreg = /^[0-9a-zA-Z-]+$/;//Textbox check regex.

            $(function () {
                $(document).ajaxSend(function () {
                    block();//Block the screen when ajax is sending, Prevent form submit repeatly.
                });
                $(document).ajaxComplete(function () {
                    $.unblockUI();//Unblock the ajax when success
                });

                $("select, :text, :button").addClass("form-control");
                $("#reSearch, #people, #firstStationWiget, #otherStationWiget, #saveNotice").hide();

                var userInfoCookie = $.cookie(userInfoCookieName);
                var isUserInfoExist = (userInfoCookie != null);

                var dialogMessage = $("#dialog-message").dialog({
                    autoOpen: false,
                    resizable: false,
                    height: "auto",
                    width: 400,
                    modal: true,
                    buttons: {
                        "確定": function () {
                            var newJobnumber = $("#newJobnumber").val();
                            if (!checkVal(newJobnumber) || !checkUserExist(newJobnumber)) {
                                showMsg(userNotFoundMessage);
                                $("#newJobnumber").val("");
                                $(this).dialog("close");
                                return false;
                            } else {
                                changeJobnumber(newJobnumber);
                                $(this).dialog("close");
                            }
                        },
                        "取消": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                //Don't set event on dom when user cookie info is not vaild.
                var cookieCheckStatus = checkExistCookies();
                if (cookieCheckStatus == false) {
                    return false;
                }

                initUserInputWiget();

                $("#step1").find(":text, select").attr("disabled", isUserInfoExist);

                $("#saveInfo").attr("disabled", isUserInfoExist);
                $("#clearInfo, #changeUser").attr("disabled", !isUserInfoExist);

                $(":text").keyup(function () {
                    textBoxToUpperCase($(this));
                }).focus(function () {
                    $(this).select();
                });

                //Step 1 event
                //儲存使用者資訊
                $("#saveInfo").click(function () {
                    var jobnumber = $("#jobnumber").val();
                    var tagName = $("#tagName").val();
                    saveUserStatus(tagName, jobnumber);
                });

                //當找不到資訊時，相關event註冊到此(只有saveInfo後後續event才會依序註冊)
                if (!isUserInfoExist) {
                    return false;
                }

                //重設使用者資訊
                $("#clearInfo").click(function () {
                    if (confirm("確定離開此站別?")) {
                        if (!isUserInfoExist) {
                            return false;
                        }
                        sensorChangeStatus($.parseJSON(userInfoCookie), STATION_LOGOUT);
                    }
                });

                $("#isFirstStation").click(function () {
                    if (checkStation($("#tagName").val(), true) == true) {
                        $(this).attr("disabled", true);
                        $("#isNotFirstStation").removeAttr("disabled");
                        $("#firstStationWiget").show();
                        $("#otherStationWiget, #saveNotice").hide();
                        $("#step2Hint").html("")
                                .append("<li>輸入工單</li>")
                                .append("<li class='importantMsg'>確定系統有帶出機種</li>")
                                .append("<li>選擇人數</li>")
                                .append("<li>點選<code>Begin</code>開始投入</li>")
                                .append("<li>如果要更換使用者，請點選<code>換人</code>，填入您的新工號之後進行工號切換</li>");
                        $("#people").show();
                    }
                });

                $("#isNotFirstStation").click(function () {
                    var processData = searchProcessing();
                    if (processData == null || processData.length == 0 || processData[0].people == 1 
                            || checkStation($("#tagName").val(), false) == true) {
                        $(this).attr("disabled", true);
                        $("#isFirstStation").removeAttr("disabled");
                        $("#firstStationWiget").hide();
                        $("#otherStationWiget, #saveNotice").show();
                        $("#step2Hint").html("")
                                .append("<li>做完最後一台時點擊<code>Save</code>，告知系統您已經做完了</li>")
                                .append("<li>如果要更換使用者，請點選<code>換人</code>，填入您的新工號之後進行工號切換</li>");
                    }
                });

                //Step 2 event
                $("#po").on("keyup", function () {
                    getModel($(this).val(), $("#modelName"));
                });

                $("#searchProcessing").click(function () {
                    showProcessing();
                });

                //Re搜尋工單
                $("#reSearch").click(function () {
                    $("#step2").block({message: "Please wait..."});
                    $("#po").trigger("keyup");
                    setTimeout(function () {
                        $("#step2").unblock();
                    }, 2000);
                });

                //站別一操作工單投入
                $("#babBegin").click(function () {
                    var po = $("#po").val();
                    var modelName = $("#modelName").val();
                    var people = $("#people").val();

                    if (checkVal(po, modelName, people) == false || modelName == "data not found") {
                        showMsg(paramNotVaildMessage);
                        return false;
                    }

                    if (confirm("確定開始工單?\n" + "工單:" + po + "\n機種:" + modelName + "\n人數:" + people)) {
                        startBab(po, modelName, people);
                    }
                });

                var searchResult = null;

                //其他站別結束命令
                $("#babEnd").click(function () {
                    var userInfo = $.parseJSON(userInfoCookie);

                    if (searchResult == null) {
                        var data = searchProcessing();
                        searchResult = data[0];//取最先投入的工單做關閉
                    }

                    if (searchResult == null) { //當查第二次還是沒有結果
                        showMsg("站別一無投入的工單，請重新做確認");
                    } else {
                        if (confirm(
                                "確定儲存?\n" +
                                "工單: " + searchResult.po + "\n" +
                                "機種: " + searchResult.modelName + "\n" +
                                "人數: " + searchResult.people
                                )) {

                            otherStationUpdate({
                                bab_id: searchResult.id,
                                tagName: userInfo.tagName,
                                jobnumber: userInfo.jobnumber,
                                action: BAB_END
                            });
                        }
                    }
                });

                $("#changeUser").click(function () {
                    dialogMessage.dialog("open");
                });

                $("#searchBabSetting").click(function () {
                    var po = $("#po3").val();
                    if (checkVal(po) == false) {
                        alert("Po is invalid");
                        return false;
                    }
                    findBabSettingHistory(po);
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

            function getLine() {
                var result;
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabLineController/findAll" />",
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

            //extra functions
            function checkExistCookies() {
                var testLineTypeCookie = $.cookie(testLineTypeCookieName);
                var cellCookie = $.cookie(cellCookieName);
                var babLineTypeCookie = $.cookie(userInfoCookieName);

                if (testLineTypeCookie != null || cellCookie != null) {
                    lockAllUserInput();
                    var message = "您已經登入測試或Cell桌";
                    alert(message);
                    showMsg(message);
                    return false;
                }

                if (babLineTypeCookie != null) {
                    var cookieMsg = $.parseJSON(babLineTypeCookie);
                    if (cookieMsg["floor.name"] != null && cookieMsg["floor.name"] != $("#userSitefloorSelect").val()) {
                        lockAllUserInput();
                        alert("您已經登入其他樓層");
                        showMsg("您已經登入其他樓層");
                        return false;
                    }
                }
                return true;
            }

            function initUserInputWiget() {
                var userInfoCookie = $.cookie(userInfoCookieName);

                $("#searchProcessing").attr("disabled", userInfoCookie == null);

                if (userInfoCookie != null) {
                    var obj = $.parseJSON(userInfoCookie);
                    $("#jobnumber").val(obj.jobnumber);
                    $("#tagName").val(obj.tagName);
                    $("#step2").unblock();
                    firstStationMaxPeopleInit(obj.line_max_people, obj.position);
                    showProcessing();
                } else {
                    $(".stepAfterLogin").block({
                        message: "請先在步驟一完成相關步驟。",
                        css: {cursor: 'default'},
                        overlayCSS: {cursor: 'default'}});
                }
            }

            function firstStationMaxPeopleInit(lineMax, position) {
                var obj = $("#people");
                var maxPeopleInput = lineMax - position + 1;
                for (var i = 1; i <= maxPeopleInput; i++) {
                    obj.append("<option value='" + i + "'> " + i + " 人</option>");
                }
            }

            function lockAllUserInput() {
                $(":input,select").not("#redirectBtn, #directlyClose").attr("disabled", "disabled");
            }

            //步驟一儲存使用者資訊
            function saveUserStatus(tag, jobnumber) {

                if (checkVal(jobnumber, tag) == false || !jobnumber.match(tabreg)) {
                    showMsg(paramNotVaildMessage);
                    return false;
                }

                if (!checkUserExist(jobnumber)) {
                    showMsg(userNotFoundMessage);
                    return false;
                }

                var tagName = getTagName(tag);

                if (tagName.id == null) {
                    showMsg(paramNotVaildMessage);
                    return false;
                }

                if ($("#userSitefloorSelect").val() != tagName.line.floor.name) {
                    showMsg("Sensor's 不在您所屬的樓層");
                    return false;
                }

                if (!confirm("確定您所填入的資料無誤?\n" + "Sensor代號:" + tagName.id.lampSysTagName.name + "\n工號:" + jobnumber + "\n")) {
                    return false;
                }

                sensorChangeStatus({
                    jobnumber: jobnumber,
                    tagName: tagName.id.lampSysTagName.name,
                    line_id: tagName.line.id,
                    lineType_id: tagName.line.lineType.id,
                    line_max_people: tagName.line.people,
                    floor_id: tagName.line.floor.id,
                    position: tagName.position,
                    "floor.name": tagName.line.floor.name
                }, STATION_LOGIN);
            }

            function getTagName(encodeStr) {
                var result;
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabSensorLoginController/findSensorByEncode" />",
                    data: {
                        encodeStr: encodeStr
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

            function changeJobnumber(newJobnumber) {
                var data = $.parseJSON($.cookie(userInfoCookieName));
                data.jobnumber = newJobnumber;
                data.action = CHANGE_USER;
                otherStationUpdate(data);
            }

            //使用者換人時，把cookievaule做更新
            function changeJobnumberInCookie(newJobnumber) {
                var cookieInfo = $.parseJSON($.cookie(userInfoCookieName));
                $.removeCookie(userInfoCookieName);
                cookieInfo.jobnumber = newJobnumber;
                generateCookie(userInfoCookieName, JSON.stringify(cookieInfo));
            }

            //看使用者是否存在
            function checkUserExist(jobnumber) {
                var result;
                $.ajax({
                    type: "GET",
                    url: "CheckUser",
                    data: {
                        jobnumber: jobnumber
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

            function checkStation(tagName, isFirstStation) {
                var result;
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabSensorLoginController/checkStation" />",
                    data: {
                        tagName: tagName,
                        isFirstStation: isFirstStation
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

            //站別一取得機種
            function getModel(text, obj) {
                var reg = "^[0-9a-zA-Z]+$";
                if (text != "" && text.match(reg)) {
                    window.clearTimeout(hnd);
                    hnd = window.setTimeout(function () {
                        $.ajax({
                            type: "GET",
                            url: "BabController/findModelNameByPo",
                            data: {
                                po: text.trim()
                            },
                            dataType: "html",
                            success: function (response) {
                                obj.val(response);
                                $("#reSearch").show();
                                $("#ispre").prop("checked", false);
                            },
                            error: function () {
                                showMsg(serverErrorConnMessage);
                            }
                        });
                    }, 1000);
                } else {
                    obj.val("");
                }
            }

            function searchProcessing() {
                var data;
                $.ajax({
                    type: "GET",
                    url: "BabController/findProcessingByTagName",
                    data: {
                        tagName: $("#tagName").val()
                    },
                    dataType: "html",
                    async: false,
                    success: function (response) {
                        var babs = JSON.parse(response);
                        data = babs;
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
                return data;
            }

            function showProcessing() {
                showInfo("");
                var data = searchProcessing();
                if (data.length != 0) {
                    for (var i = 0; i < data.length; i++) {
                        var processingBab = data[i];
                        $("#processingBab").append(
                                "<p" + (i == 0 ? " class='alarm'" : "") + ">工單: " + processingBab.po +
                                " / 機種: " + processingBab.modelName +
                                " / 人數: " + processingBab.people +
                                (processingBab.ispre == 1 ? " / 前置" : "") +
                                "</p>");
                    }
                } else {
                    showInfo("No data");
                }
            }

            //步驟一，維持各站別的唯一性
            function sensorChangeStatus(data, action) {
                console.log(data);
                $.ajax({
                    type: "Post",
                    url: "BabSensorLoginController/" + action.toLowerCase(),
//                    url: "TestController/testInputParam",
                    data: data,
                    dataType: "html",
                    traditional: true,
                    success: function (response) {
                        //傳回來 success or fail
                        if (response == "success") {
                            if (action == STATION_LOGIN) {
                                generateCookie(userInfoCookieName, JSON.stringify(data));
                            } else {
                                removeAllStepCookie();
                            }
                            reload();
                        } else {
                            showMsg(response);
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            //
            function startBab(po, modelName, people) {
                if ($.cookie(userInfoCookieName) == null) {
                    showMsg(userNotFoundMessage);
                    return false;
                }

                var people = parseInt($("#people").val());
                var ispre = $("#ispre").is(":checked") ? 1 : 0;

                saveBabInfo({
                    po: po,
                    modelName: modelName,
                    people: people,
                    ispre: ispre
                });
            }

            //站別一對資料庫操作
            function saveBabInfo(data) {
                var totalUserInfo = $.extend($.parseJSON($.cookie(userInfoCookieName)), data);
                $.ajax({
                    type: "Post",
                    url: "BabFirstStationController/insert",
                    data: totalUserInfo,
                    dataType: "html",
                    success: function (response) {
                        $("#searchProcessing").trigger("click");
                        showMsg(response);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            //其他站別動作
            function otherStationUpdate(data) {
                $.ajax({
                    type: "Post",
                    url: "BabOtherStationController/" + data.action,
                    data: data,
                    dataType: "html",
                    success: function (response) {
                        showMsg(response);
                        if (response == "success") {
                            if (data.action == CHANGE_USER) {
                                changeJobnumberInCookie(data.jobnumber);
                            }
                            reload();
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function findBabSettingHistory(po) {
                var findWithBalance = $("#maxBalance").is(":checked");
                var findWithAlarmPercent = $("#minAlarmPercent").is(":checked");
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabSettingHistoryController/findWithMaxBalanceOrMinAlarmPercent" />",
                    data: {
                        po: po,
                        findWithCurrentLine: $("#currentLine").is(":checked"),
                        findWithBalance: findWithBalance,
                        findWithAlarmPercent: findWithAlarmPercent,
                        isAboveMinPcs: $("#aboveMinPcs").is(":checked")
                    },
                    dataType: "json",
                    success: function (response) {
                        var obj = $("#babSettingHistorySearchResult");
                        obj.html("");
                        var arr = response.data;
                        if (arr.length == 0) {
                            obj.html("<p>No data.</p>");
                        } else {
                            obj.append("<h5>Po : " + po + "</h5>");
                            if (findWithBalance) {
                                obj.append("<h5>Best lineBalance: " + arr[0].balance + "</h5>");
                            } else if (findWithAlarmPercent) {
                                obj.append("<h5>Min alarmPercent: " + arr[0].alarmPercent + "</h5>");
                            }
                            obj.append("<h5>User setting: </h5>");
                            for (var i = 0; i < arr.length; i++) {
                                var info = arr[i].babAlarmHistory;
                                obj.append("<h5>" +
                                        "(id: " + info.bab.id +
                                        " / pcs: " + arr[i].totalPcs + ")" +
                                        " / tagName: " + info.tagName.name +
                                        " / station: " + info.station +
                                        " / jobnumber: " + info.jobnumber +
                                        "</h5>");
                            }
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            //auto uppercase the textbox value(PO, ModelName)
            function textBoxToUpperCase(obj) {
                obj.val(obj.val().trim().toLocaleUpperCase());
            }

            //generate all cookies exist 12 hours
            function generateCookie(name, value) {
                var cookie_expired_time = moment().set({hour: 23, minute: 0, second: 0});
                $.cookie(name, value, {expires: cookie_expired_time.toDate()});
            }

            function removeAllStepCookie() {
                removeCookie(userInfoCookieName);
            }

            //removeCookieByName
            function removeCookie(name) {
                $.removeCookie(name);
            }

            //refresh the window
            function reload() {
                window.location.reload();
            }

            function showMsg(msg) {
                alert(msg);
                $("#serverMsg").html(msg);
                clearTimeout(serverMsgTimeout);
                serverMsgTimeout = setTimeout(function () {
                    $("#serverMsg").html("");
                }, 5000);
            }

            function showInfo(msg) {
                $("#processingBab").html(msg);
            }
        </script>
    </head>
    <body>
        <input id="userSitefloorSelect" type="hidden" value="${userSitefloor}">
        <div id="titleAlert">
            <c:out value="您所選擇的樓層是: ${userSitefloor}" />
            <a href="${pageContext.request.contextPath}">
                <button id="redirectBtn" class="btn btn-default btn-xs" >不是我的樓層?</button>
            </a>
        </div>
        <jsp:include page="temp/head.jsp" />
        <script>
            var btn = $.fn.button.noConflict(); // reverts $.fn.button to jqueryui btn
            $.fn.btn = btn; // assigns bootstrap button functionality to $.fn.btn 
        </script>

        <!--Dialogs-->
        <div id="dialog-message" title="${initParam.pageTitle}">
            <p>
                <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 50px 0;"></span>
                請再次輸入您的工號。
            </p>
            <p>
                <input type="text" id="newJobnumber">
            </p>
        </div>
        <!--Dialogs-->

        <!--Contents-->
        <div class="container">
            <div id="step1" class="step">
                <div class="userWiget form-inline">
                    <input type="text" id="tagName" placeholder="請輸入Sensor代號" autocomplete="off" maxlength="50"/>
                    <input type="text" id="jobnumber" placeholder="請輸入工號" autocomplete="off" maxlength="50"/>
                    <input type="button" id="saveInfo" value="Begin" />
                    <input type="button" id="clearInfo" value="End" />
                    <input type="button" id="changeUser" value="換人" />
                </div>
                <div class="wigetInfo">
                    <h3>步驟1:</h3>
                    <h5>請依序填入您的相關資訊。</h5>
                </div>
            </div>

            <div id="step2" class="step stepAfterLogin">
                <div class="userWiget form-inline">
                    <div id="selectStationWiget" class="row" style="display: block">
                        <div class="col col-xs-12">
                            <table class="table table-sm">
                                <tr>
                                    <td>
                                        <input type="button" class='btn btn-default' id='isFirstStation' value="第一站" /> / 
                                        <input type="button" class='btn btn-default' id='isNotFirstStation' value="非第一站" />
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div id="firstStationWiget" class="row">
                        <div class="col col-xs-12">
                            <table>
                                <tr>
                                    <td>輸入工單</td>
                                    <td>
                                        <input type="text" name="po" id="po" placeholder="請輸入工單號碼" autocomplete="off" maxlength="50">  
                                        <input type="text" name="modelName" id="modelName" placeholder="機種" readonly style="background: #CCC">
                                        <button class='btn btn-default' id='reSearch'>重新查詢</button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>選擇人數</td>
                                    <td>
                                        <!-- Auto count people by maxLinePeopleSetting - tagName position = maxinum select people-->
                                        <select id='people'>
                                            <option value="-1">---請選擇人數---</option>
                                        </select>
                                        <input type="checkbox" id="ispre" /><label for="ispre">前置</label>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" class="alarm">※相同的Sensor代號不允許存在兩套不同的作業站別</td>
                                </tr>
                                <tr>
                                    <td>投入工單</td>
                                    <td>
                                        <div class="col-md-6">
                                            <input type="button" id="babBegin" class="btn-block" value="Begin" />
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div id="otherStationWiget" class="row">
                        <div class="col col-xs-12">
                            <table>
                                <tr>
                                    <td>儲存結束工單</td>
                                    <td>
                                        <input type="button" id="babEnd" value="Save" />
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div id="saveNotice" class="alarm">
                        <span class="glyphicon glyphicon-alert"></span>
                        做完時請記得做save。(Please "save" when you finished.)
                        <span class="glyphicon glyphicon-alert"></span>
                    </div>
                </div>
                <div class="wigetInfo">
                    <h3>步驟2:</h3>
                    <h5>請依照下列流程操作。</h5>
                    <h5>
                        <ol id="step2Hint">
                        </ol>
                    </h5>
                </div>
            </div>

            <div id="step3" class="step stepAfterLogin">
                <div id='serverMsg' class="userWiget">        
                </div>
                <div class="wigetInfo">
                    <h3>步驟3:觀看伺服器訊息。</h3>
                    <h5>此處會顯示伺服器訊息。</h5>
                </div>
            </div>

            <div id="step4" class="step stepAlarm stepAfterLogin">
                <div class="form-inline userWiget">
                    <div><input type="button" id="searchProcessing" value="查詢"></div>
                    <div id="processingBab" ></div>
                </div>
                <div class="wigetInfo">
                    <h3>完成:</h3>
                    <h5>此處可點選查詢按鈕搜尋正在進行的工單。</h5>
                    <h5>紅色字體代表目前正在進行線平衡量測的工單。</h5>
                </div>
            </div>

            <div class="step stepAfterLogin">
                <div class="form-inline userWiget">
                    <div class="form-group">
                        <input id="po3" type="text" placeholder="請輸入工單號碼" autocomplete="off" maxlength="50">  
                    </div>
                    <div class="form-group form-check">
                        <input class="form-check-input" type="checkbox" id="currentLine" checked="">
                        <label class="form-check-label" for="currentLine">本線別</label>
                        <input class="form-check-input" type="checkbox" id="aboveMinPcs" checked="">
                        <label class="form-check-label" for="aboveMinPcs">大於10台</label>
                        <input class="form-check-input" type="radio" name="searchFilters" id="maxBalance" checked="">
                        <label class="form-check-label" for="maxBalance">最佳線平衡率</label>
                        <input class="form-check-input" type="radio" name="searchFilters" id="minAlarmPercent">
                        <label class="form-check-label" for="minAlarmPercent">最低亮燈頻率</label>
                    </div>
                    <div class="form-group">
                        <input type="button" id="searchBabSetting" value="查詢">
                    </div>
                    <div>
                        <div id="babSettingHistorySearchResult"></div>
                    </div>
                </div>
                <div class="wigetInfo">
                    <h3>最佳線平衡配置查詢</h3>
                    <h5>此處可查詢該工單在此線別的最佳配置情況，以供參考。</h5>
                </div>
            </div>

            <div id="hintmsg" style="color:red;font-weight: bold;padding-left: 10px">
                <p>※第一站人員請先Key入相關資料再把機子放到定位(否則會少一台紀錄)</p>
                <p>機子擋住Sensor即開始計時，休息時間的操作不列入計算範圍之內。</p>
            </div>
        </div>
        <jsp:include page="temp/footer.jsp" />
        <%--<jsp:include page="temp/_beta.jsp" />--%>
        <jsp:include page="temp/_debug.jsp" />
    </body>
</html>
