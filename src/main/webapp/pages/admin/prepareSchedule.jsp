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
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>">
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
        <script src="<c:url value="/webjars/momentjs/2.18.1/moment.js" /> "></script>
        <script src="<c:url value="/js/bootstrap-datetimepicker.min.js" />"></script>
        <script src="<c:url value="/webjars/datatables/1.10.16/js/jquery.dataTables.min.js" /> "></script>
        <script src="<c:url value="/js/dataTables.fixedHeader.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/dataTables.buttons.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.flash.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/jszip.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/pdfmake.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/vfs_fonts.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.html5.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.print.min.js" />"></script>
        <script src="<c:url value="/js/urlParamGetter.js"/>"></script>
        <script src="<c:url value="/js/param.check.js" />"></script>
        <script src="<c:url value="/js/countermeasure.js" />"></script>
        <script src="<c:url value="/js/pivot.min.js" />"></script>
        <script src="<c:url value="/js/jquery_pivot.js" />"></script>
        <script src="<c:url value="/js/jquery.spring-friendly.js" />"></script>

        <script>
            var table;
            var lockDays = 14;
            var urlLineType;
            var lineTypeIds;

            $(function () {
                urlLineType = getQueryVariable("lineType");
                lineTypeIds = (urlLineType == null || urlLineType == "ASSY" ? [1, 2] : [3]);

                if (urlLineType == "ASSY") {
                    $("#worktimeSumArea").hide();
                    $("#page-title").html("組裝最佳排站配置");
                    $("#floor").val(2);
                } else if (urlLineType == "Packing") {
                    $("#page-title").html("包裝最佳排站配置");
                    $("#floor").val(1);
                }
                initSelectOption();
                
                $("#floor").parent().hide();

                var momentFormatString = 'YYYY-MM-DD';
                $(":text,select:not(#line)").addClass("form-control");
                $(":button").addClass("btn btn-default");

                var options = {
                    defaultDate: moment(),
                    useCurrent: true,
                    //locale: "zh-tw",
                    format: momentFormatString,
                    extraFormats: [momentFormatString]
                };

                var beginTimeObj = $('#fini').datetimepicker(options);
                var endTimeObj = $('#ffin').datetimepicker(options);

                $(":text").keyup(function () {
                    $(this).val($(this).val().toUpperCase());
                });

                $("#send").click(function () {
                    getDetail();
                });

                $("body").on("change", ".scheLines, .priority, .undoneQty, .memo", function () {
                    var data = table.row($(this).parents('tr')).data();
                    var parents = $(this).parents('tr');
                    var lineId = parents.find(".scheLines").val();
                    data.line = {
                        id: lineId
                    };
                    data.priority = parents.find(".priority").val();
                    data.undoneQty = parents.find(".undoneQty").val();
                    data.memo = parents.find(".memo").val();

                    delete data.users;
                    delete data.otherInfo;
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/PrepareScheduleController/update" />",
                        data: data,
                        dataType: "html",
                        success: function (response) {
//                            alert("success");
                            table.ajax.reload();
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                });

                $("#sep_confirm").click(function () {
                    var id = $("#sep_id").val();
                    var cnt = $("#sep_cnt").val();
                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/PrepareScheduleController/splitCnt" />",
                        data: {
                            "id": id,
                            "cnt": cnt
                        },
                        dataType: "html",
                        success: function (response) {
//                            alert("success");
                            $("#sep_id").val("");
                            $("#sep_cnt").val("");
                            table.ajax.reload();
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                });

                $("body").on("click", "input[type='text'], input[type='number']", function () {
                    $(this).select();
                });

                $(".lineSelGroup").hide();

            });

            function initSelectOption() {
                console.log(lineTypeIds);
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabLineController/findByLineType" />",
                    data: {
                        lineType_id: lineTypeIds
                    },
                    dataType: "json",
                    success: function (response) {
                        var lineWiget = $("#line");
                        var arr = response;
                        for (var i = 0; i < arr.length; i++) {
                            var line = arr[i];
                            lineWiget.append("<option value=" + line.id + ">" + line.name + "</option>");
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function getDetail() {
                $("#send").attr("disabled", true);

                $(".scheLines").off();
                var i = 0;

                table = $('#tb1').DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        'copy',
                        {
                            extend: 'excelHtml5',
                            footer: true
                        }
                    ],
                    fixedHeader: {
                        headerOffset: 50
                    },
//                    rowReorder: {
//                        selector: 'tr'
//                    },
                    "ajax": {
                        "url": "<c:url value="/PrepareScheduleController/findPrepareSchedule" />",
                        "type": "Get",
                        data: {
                            lineId: $("#line").val(),
                            d: $("#fini").val(),
                            floorId: $("#floor").val(),
                            lineType_id: lineTypeIds
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "id", title: "id"},
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種", width: "7%"},
                        {data: "totalQty", title: "工單數"},
                        {data: "scheduleQty", title: "排程數"},
                        {data: "floor.id", title: "樓層", visible: false},
                        {data: "timeCost", title: "workTime"},
                        {data: "cycleTime", title: "cycleTime", visible: false},
                        {data: "line.id", title: "線別"},
                        {data: "priority", title: "順序"},
                        {data: "startDate", title: "開始時間", width: "5%"},
                        {data: "endDate", title: "結束時間", width: "5%"},
                        {data: "users", title: "站1", width: "5%"},
                        {data: "users", title: "站2", width: "5%"},
                        {data: "users", title: "站3", width: "5%"},
                        {data: "users", title: "站4", width: "5%"},
                        {data: "undoneQty", title: "未完成", visible: false},
                        {data: "hrcMemo", title: "人機協作"},
                        {data: "memo", title: "備註"}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": [5],
                            'render': function (data, type, full, meta) {
                                return data == 1 ? "5F" : "6F";
                            }
                        },
                        {
                            "type": "html",
                            "targets": [7],
                            'render': function (data, type, full, meta) {
                                var timeCost = full["timeCost"];
                                var people = 3;
                                var qty = full["scheduleQty"];

                                return roundDecimal(timeCost / people / qty, 2);

                            }
                        },
                        {
                            "type": "html",
                            "targets": [8],
                            'render': function (data, type, full, meta) {
                                var selClone = $("#line").clone(true).off();
                                selClone.removeAttr("id").addClass("scheLines");
                                selClone.find("option[value = '" + data + "']").attr("selected", "selected");
                                return selClone.prop("outerHTML");
                            }
                        },
                        {
                            "type": "html",
                            "targets": [10, 11],
                            'render': function (data, type, full, meta) {
                                return data == null ? 'n/a' : formatDate(data);
                            }
                        },
                        {
                            "type": "html",
                            "targets": [12, 13, 14, 15],
                            'render': function (data, type, full, meta) {
                                var col = meta.col % 12;
                                var arr = data;
                                if (arr == null) {
                                    return 'n/a';
                                } else if (col + 1 > arr.length) {
                                    return 'n/a';
                                } else {
                                    var username = arr[col].username;
                                    if (full.hasOwnProperty("otherInfo") && full["otherInfo"] != null) {
                                        var flag = full["otherInfo"][col];
                                        if (flag == false) {
                                            return "<b class='text-danger'>" + username + "</b>";
                                        }
                                    } else if (username == null) {
                                        return 'n/a';
                                    }
                                    return username;
                                }
                            }
                        },
                        {
                            "type": "html",
                            "targets": [9],
                            'render': function (data, type, full, meta) {
                                var disabled = full.line == null;
                                return "<div class='col-sm-10'><input type='number' class='priority' min=1 value='" + (data == null ? 0 : data) + "' " + (disabled ? "disabled" : "") + "></div>";
                            }
                        },
                        {
                            "type": "html",
                            "targets": [16],
                            'render': function (data, type, full, meta) {
                                var disabled = full.line == null;
                                return "<input type='number' class='form-control undoneQty' min=1 value='" + (data == null ? 0 : data) + "' " + (disabled ? "disabled" : "") + ">";
                            }
                        },
                        {
                            "type": "html",
                            "targets": [17],
                            'render': function (data, type, full, meta) {
                                var disabled = full.line == null;
                                return "<input type='text' class='form-control memo' value='" + (data == null ? '' : data) + "' " + (disabled ? "disabled" : "") + ">";
                            }
                        },
                        {
                            "type": "html",
                            "targets": [19],
                            'render': function (data, type, full, meta) {
                                return (data == null ? '' : data);
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
                        $("#send").attr("disabled", false);
                        var worktimeSum = 0;
                        var data = table
                                .rows()
                                .data();

                        for (var i = 0; i < data.length; i++) {
                            worktimeSum += data[i].timeCost;
                        }
                        $("#worktimeSumArea").html("<h5 class='text text-danger'>排站工時: " + worktimeSum + " 分</h5>");
                    },
                    filter: false,
                    destroy: true,
                    paginate: false
                });

            }

            function formatDate(dateString) {
                return moment(dateString).subtract(8, 'h').format('YYYY-MM-DD HH:mm');
            }

            function getPercent(val) {
                return roundDecimal((val * 100), 2) + '%';
            }

            function roundDecimal(val, precision) {
                var size = Math.pow(10, precision);
                return Math.round(val * size) / size;
            }

        </script>
    </head>
    <body>
        <c:import url="/temp/admin-header.jsp" />
        <div class="container-fluid">
            <div>
                <h3 id="page-title" class="title">組裝最佳排站配置</h3>
            </div>
            <div class="row form-inline">
                <div class="col form-group lineSelGroup">
                    <label for="line">線別: </label>
                    <select id="line">
                    </select>
                </div>
                <div class="col form-group">
                    <label for="floor">樓層: </label>
                    <select id="floor">
                        <option value="1">5F</option>
                        <option value="2">6F</option>
                    </select>
                </div>
                <div class="col form-group">
                    <label for="beginTime">日期: 從</label>
                    <div class='input-group date' id='beginTime'>
                        <input type="text" id="fini" placeholder="請選擇起始時間"> 
                    </div> 
                </div>
                <div class="col form-group">
                    <input type="button" id="send" value="確定(Ok)">
                </div>
                <div class="col form-group">
                    <div id="worktimeSumArea"></div>
                </div>
            </div>

            <div class="row">
                <table id="tb1" class="table table-striped">
                </table>
            </div>

            <hr />

            <div class="row">
                <h3 class="title">工單拆解</h3>
                <table class="table table-borderless">
                    <tr>
                        <td>
                            <div class="col form-inline">
                                <label for="">將</label>
                                <input type="text" id="sep_id" placeholder="資料欄id">
                                <label for="">拆解為</label>
                                <input type="text" id="sep_cnt" placeholder="數量">
                                <input type="button" id="sep_confirm" value="確定(Ok)">
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <h5 class="text-danger">※數量請用小寫逗號隔開, 若拆解後數量總和未達排程數系統會自動補齊</h5>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
