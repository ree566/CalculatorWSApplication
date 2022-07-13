<%-- 
    Document   : babDetailInfo
    Created on : 2016/6/14, 下午 03:18:11
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isAuthenticated" />
<sec:authorize access="hasRole('BACKDOOR_4876_')"  var="isBackDoor4876" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
        <link rel="stylesheet" href="<c:url value="/webjars/datatables/1.10.16/css/jquery.dataTables.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/fixedHeader.dataTables.min.css"/>">
        <link rel="stylesheet" href="<c:url value="/css/buttons.dataTables.min.css"/>">

        <style>
            body{
                font-size: 16px;
                padding-top: 70px;
            }
            .wiget-ctrl{
                width: 98%;
                margin: 5px auto;
            }
            table th{
                text-align: center;
            }
            .alarm{
                color:red;
            }
            .title, .subTitle{
                display: inline !important;
            }
            .subTitle{
                color: red;
            }
            table td{
                text-align: center;
            }
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/webjars/datatables/1.10.16/js/jquery.dataTables.min.js" /> "></script>
        <script src="<c:url value="/js/dataTables.fixedHeader.min.js" />"></script>
        <script src="<c:url value="/js/urlParamGetter.js"/>"></script>
        <script src="<c:url value="/js/param.check.js" />"></script>
        <script src="<c:url value="/js/pivot.min.js" />"></script>
        <script src="<c:url value="/js/jquery.spring-friendly.js" />"></script>

        <script>
            var table;

            $(function () {

                $(":text").keyup(function () {
                    $(this).val($(this).val().toUpperCase());
                });

                initAvailableTagNames();

                getDetail();
                $("#saveBabSensorLoginRecord").click(saveBabSensorLoginRecord);
                $("body").on("click", ".deleteBabSensorLoginRecord", deleteBabSensorLoginRecord);
            });

            function getDetail() {
                table = $('#tb1').DataTable({
                    "ajax": {
                        "url": "<c:url value="/BabSensorLoginController/findAll" />",
                        "type": "Get",
                        data: {
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "id", title: "id"},
                        {data: "tagName.name", title: "感應器代號"},
                        {data: "jobnumber", title: "使用者工號"},
                        {data: "beginTime", title: "登入時間"},
                        {data: "id", title: "#"}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": [4],
                            'render': function (data, type, full, meta) {
                                return "<button class='btn btn-danger deleteBabSensorLoginRecord' data-value=" + data + ">刪除</button>";
                            }
                        }
                    ],
                    "order": [],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    displayLength: -1,
                    "processing": true,
                    "initComplete": function (settings, json) {
                    },
                    filter: false,
                    destroy: true,
                    paginate: false
                });

            }

            function initAvailableTagNames() {
                const sel = $("#tagName");
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabSensorLoginController/findAvailableTagNameComparison" />",
                    data: {
                    },
                    success: function (response) {
                        var data = response;
                        data.map(d => {
                            const v = d.id.lampSysTagName.name;
                            sel.append("<option value='" + v + "'>" + v + "</option>")
                        });
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(xhr.responseText);
                    }
                });
            }

            function saveBabSensorLoginRecord() {
                const tagName = $("#tagName").val();
                const jobnumber = $("#jobnumber").val();
                if (tagName === "" || jobnumber === "") {
                    alert("TagName or Jobnumber can't be empty");
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: "<c:url value="/BabSensorLoginController/login" />",
                    data: {
                        "tagName": tagName,
                        "jobnumber": jobnumber
                    },
                    success: function (response) {
                        if (response == "success") {
                            table.ajax.reload();
                        }
                        alert(response);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(xhr.responseText);
                    }
                });

            }

            function deleteBabSensorLoginRecord() {
                const id = $(this).attr("data-value");
                var selectData = table.row($(this).closest('tr')).data();
                const {tagName, jobnumber} = selectData;
                if (!confirm("Delete data " + tagName.name + " " + jobnumber + " ?")) {
                    return;
                }
                $.ajax({
                    type: "POST",
                    url: "<c:url value="/BabSensorLoginController/logout" />",
                    data: {
                        "tagName": tagName.name,
                        "jobnumber": jobnumber
                    },
                    success: function (response) {
                        if (response == "success") {
                            table.ajax.reload();
                        }
                        alert(response);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert(xhr.responseText);
                    }
                });
            }

            function formatDate(dateString) {
                return moment(dateString).subtract(8, 'h').format('YYYY-MM-DD HH:mm');
            }

        </script>
    </head>
    <body>
        <c:import url="/temp/admin-header.jsp" />
        <div class="container-fluid">
            <div>
                <h3 id="page-title" class="title">Sensor登入登出設定</h3>
            </div>

            <div class="form-inline">
                <input type="textbox" id="jobnumber" class="form-control" />
                <select class="form-control" id="tagName">
                </select>
                <input type="button" value="save" class="form-control" id="saveBabSensorLoginRecord" />
            </div>

            <div class="row">
                <table id="tb1" class="table table-striped">
                </table>
            </div>

            <hr />

        </div>

        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
