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

                initSelectOption();

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

                $(":text").keyup(function () {
                    $(this).val($(this).val().toUpperCase());
                });

                $("#send").click(function () {
                    getDetail();
                });

                $('body').on('click', '#tb1 tbody tr', function () {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                    } else {
                        table.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                    }
                });

                $(".lineSelGroup").hide();

            });

            function initSelectOption() {
                console.log(lineTypeIds);
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabLineController/findByUserAndLineType" />",
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

                table = $('#tb1').DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        {
                            extend: 'copyHtml5',
                            exportOptions: {
                                columns: ':visible'
                            }
                        },
                        {
                            extend: 'excelHtml5',
                            footer: true,
                            exportOptions: {
                                columns: ':visible'
                            }
                        }
                    ],
                    fixedHeader: {
                        headerOffset: 50
                    },
                    "ajax": {
                        "url": "<c:url value="/PrepareScheduleController/findPrepareSchedulePercentage" />",
                        "type": "Get",
                        data: {
                            startDate: $("#fini").val(),
                            floorId: $("#floor").val(),
                            "lineType_id[]": $("#lineType").val()
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "id", title: "id", visible: false},
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種"},
                        {data: "totalQty", title: "總工單數", visible: false},
                        {data: "scheduleQty", title: "當日排程數"},
                        {data: "otherInfo.passCntQry_PREASSY", title: "MES總筆數(Pre-Assy)", visible: false},
                        {data: "otherInfo.passCntQry_ASSY", title: "MES總筆數(Assy)"},
                        {data: "otherInfo.passCntQry_T1", title: "MES總筆數(T1)"},
                        {data: "otherInfo.passCntQry_BI", title: "MES總筆數(BI)"},
                        {data: "otherInfo.passCntQry_T2", title: "MES總筆數(T2)"},
                        {data: "otherInfo.passCntQry_T3", title: "MES總筆數(T3)"},
                        {data: "otherInfo.passCntQry_T4", title: "MES總筆數(T4)"},
                        {data: "otherInfo.passCntQry_PACKAGE", title: "MES總筆數(PACKAGE)"}
//                        {data: "otherInfo.passCntQry_PACKAGE", title: "總完成度(PACKAGE)", "sType": "numeric-comma"}
//                        {data: "floor.id", title: "樓層", visible: false},
//                        {data: "line.id", title: "線別", visible: false}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": [5, 6, 7, 8, 9, 10, 11, 12],
                            'render': function (data, type, full, meta) {
                                if (data == null) {
                                    return '---';
                                } else {
                                    return '(' + data + '/' + full["scheduleQty"] + ')';
                                }
                            }
                        }
//                        {
//                            "type": "html",
//                            "targets": [6, 8, 10, 12, 14, 16],
//                            'render': function (data, type, full, meta) {
//                                return data == null ? '---' : getPercent((data / full["totalQty"]));
//                            }
//                        }
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
                    },
                    "fnDrawCallback": function () {
//                        var api = this.api();
//                        setTimeout(function () {
//                            api.columns().flatten().each(function (colIdx) {
//                                var columnData = api.columns(colIdx).data().join('');
//                                if (columnData.length == (api.rows().count() - 1) && colIdx != 0) {
//                                    api.column(colIdx).visible(false);
//                                }
//                            });
//                        }, 0);
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

            function isNull(col, replace_value) {
                return col == null ? replace_value : col;
            }

        </script>
    </head>
    <body>
        <c:import url="/temp/admin-header.jsp" />
        <div class="container-fluid">
            <div>
                <h3 id="page-title" class="title">工單完程度列表</h3>
            </div>
            <div class="row form-inline">
                <div class="col form-group">
                    <label for="lineType">製程: </label>
                    <select id="lineType">
                        <option value="2">前置</option>
                        <option value="1">組裝</option>
                        <option value="7">T1</option>
                        <option value="8">T2</option>
                        <option value="3">包裝</option>
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
                <table id="tb1" class="table table-striped table-hover">
                </table>
            </div>

            <hr />
        </div>

        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
