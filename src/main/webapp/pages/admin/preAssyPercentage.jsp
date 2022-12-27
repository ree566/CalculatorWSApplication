<%-- 
    Document   : babDetailInfo
    Created on : 2016/6/14, 下午 03:18:11
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication var="user" property="principal" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
        <link rel="stylesheet" href="<c:url value="/webjars/jquery-ui-themes/1.12.1/redmond/jquery-ui.min.css" />" >
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>">
        <link rel="stylesheet" href="<c:url value="/webjars/datatables/1.10.16/css/jquery.dataTables.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/fixedHeader.dataTables.min.css"/>">
        <link rel="stylesheet" href="<c:url value="/css/buttons.dataTables.min.css"/>">
        <style>
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/js/jquery-ui-1.10.0.custom.min.js" />"></script>
        <script src="<c:url value="/webjars/momentjs/2.18.1/moment.js" /> "></script>
        <script src="<c:url value="/js/bootstrap-datetimepicker.min.js" />"></script>
        <script src="<c:url value="/webjars/datatables/1.10.16/js/jquery.dataTables.min.js" /> "></script>
        <script src="<c:url value="/js/dataTables.fixedHeader.min.js" />"></script>
        <script src="<c:url value="/js/ajax-option-select-loader/babLineType.loader.js" />"></script>

        <script>
            var table;
            $(function () {

                lineTypeLoaderUrl = "<c:url value="/BabLineController/findLineType" />";
                setLineObject();
                initOptions($("#lineType"));

                var momentFormatString = 'YYYY-MM-DD';
                $(":text,input[type='number'],select").addClass("form-control");
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

                $("#send").click(function () {
                    getDetail();
                });

                var lockDays = 180;
                beginTimeObj.on("dp.change", function (e) {
                    endTimeObj.data("DateTimePicker").minDate(e.date);
                    var beginDate = e.date;
                    var endDate = endTimeObj.data("DateTimePicker").date();
                    var dateDiff = endDate.diff(beginDate, 'days');
                    if (dateDiff > 30) {
                        endTimeObj.data("DateTimePicker").date(beginDate.add(lockDays, 'days'));
                    }
                });

                endTimeObj.on("dp.change", function (e) {
                    var beginDate = beginTimeObj.data("DateTimePicker").date();
                    var endDate = e.date;
                    var dateDiff = endDate.diff(beginDate, 'days');
                    if (dateDiff > 30) {
                        beginTimeObj.data("DateTimePicker").date(endDate.add(-lockDays, 'days'));
                    }
                });

                $(":text").keyup(function () {
                    $(this).val($(this).val().toUpperCase());
                });

                $(document).on('click', '.searchBtn', function (event) { //any element with the class EditButton will be handled here
                    var data = table.row($(this).parents('tr')).data();

                    getLastProcessingRecords({
                        preAssyModuleTypeId: data.preAssyModuleTypeId,
                        po: data.po
                    });
                });

            });

            function getDetail() {
                $("#send").attr("disabled", true);
                table = $("#tb1").DataTable({
                    "processing": true,
                    "serverSide": false,
                    fixedHeader: {
                        headerOffset: 50
                    },
                    "ajax": {
                        "url": "<c:url value="/SqlViewController/findPreAssyPercentage" />",
                        "type": "Get",
                        data: {
                            startDate: $('#fini').val(),
                            lineTypeId: $('#lineType').val()
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種"},
                        {data: "preAssyModuleTypeName", title: "模組名稱"},
                        {data: "totalQty", title: "總數量"},
                        {data: "scheduleQty", title: "已排數量"},
                        {data: "pcs", title: "完成數"},
                        {data: "processing_percentage", title: "完成百分比"},
                        {
                            "data": "preAssyModuleTypeId",
                            title: "近期上線紀錄",
                            "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
//                                return '<button class="btn btn-sm btn-primary searchBtn">Search</button>';
                                return "<input type='button' class='searchBtn btn btn-primary btn-sm' data-toggle= 'modal' data-target='#myModal' value='檢視' />";
                            }
                        }
                    ],
                    "columnDefs": [

                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    bAutoWidth: true,
                    displayLength: -1,
                    lengthChange: true,
                    "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                    filter: true,
                    info: true,
                    paginate: true,
                    pageLength: 10,
                    select: true,
                    destroy: true,
                    "initComplete": function (settings, json) {
                        $("#send").attr("disabled", false);
                        $("#tb1").show();
                    },
                    "order": [[1, "asc"]]
                });
            }

            function getLastProcessingRecords(formData) {
                $(".searchBtn").attr("disabled", true);
                $("#tb2").DataTable({
                    "processing": true,
                    "serverSide": false,
                    searching: false,
                    paging: false,
                    "ajax": {
                        "url": "<c:url value="/BabController/findByPreAssyModuleType" />",
                        "type": "Get",
                        data: formData,
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種"},
                        {data: "line.name", title: "線別"},
                        {data: "people", title: "人數"},
                        {data: "babSettingHistorys[0].babPreAssyPcsRecords[0].pcs", title: "台數"},
                        {data: "beginTime", title: "開始時間"},
                        {data: "lastUpdateTime", title: "結束時間"}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": 5,
                            'render': function (data, type, full, meta) {
                                return data == null ? "PROCESSING" : data;
                            }
                        }
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    bAutoWidth: true,
                    lengthChange: false,
                    displayLength: -1,
                    destroy: true,
                    "initComplete": function (settings, json) {
                        $(".searchBtn").attr("disabled", false);
                        $("#tb1").show();
                    },
                    "order": [[1, "asc"]]
                });
            }

            function formatDate(dateString) {
                return moment(dateString).format('YYYY-MM-DD HH:mm');
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

        <div class="container">
            <div>
                <h3 class="title">前置完程度查詢</h3>
                <h5 class="subTitle">僅會顯示當日排程工單之當日總完程度</h5>
            </div>
            <div class="row form-inline">

                <div class="col form-group">
                    <label for="beginTime">日期: 從</label>
                    <div class='input-group date' id='beginTime'>
                        <input type="text" id="fini" placeholder="請選擇起始時間"> 
                    </div> 
                </div>
                <div class="col form-group">
                    <label for="endTime"> 到 </label>
                    <div class='input-group date' id='endTime'>
                        <input type="text" id="ffin" placeholder="請選擇結束時間"> 
                    </div>
                </div>
                <div class="col form-group">
                    <select id="lineType">
                    </select>
                </div>
                <div class="col form-group">
                    <div class="col form-group">
                        <input type="button" id="send" value="確定(Ok)">
                    </div>
                </div>

            </div>

            <div class="row">
                <table id="tb1" class="table table-striped" cellspacing="0" width="100%" style="text-align: center" hidden>
                </table>
            </div>

        </div>
        <div>
            <!-- Modal -->
            <div id="myModal" class="modal fade" role="dialog">
                <div class="modal-dialog" style="width:1250px;">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 id="titleMessage" class="modal-title">近五次生產紀錄</h4>
                        </div>
                        <div class="modal-body">
                            <div>
                                <table id="tb2" class="table table-bordered" cellspacing="0" width="100%" style="text-align: center">
                                </table>
                                <div id="dialog-msg" class="alarm"></div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
