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
            .remark-field{
                text-align: center;
                color: red;
                outline: #4CAF50 solid 2px;
            }
            .show-read-more .more-text{
                display: none;
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
        <%--<script src="<c:url value="https://cdn.datatables.net/buttons/1.6.5/js/buttons.colVis.min.js" />"></script>--%>

        <script>
            var table;
            var lockDays = 14;
            var urlLineType;
            var lineTypeIds;

            var dynamicColIdxMin = 8;
            var dynamicColIdxMax = 23;

            var stationsDailySumIndex = [];
            var stationsTotalSumIndex = [];

            var memoIndex = dynamicColIdxMax + 1;
            var editBtnIndex = memoIndex + 2;

            $(function () {
                for (var i = 0; i < editBtnIndex - 1; i++) {
                    $("#totalFooter").after("<th></th>");
                }

                for (var i = dynamicColIdxMin, j = 0; i <= dynamicColIdxMax; i++, j++) {
                    if (j % 2 == 0) {
                        stationsDailySumIndex.push(i);
                    } else {
                        stationsTotalSumIndex.push(i);
                    }
                }

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

                dataTableInit();
                $("#send").on("click", getDetail);
                $('body').on('click', '.save', function () {

                    if (!confirm("Save?")) {
                        return;
                    }
                    var data = table.row($(this).closest('tr')).data();
                    var p_id = data["id"];
                    var memo = $(this).parents("tr").find(".memo").val();

                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/PrepareScheduleController/updateMemo" />",
                        data: {
                            prepareSchedule_id: p_id,
                            memo: memo
                        },
                        dataType: "html",
                        success: function (response) {
                            $("#send").trigger("click");
                            alert(response);
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                });

                $(".lineSelGroup").hide();
            });

            function initSelectOption() {
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

                $.ajax({
                    url: "<c:url value="/PrepareScheduleController/findPrepareSchedulePercentage" />",
                    type: "Get",
                    data: {
                        startDate: $("#fini").val(),
                        floorId: $("#floor").val(),
                        "lineType_id[]": $("#lineType").val()
                    }
                }).done(function (result) {
                    table.clear().draw();
                    table.rows.add(result.data).draw();
                }).fail(function (jqXHR, textStatus, errorThrown) {
                    // needs to implement if it fails
                    alert(jqXHR.responseText);
                });

                findRemark();
            }

            function dataTableInit() {

                table = $('#tb1').DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        {
                            extend: 'copyHtml5',
                            exportOptions: {
                                columns: ':not(:eq(23)):visible'
                            }
                        },
                        {
                            extend: 'excelHtml5',
                            exportOptions: {
                                columns: ':not(:eq(23)):visible'
                            }
                        }
                    ],
                    fixedHeader: {
                        headerOffset: 50,
                        footer: true
                    },
                    data: [],
                    "columns": [
                        {data: "id", title: "id", visible: false},
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種"},
                        {data: "totalQty", title: "總工單數"},
                        {data: "scheduleQty", title: "當日排程數"},
                        {data: "timeCost", title: "工時"},
                        {data: "otherInfo.changeTimeInfo", title: "換線"},
                        {data: "otherInfo.changeTimeInfo", title: "MES線別"},
                        {data: "otherInfo.passCntQry_PREASSY", title: "過站(Pre-Assy)"},
                        {data: "otherInfo.totalPassCntQry_PREASSY", title: "總過站(Pre-Assy)"},
                        {data: "otherInfo.passCntQry_ASSY", title: "過站(Assy)"},
                        {data: "otherInfo.totalPassCntQry_ASSY", title: "總過站(Assy)"},
                        {data: "otherInfo.passCntQry_T1", title: "過站(T1)"},
                        {data: "otherInfo.totalPassCntQry_T1", title: "總過站(T1)"},
                        {data: "otherInfo.passCntQry_BI", title: "過站(BI)"},
                        {data: "otherInfo.totalPassCntQry_BI", title: "總過站(BI)"},
                        {data: "otherInfo.passCntQry_T2", title: "過站(T2)"},
                        {data: "otherInfo.totalPassCntQry_T2", title: "總過站(T2)"},
                        {data: "otherInfo.passCntQry_T3", title: "過站(T3)"},
                        {data: "otherInfo.totalPassCntQry_T3", title: "總過站(T3)"},
                        {data: "otherInfo.passCntQry_T4", title: "過站(T4)"},
                        {data: "otherInfo.totalPassCntQry_T4", title: "總過站(T4)"},
                        {data: "otherInfo.passCntQry_PACKAGE", title: "過站(PACKAGE)"},
                        {data: "otherInfo.totalPassCntQry_PACKAGE", title: "總過站(PACKAGE)"},
                        {data: "memo", title: "人員備註", "width": "15%"},
                        {data: "poMemo", title: "工單備註", "width": "15%"},
                        {data: "id", title: "#"}
                    ],
                    "columnDefs": [
                        {
                            className: "show-read-more",
                            "show-read-more": [25]
                        },
                        {
                            "type": "html",
                            "targets": [6],
                            'render': function (data, type, full, meta) {
                                return data == null ? '---' : roundDecimal(data.change_TIME * data.power_CNT, 1);
                            }
                        },
                        {
                            "type": "html",
                            "targets": [7],
                            'render': function (data, type, full, meta) {
                                return data == null ? '---' : data.line_DESC;
                            }
                        },
                        {
                            "type": "html",
                            "targets": stationsDailySumIndex,
                            'render': function (data, type, full, meta) {
                                if (data == null) {
                                    return '---';
                                } else {
                                    return "<p>(" + data + " / " + full["scheduleQty"] + ")</p>" +
                                            "<p>" + getPercent(data == 0 ? 0 : data / full["scheduleQty"]) + "</p>";
                                }
                            }
                        },
                        {
                            "type": "html",
                            "targets": stationsTotalSumIndex,
                            'render': function (data, type, full, meta) {
                                if (data == null) {
                                    return '---';
                                } else {
                                    return "<p>(" + data + " / " + full["totalQty"] + ")</p>" +
                                            "<p>" + (getPercent(data == 0 ? 0 : data / full["totalQty"])) + "</p>";
                                }
                            }
                        },
                        {
                            "type": "html",
                            "targets": [memoIndex],
                            'render': function (data, type, full, meta) {
                                return "<textarea class='memo form-control'>" + (data == null ? "" : data) +
                                        "</textarea>";
                            }
                        },
                        {
                            "type": "html",
                            "targets": [editBtnIndex],
                            'render': function (data, type, full, meta) {
                                return "<button type='button' class='btn btn-secondary btn-sm save'>Save</button></div>";
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
                        $.fn.dataTable.ext.errMode = 'none';
                        $('#tb1')
                                .on('error.dt', function (e, settings, techNote, message) {
                                    console.log('An error has been reported by DataTables: ', message);
                                })
                                .DataTable();
                    },
                    "fnDrawCallback": function () {
                        var api = this.api();
                        setTimeout(function () {
                            api.columns().flatten().each(function (colIdx) {
                                var columnData = api.columns(colIdx).data().join('');
                                if (colIdx >= dynamicColIdxMin && colIdx <= dynamicColIdxMax) {
                                    if (columnData.length == (api.rows().count() - 1) && colIdx != 0) {
                                        api.column(colIdx).visible(false);
                                    } else {
                                        api.column(colIdx).visible(true);
                                    }
                                }
                            });
                        }, 500);

                        setTimeout(function () {
                            $("#send").attr("disabled", false);
                        }, 1500);

                        addLongTextReadmoreEvent();
                    },
                    "footerCallback": function (row, data, start, end, display) {
                        var api = this.api(), data;

                        // Remove the formatting to get integer data for summation
                        var intVal = function (i) {
                            return typeof i === 'string' ?
                                    i.replace(/[\$,]/g, '') * 1 :
                                    typeof i === 'number' ?
                                    i : 0;
                        };

                        var pageTotal;

                        for (var i = 3; i <= 5; i++) {

                            var sum = api
                                    .column(i, {page: 'current'})
                                    .data()
                                    .reduce(function (a, b) {
                                        return intVal(a) + intVal(b);
                                    }, 0);

                            if (i == 4) {
                                pageTotal = sum;
                            }

                            $(api.column(i).footer()).html(
                                    '<p>' + sum + '</p>'
                                    );

                        }

//                        var scheduleTotalWorktime = 0;
//                        var poTotalWorktime = 0;
//                        var totalPcs = api.column(3).data().toArray();
//                        var schedulePcs = api.column(4).data().toArray();
//                        var worktime = api.column(5).data().toArray();
//
//                        for (i = 0; i < schedulePcs.length; i++) {
//                            scheduleTotalWorktime += worktime[i];
//                            poTotalWorktime += (worktime[i] * 1.0 / schedulePcs[i]) * totalPcs[i];
//                        }
//
//                        for (var i = dynamicColIdxMin, j = 0; i <= dynamicColIdxMax; i++, j++) {
//                            var totalWorktime = ((j % 2 == 0) ? scheduleTotalWorktime : poTotalWorktime);
//                            var passStationPcs = api.column(i).data().toArray();
//
//                            if (passStationPcs != null) {
//                                var passStationWorktime = 0;
//                                for (var k = 0; k < passStationPcs.length; k++) {
//                                    passStationWorktime += (worktime[k] * 1.0 / schedulePcs[k]) * passStationPcs[k];
//                                }
//
////                                // Update footer
//                                $(api.column(i).footer()).html(
//                                        '<p>' + roundDecimal(passStationWorktime, 1) + ' / ' + roundDecimal(totalWorktime, 1) + '</p>' +
//                                        '<p>' + getPercent((totalWorktime == 0 || passStationWorktime == 0) ? 0 : (passStationWorktime / totalWorktime)) + '</p>'
//                                        );
//                            }
//                        }

                        for (var i = dynamicColIdxMin, j = 0; i <= dynamicColIdxMax; i++, j++) {

                            if (j % 2 == 0) {
                                pageCurrent = api
                                        .column(i, {page: 'current'})
                                        .data()
                                        .reduce(function (a, b) {
                                            return intVal(a) + intVal(b);
                                        }, 0);

                                // Update footer
                                $(api.column(i).footer()).html(
                                        '<p>( ' + pageCurrent + ' / ' + pageTotal + ' )</p>' +
                                        '<p>' + getPercent((pageCurrent == 0 || pageTotal == 0) ? 0 : (pageCurrent / pageTotal)) + '</p>'
                                        );
                            }
                        }
                    },
                    filter: false,
                    destroy: false,
                    paginate: false,
                    retrieve: true
                });

                $('#tb1').on("preXhr.dt", function (e, settings, data) {
                    $(this).DataTable().clear();
                    settings.iDraw = 0;
                    $(this).DataTable().draw();
                });

            }

            function findRemark() {
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/PrepareScheduleController/findPrepareScheduleRemark" />",
                    data: {
                        startDate: $("#fini").val(),
                        "lineType_id[]": $("#lineType").val()
                    },
                    dataType: "json",
                    success: function (response) {
                        var d = response.data;
                        $("#pmcRemark").html(d.length == 0 ? "NO REMARK" : d[0].pmcRemark);
                        console.log(response);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function addLongTextReadmoreEvent() {
                var maxLength = 300;
                $(".show-read-more").each(function () {
                    var myStr = $(this).text();
                    if ($.trim(myStr).length > maxLength) {
                        var newStr = myStr.substring(0, maxLength);
                        var removedStr = myStr.substring(maxLength, $.trim(myStr).length);
                        $(this).empty().html(newStr);
                        $(this).append(' <a href="javascript:void(0);" class="read-more">read more...</a>');
                        $(this).append('<span class="more-text">' + removedStr + '</span>');
                    }
                });
                $(".read-more").click(function () {
                    $(this).siblings(".more-text").contents().unwrap();
                    $(this).remove();
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
                        <option value="9">前置</option>
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

            <div class="row remark-field">
                <h5 id="pmcRemark"></h5>
            </div>

            <div class="row">
                <table id="tb1" class="table table-striped table-hover">
                    <tfoot>
                        <tr>
                            <th></th>
                            <th id="totalFooter" style="text-align:right">Total:</th>
                        </tr>
                    </tfoot>
                </table>

            </div>

            <hr />
        </div>

        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
