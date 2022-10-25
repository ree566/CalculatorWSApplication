<%-- 
    Document   : index
    Created on : 2015/9/23, 上午 10:40:43
    Author     : Wei.Cheng

    http://encosia.com/3-reasons-why-you-should-let-google-host-jquery-for-you/?ref=sidebar_most_popular
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />"/>
        <!--不用header 因為這裡寫入的style會被複寫-->
        <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.7/css/bootstrap.min.css" />">
        <style>
            body{
                font-family: 微軟正黑體;
            }
            a:link,a:visited{
                color: blue;
            }
            a:hover{
                opacity: 0.5;
            }
            #titleLogo{
                width: 20%;
                height: 20%;
            }
            .noscript input{
                display: block;
            }
            .bg {

                background: url("images/mfg_lgo_advantech.gif") no-repeat center center;
                background-size: 100%;

                /*background-color: red;*/
                position: fixed;
                width: 100%;
                height: 350px; /*same height as jumbotron */
                top:0;
                left:0;
                z-index: -1;

                -webkit-filter: blur(5px);
                -moz-filter: blur(5px);
                -o-filter: blur(5px);
                -ms-filter: blur(5px);
                filter: blur(5px);

            }
            .jumbotron {
                height: 350px;
                color: white;
                text-shadow: #444 0 1px 1px;
                background-color: transparent !important;

                border-style:solid;
                border: 1px solid blue;
            }
            .outline {
                color: white;
                text-shadow:
                    3px 3px 0 #000,
                    -1px -1px 0 #000,
                    1px -1px 0 #000,
                    -1px 1px 0 #000,
                    1px 1px 0 #000;
            }
            /* prepare the selectors to add a stroke to */
            .stroke-single,
            .stroke-double {
                position: relative;
                background: transparent;
                z-index: 0;
            }
            /* add a single stroke */
            .stroke-single:before,
            .stroke-double:before {
                content: attr(title);
                position: absolute;
                -webkit-text-stroke: 0.2em #bada55; /* Chris Coyier's favorite color! */
                left: 0;
                z-index: -1;
            }
            /* add a double stroke */
            .stroke-double:after {
                content: attr(title);
                position: absolute;
                -webkit-text-stroke: 0.4em #778B37;
                left: 0;
                z-index: -2;
            }
            .selectGroup{
                padding-bottom: 20px;
            }
            .hidden{
                display: none;
            }
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/webjars/bootstrap/3.3.7/js/bootstrap.min.js" />"></script>

        <script src="<c:url value="/webjars/jquery-blockui/2.70/jquery.blockUI.js" /> "></script>
        <script src="<c:url value="/js/jquery.blockUI.Default.js" /> "></script>
        <script src="<c:url value="/js/jquery.cookie.js" /> "></script>
        <script src="<c:url value="/js/cookie.check.js" /> "></script>
        <script>

            $(document).ready(function () {

                $("input").addClass("form-control");

                var $selectGroup = $(".selectGroup").detach();

                $.getJSON("json/sitefloor.json", function (data) {
                    var sitefloors = data.sitefloors;
                    for (var i = 0, j = sitefloors.length; i < j; i++) {
                        var sitefloor = sitefloors[i].floor;
                        var cloneObj = $selectGroup.clone();

                        cloneObj.find("label>font").html(sitefloor + "F ");
                        cloneObj.find(".bab").attr("href", "Bab?sitefloor=" + sitefloor);
                        cloneObj.find(".test").attr("href", "Test?sitefloor=" + sitefloor);
                        cloneObj.find(".cell").attr("href", "Cell?sitefloor=" + sitefloor);

                        $("#selectGroupArea").append(cloneObj);
                    }
                });

                if (!are_cookies_enabled()) {
                    alert(cookie_disabled_message);
                    $(":input,select").attr("disabled", true);
                }

                //Quartz standby and resume.(Please remove when sensor testing is finish)
                $("#title").dblclick(function () {
                    $("#trigobj").slideToggle();
                });

                $("#trigobj :button").on("click", function () {
                    var order = $(this).val();
                    $.ajax({
                        type: "Post",
                        url: "QuartzTriggerControl",
                        data: {
                            order: order
                        },
                        dataType: "html",
                        success: function (response) {
                            $("#servermsg").html(response);
                        },
                        error: function () {
                            $("#servermsg").html("error");
                        }
                    });
                });

                $("#locale").change(function () {
                    window.location.href = window.location.href + "?locale=" + $(this).val();
                });

                var jumboHeight = $('.jumbotron').outerHeight();
                function parallax() {
                    var scrolled = $(window).scrollTop();
                    $('.bg').css('height', (jumboHeight - scrolled) + 'px');
                }

                $(window).scroll(function (e) {
                    parallax();
                });
            });
        </script>
    </head>
    <body class="noscript">
        <script>
            $("body").removeClass("noscript");
        </script>
        <%--<jsp:include page="head.jsp" />--%>
        <div class="bg"></div>
        <div class="jumbotron outline stroke-double">
            <h1 id="title">${initParam.pageTitle}</h1>
            <p class="lead">+ Dynamic management the production bottleneck.</p>
        </div>

        <div class="container">
            <div id="selectGroupArea"></div>
            <div class="selectGroup">
                <label><font></font><fmt:message key="index.label.userhint" /></label>
                <div class="sitefloorWiget form-inline row linkWiget">
                    <a class="bab">
                        <button class="btn btn-default col-xs-2"><fmt:message key="index.btn.assy.pkg" /></button>
                    </a>

                    <a class="test">
                        <button class="btn btn-default col-xs-2"><fmt:message key="index.btn.test" /></button>
                    </a>

                    <!--                    <a class="cell">
                                            <button class="btn btn-default col-xs-2">Cell</button>
                                        </a>-->
                </div>
            </div>
            <div>
                <h3>
                    <fmt:message key="label.changeLang" />
                </h3>
                <h5>
                    <a href="<c:url value="/?locale=en_US" />">
                        <fmt:message key="label.lang.en" />                        
                    </a>
                </h5>
                <h5>
                    <a href="<c:url value="/?locale=zh_TW" />">
                        <fmt:message key="label.lang.tw" />
                    </a>
                </h5>
            </div>
            <div class="row">
                <div id="trigobj" hidden="hidden">
                    trigger order:
                    <input type="button" value="pause">
                    <input type="button" value="resume">
                    <div id="servermsg"></div>
                </div>
                <hr/>
                <h3>Tomcat Version : <%= application.getServerInfo()%></h3>
                <h5>Servlet Specification Version : <%= application.getMajorVersion()%>.<%= application.getMinorVersion()%> </h5>
                <h5>JSP version :<%=JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion()%></h5>
            </div>
            <jsp:include page="temp/footer.jsp" />
    </body>
</html>
