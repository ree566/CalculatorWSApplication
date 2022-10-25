<%-- 
    Document   : chart
    Created on : 2016/2/16, 下午 02:26:52
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication var="user" property="principal" />
<sec:authorize access="isAuthenticated()"  var="isAuthenticated" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
        <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.7/css/bootstrap.min.css" />">
        <link rel="stylesheet" href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css" />" >
        <link rel="stylesheet" href="<c:url value="/webjars/datatables/1.10.16/css/jquery.dataTables.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>">
        <link rel="stylesheet" href="<c:url value="/webjars/jquery-ui-themes/1.12.1/redmond/jquery-ui.min.css" />" >
        <link rel="stylesheet" href="<c:url value="/css/buttons.dataTables.min.css"/>" >
        <style>
            body{
                font-family: 微軟正黑體;
            }
            table{
                width:100%;
            }
            .alarm{
                color:red;
            }
            .wiget-ctrl{
                width: 98%;
                margin: 5px auto;

            }
            .expDetail{
                width: 48%;
                float:right;
            }
            .ctrlDetail{
                width: 48%;
                float:left;
            }
            .chartContainer{
                height: 360px; /*限制高度不然會把footer給覆蓋掉*/
                width: 100%;
            }
            .exp{
                background-color: #dca7a7;
            }
            .ctrl{
                background-color: #bce8f1;
            }
            .search-container{
                background-color: wheat;
            }
            .detail{
                /*height: 400px;*/
            }
            .balance{
                text-align: center;
                border: 1px black solid;
            }
            textArea{
                width: 100%;
                height: 300px;
                resize: none;
            }
            .modal-content table .lab{
                width: 120px;
            }
            .expose {
                position:relative;
            }
            #overlay {
                background:rgba(0,0,0,0.3);
                display:none;
                width:100%;
                height:100%;
                position:fixed;
                top:0;
                left:0;
                z-index:99998;
            }
            u {
                border-bottom: 1px dotted #000;
                text-decoration: none;
            }
            .detailTables{

            }
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/webjars/bootstrap/3.3.7/js/bootstrap.min.js" />"></script>
        <script src="<c:url value="/js/charts.loader.js"/>"></script>
        <script src="<c:url value="/js/jquery-ui-1.10.0.custom.min.js"/>"></script>
        <script src="<c:url value="/js/canvasjs.min.js"/>"></script>
        <script src="<c:url value="/webjars/datatables/1.10.16/js/jquery.dataTables.min.js" /> "></script>
        <script src="<c:url value="/js/dataTables.fnMultiFilter.js" /> "></script>
        <script src="<c:url value="/webjars/jquery-blockui/2.70/jquery.blockUI.js" /> "></script>
        <script src="<c:url value="/webjars/momentjs/2.18.1/moment.js" /> "></script>
        <script src="<c:url value="/js/bootstrap-datetimepicker.min.js" /> "></script>
        <script src="<c:url value="/js/jquery.cookie.js" /> "></script>
        <script src="<c:url value="/js/alasql.min.js" /> "></script> 
        <script src="<c:url value="/js/jquery-datatable-button/dataTables.buttons.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.flash.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/jszip.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/pdfmake.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/vfs_fonts.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.html5.min.js" />"></script>
        <script src="<c:url value="/js/jquery-datatable-button/buttons.print.min.js" />"></script>
        <script src="<c:url value="/js/param.check.js" />"></script>
        <script src="<c:url value="/js/urlParamGetter.js" />"></script>
        <script src="<c:url value="/js/jquery.fileDownload.js" />"></script>
        <script src="<c:url value="/js/ajax-option-select-loader/babLineType.loader.js" />"></script>
        <script src="<c:url value="/js/ajax-option-select-loader/floor.loader.js" />"></script>
        <script src="<c:url value="/js/countermeasure.js" />"></script>
        <script>
            var round_digit = 2;
            var historyTable;

            var autoReloadInterval;

            lineTypeLoaderUrl = "<c:url value="/BabLineController/findLineType" />";
            floorLoaderUrl = "<c:url value="/FloorController/findAll" />";

            var beginTimeObj, endTimeObj;

            var countermeasureType = "Bab_Abnormal_LineBalance";

            $("#sitefloor").hide();

            function initSelectOption() {
                initOptions($("#lineType, #lineType2"));
                initFloorOptions($("#sitefloor"));
            }

            function getPcsDetail(tableId, id, isused) {
                var data = {};
                if (id != null) {
                    data = {
                        id: id,
                        babStatus: isused
                    };
                }
                $(tableId).DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        'copy',
                        {
                            extend: 'excelHtml5',
                            title: 'Pcs detail'
                        }, 
                        'print'
                    ],
                    "ajax": {
                        "url": "<c:url value="/BabChartController/findPcsDetail" />",
                        "type": "GET",
                        "data": data
                    },
                    "columns": [
                        {data: "bab.id", title: "bab_id", visible: false},
                        {data: "tagName", title: "感應器"},
                        {data: "station", title: "站別"},
                        {data: "groupid", title: "組別"},
                        {data: "diff", title: "花費時間"},
                        {data: "lastUpdateTime", title: "最後更新"}
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    bAutoWidth: true,
                    destroy: true,
                    ordering: true,
                    info: false,
                    bFilter: false,
                    lengthChange: false,
                    "processing": true,
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": 5,
                            'render': function (data, type, full, meta) {
                                return formatDate(moment(data).format('YYYY-MM-DDTHH:mm:ss.SSS'));
                            }
                        },
                        {
                            "sDefaultContent": "",
                            "aTargets": ["_all"]
                        }
                    ],
                    "order": [[1, "asc"]]
                });
            }

            function getBalanceDetail(tableId, id, isused) {
                var data;
                if (id == null) {
                    data = {};
                } else {
                    data = {
                        id: id,
                        babStatus: isused
                    };
                }
                $(tableId).DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        'copy',
                        {
                            extend: 'excelHtml5',
                            title: 'Line balance detail'
                        }, 
                        'print'
                    ],
                    "ajax": {
                        "url": "<c:url value="/BabChartController/findLineBalanceDetail" />",
                        "type": "GET",
                        "data": data
                    },
                    "columns": [
                        {data: "bab.id", title: "bab_id", visible: false},
                        {data: "groupid", title: "組別"},
                        {data: "balance", title: "平衡率"},
                        {data: "pass", title: "是否合格"}
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    bAutoWidth: true,
                    destroy: true,
                    ordering: true,
                    info: false,
                    bFilter: false,
                    lengthChange: false,
                    "processing": true,
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": 2,
                            'render': function (data, type, full, meta) {
                                return getPercent(data, round_digit);
                            }
                        },
                        {
                            "type": "html",
                            "targets": 3,
                            'render': function (data, type, full, meta) {
                                return data == 1 ? "是" : "否";
                            }
                        },
                        {
                            "sDefaultContent": "",
                            "aTargets": ["_all"]
                        }
                    ],
                    "order": [[1, "asc"]],
                    "footerCallback": function (row, data, start, end, display) {
                        var columnIndex = 3;
                        var api = this.api(), data;

                        // Remove the formatting to get integer data for summation
                        var intVal = function (i) {
                            return typeof i === 'number' ?
                                    (i == 0 ? 1 : i) : 0;
                        };

                        // Total over all pages
                        total = api
                                .column(columnIndex)
                                .data()
                                .reduce(function (a, b) {
                                    return a + intVal(b);
                                }, 0);

                        // Total over this page
                        passTotal = api
                                .column(columnIndex)
                                .data()
                                .reduce(function (a, b) {
                                    return a + (b == 1 ? 1 : 0);
                                }, 0);

                        failTotal = api
                                .column(columnIndex)
                                .data()
                                .reduce(function (a, b) {
                                    return a + (b == 0 ? 1 : 0);
                                }, 0);

                        // Update footer
                        $(api.column(columnIndex).footer()).html(
                                '<h5>達到標準: ' + passTotal + '組 (' + getPercent((passTotal / total), round_digit) + ')</h5>' +
                                '<h5>未達標準<code>(亮燈頻率)</code>: ' + failTotal + '組 (' + getPercent((failTotal / total), round_digit) + ')</h5>' +
                                '<h5>組別共計: ' + total + ' 組</h5>'
                                );
                    }
                });
            }

            function getChartData(id, isused, chartId) {
                var data;
                if (id == null) {
                    data = {};
                } else {
                    data = {
                        id: id,
                        babStatus: isused
                    };
                }
                $.ajax({
                    url: "<c:url value="/BabChartController/getSensorDiffChart" />",
                    method: 'GET',
                    dataType: 'json',
                    data: data,
                    success: function (d) {
                        var arr = d.data;
                        if (arr != null) {
                            for (var i = 0, j = arr.length; i < j; i++) {
                                var obj = arr[i];
                                obj.type = "line";
                                obj.lineThickness = 3;
                                obj.axisYType = "secondary";
                                obj.showInLegend = true;
                                obj.name = "站別" + (i + 1);
                            }
                        }
                        generateChart(d, chartId);
                    }
                });
            }

//http://canvasjs.com/docs/charts/how-to/hide-unhide-data-series-chart-legend-click/
            function generateChart(d, chartId) {
                var totalAvg = Math.round(d.avg);
                var worktimeAllowancesMin = Math.round(d.worktimeAllowances_min);
                var worktimeAllowancesMax = Math.round(d.worktimeAllowances_max);

                var chart = new CanvasJS.Chart(chartId,
                        {
                            zoomEnabled: true,
                            animationEnabled: true,
                            exportEnabled: true,
                            exportFileName: "Range Area",
                            zoomType: "xy",
                            title: {
                                text: "各站各機台消耗時間(秒)"
                            },
                            axisY2: {
                                title: "時間",
                                valueFormatString: "0 sec",
                                interlacedColor: "#F5F5F5",
                                gridColor: "#D7D7D7",
                                tickColor: "#D7D7D7",
                                minimum: 0,
                                maximum: 600,
                                stripLines: [
                                    {
                                        value: worktimeAllowancesMin,
                                        label: "標工- " + worktimeAllowancesMin,
                                        labelPlacement: "inside",
                                        labelBackgroundColor: "white",
                                        labelFontStyle: "微軟正黑體",
                                        showOnTop: true
                                    },
                                    {
                                        value: worktimeAllowancesMax,
                                        label: "標工+ " + worktimeAllowancesMax,
                                        labelPlacement: "inside",
                                        labelBackgroundColor: "white",
                                        labelFontStyle: "微軟正黑體",
                                        showOnTop: true
                                    },
                                    {
                                        value: totalAvg,
                                        label: totalAvg + "sec - avg",
                                        labelPlacement: "outside",
                                        color: "orange",
                                        labelBackgroundColor: "black",
                                        labelFontStyle: "微軟正黑體",
                                        showOnTop: true
                                    }
                                ]
                            },
                            axisX: {
                                title: "台數",
                                intervalType: "number"
                            },
                            theme: "theme2",
                            toolTip: {
                                shared: true
                            },
                            legend: {
                                verticalAlign: "bottom",
                                horizontalAlign: "center",
                                fontSize: 15,
                                fontFamily: "Lucida Sans Unicode",
                                cursor: "pointer",
                                itemclick: function (e) {
                                    if (typeof (e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
                                        e.dataSeries.visible = false;
                                    } else {
                                        e.dataSeries.visible = true;
                                    }
                                    e.chart.render();
                                }
                            },
                            data: (d.data == null ? [] : d.data)
                        });
                isNoDataAvailable(chart);
                chart.render();
            }

            function isNoDataAvailable(chart) {
                var options = chart.options;
                if (!options.axisY)
                    options.axisY = {};

                if (options.data == 0) {
                    options.title.horizontalAlign = "center";
                    options.title.verticalAlign = "center";
                    options.title.text = 'No result';
                    options.axisY.maximum = 0;
                } else {
                    options.axisY.maximum = null;
                }
            }

            function getHistoryBab() {
                var lineType = $("#lineType2").val();
                var sitefloor = $("#sitefloor").val();
                var startDate = $('#fini').val();
                var endDate = $('#ffin').val();
                var aboveStandard = $("#aboveStandard").is(":checked");

                var table = $("#babHistory").DataTable({
                    dom: 'lfrtip',
                    "serverSide": false,
                    "ajax": {
                        "url": "<c:url value="/SqlViewController/findBabDetail" />",
                        "type": "GET",
                        "data": {
                            lineType_id: lineType,
                            floor_id: sitefloor,
                            startDate: startDate,
                            endDate: endDate,
                            isAboveStandard: aboveStandard
                        }
                    },
                    "columns": [
                        {data: "id", title: "id"},
                        {data: "po", title: "工單"},
                        {data: "modelName", title: "機種"},
                        {data: "line_name", title: "線別"},
                        {data: "floor_name", title: "樓層", visible: false},
                        {data: "people", title: "人數"},
                        {data: "isused", title: "紀錄flag"},
                        {data: "failPercent", title: "亮燈頻率(%)", "sType": "numeric-comma"},
                        {data: "btime", title: "投入時間"},
                        {data: "replyFlag", title: "異常回覆"},
                        {data: "replyFlag", title: "站別詳細"}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": 6,
                            "data": "isused",
                            'render': function (data, type, row) {
                                switch (data) {
                                    case 1:
                                        return "已經關閉";
                                    case - 1:
                                    case - 2:
                                        return "沒有儲存紀錄";
                                    default:
                                        return "尚未關閉";
                                }
                            }
                        },
                        {
                            "type": "html",
                            "targets": 7,
                            "data": "isused",
                            'render': function (data, type, row) {
                                return roundDecimal(((data == null ? 0.0 : data) * 100), 2);
                            }
                        },
                        {
                            "type": "html",
                            "targets": 8,
                            'render': function (data, type, row) {
                                return formatDate(data);
                            }
                        },
                        {
                            "type": "html",
                            "targets": 9,
                            'render': function (data, type, row) {
                                var isCmReply = row.cm_id != null;
                                var isNeedToReply = (row.replyFlag == -1);
                                var isBabAvail = row.isused != -1;

                                if (!isBabAvail) {
                                    return "無紀錄";
                                } else if (!isNeedToReply && row.failPercent != null && !isCmReply) {
                                    return "達到標準";
                                } else if ((!isCmReply && isNeedToReply)) {
                                    return "<input type='button' class='cm-detail btn btn-danger btn-sm' data-toggle= 'modal' data-target='#myModal' value='檢視詳細' />";
                                } else if (isCmReply) {
                                    return "<input type='button' class='cm-detail btn btn-info btn-sm' data-toggle= 'modal' data-target='#myModal' value='檢視詳細' />";
                                } else {
                                    return data;
                                }
                            }
                        },
                        {
                            "type": "html",
                            "targets": 10,
                            'render': function (data, type, row) {
                                return "<input type='button' class='babSetting-detail btn btn-primary btn-sm' data-toggle= 'modal' data-target='#myModal2' value='檢視' />";
                            }
                        }
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                    destroy: true,
                    stateSave: false,
                    "order": [[8, "desc"]],
                    "drawCallback": function (settings) {
                        $.unblockUI();
                    }
                });
                return table;
            }

            $.fn.dataTable.ext.search.push(
                    function (settings, searchData, index, rowData, counter) {
                        var queryString = getQueryVariable("onlyFailRecord");
                        var onlyFailRecord = queryString == null ? false : (queryString.toUpperCase() == "TRUE");

                        var isPass = searchData[9]; // using the data from the 4th column

                        if (queryString == null || !onlyFailRecord || (queryString != null && onlyFailRecord && isPass != "達到標準"))
                        {
                            return true;
                        }
                        return false;
                    }
            );

            function getPercent(val) {
                return roundDecimal((val * 100), round_digit) + '%';
            }

            function roundDecimal(val, precision) {
                var size = Math.pow(10, precision);
                return Math.round(val * size) / size;
            }

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
                    fadeIn: 0
                    , overlayCSS: {
                        backgroundColor: '#FFFFFF',
                        opacity: .3
                    }
                });
            }

            function getBabCompare(bab_id, modelName, lineType) {
                if (modelName != null) {
                    modelName = modelName.trim();
                }

                var postObj;

                if (bab_id == null) {
                    postObj = {
                        "url": "<c:url value="/SqlViewController/findLineBalanceCompare" />",
                        "type": "GET",
                        "data": {
                            "modelName": modelName,
                            "lineTypeName": lineType
                        }
                    };
                } else {
                    postObj = {
                        "url": "<c:url value="/SqlViewController/findLineBalanceCompareByBab" />",
                        "type": "GET",
                        "data": {
                            "bab_id": bab_id
                        }
                    };
                }

                $("#lineBalnHistory").show();
                $("#lineBalnHistory").DataTable({
                    "ajax": postObj,
                    "columns": [
                        {data: "modelName", title: "機種", width: "150px"},
//                          已完結工單資訊(對照組)  
                        {data: "ctrl_id", title: "上次生產Id", visible: false},
                        {data: "ctrl_PO", title: "上次生產<br/>工單", width: "140px"},
                        {data: "ctrl_lineName", title: "上次生產<br/>線別", width: "140px"},
                        {data: "ctrl_alarmPercent", title: "上次生產<br/>亮燈頻率", width: "140px"},
                        {data: "ctrl_btime", title: "投入時間", width: "100px"},
//                          最後一筆生產紀錄(實驗組)  
                        {data: "exp_id", title: "本次生產Id", visible: false},
                        {data: "exp_PO", title: "本次生產<br/>工單", width: "140px"},
                        {data: "exp_lineName", title: "本次生產<br/>線別", width: "140px"},
                        {data: "exp_alarmPercent", title: "本次生產<br/>亮燈頻率", width: "140px"},
                        {data: "exp_btime", title: "投入時間", width: "100px"},
//                          狀態  
                        {data: 1, title: "狀態", width: "40px"}
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    "columnDefs": [
                        {className: "ctrl", "targets": [1, 2, 3, 4, 5]},
                        {className: "exp", "targets": [6, 7, 8, 9, 10]},
                        {
                            "type": "html",
                            "targets": 4,
                            "data": "ctrl_alarmPercent",
                            'render': function (data, type, full, meta) {
                                return ((data == null || data == "") ? 0 : getPercent(data));
                            }
                        },
                        {
                            "type": "html",
                            "targets": 9,
                            "data": "exp_alarmPercent",
                            'render': function (data, type, full, meta) {
                                return ((data == null || data == "") ? 0 : getPercent(data));
                            }
                        },
                        {
                            "type": "html",
                            "targets": 11,
                            'render': function (data, type, full, meta) {

                                var ctrlPercent = full.ctrl_alarmPercent;
                                var expPercent = full.exp_alarmPercent;
                                if (full.exp_id == null || full.ctrl_id == null)
                                    return 'N/A';
                                else if (expPercent > ctrlPercent)
                                    return '變差';
                                else if (expPercent < ctrlPercent)
                                    return '變好';
                                else if (expPercent == ctrlPercent)
                                    return '持平';

                            }
                        },
                        {
                            "type": "html",
                            "targets": '_all',
                            'render': function (data, type, full, meta) {
                                return (data == null || data == "") ? 'N/A' : data;
                            }
                        }
                    ],
                    "createdRow": function (row, data, dataIndex) {
                        var closedAlarmPercent = data.ctrl_alarmPercent;
                        var lastAlarmPercent = data.exp_alarmPercent;
                        var lastbab_id = data.ctrl_id;
                        if (closedAlarmPercent < lastAlarmPercent && lastbab_id != null) {
                            $(row).addClass('alarm');
                        }

                    },
                    autoWidth: true,
                    destroy: true,
                    paging: false,
                    info: false,
                    bFilter: false,
                    "processing": true,
                    "initComplete": function (settings, json) {
                        var arrObj = json.data;
                        if (arrObj.length == 0) {
                            $("#totalDetail").hide();
                            $.unblockUI();
                            return;
                        }
                        var jsonObj = arrObj[arrObj.length - 1];

                        var ctrlId = jsonObj.ctrl_id;
                        var ctrl_isused = jsonObj.ctrl_isused;

                        var expId = jsonObj.exp_id;
                        var exp_isused = jsonObj.exp_isused;

                        var ctrlAvgs = jsonObj["ctrl_avgs"];
                        var expAvgs = jsonObj["exp_avgs"];

                        $("#totalDetail, #totalDetail > div").show();

                        getBalanceDetail("#ctrlBalanceDetail", ctrlId, ctrl_isused);
                        getPcsDetail("#ctrlPcsDetail", ctrlId, ctrl_isused);
                        getChartData(ctrlId, ctrl_isused, "chartContainer1");

                        getBalanceDetail("#expBalanceDetail", expId, exp_isused);
                        getPcsDetail("#expPcsDetail", expId, exp_isused);
                        getChartData(expId, exp_isused, "chartContainer2");

                        $("#ctrlBalance").html("線平衡率: " + getPercent(ctrlAvgs, round_digit));
                        $("#expBalance").html("線平衡率: " + getPercent(expAvgs, round_digit));

                        $.unblockUI();
                    }
                });
            }

            function initDateTimePickerWiget() {
                var lockDays = 30;
                var momentFormatString = 'YYYY-MM-DD';
                var options = {
                    defaultDate: moment(),
                    useCurrent: true,
                    maxDate: moment(),
                    format: momentFormatString,
                    extraFormats: [momentFormatString]
                };
                beginTimeObj = $('#fini').datetimepicker(options);
                endTimeObj = $('#ffin').datetimepicker(options);

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
            }

            function getBabSettingHistory(bab_id) {
                $("#babSettingHistory").DataTable({
                    dom: 'lfrtip',
                    "serverSide": false,
                    "ajax": {
                        "url": "<c:url value="/BabSettingHistoryController/findByBab" />",
                        "type": "GET",
                        "data": {
                            "id": bab_id
                        }
                    },
                    "columns": [
                        {data: "station"},
                        {data: "tagName.name"},
                        {data: "jobnumber"},
                        {data: "createTime"},
                        {data: "lastUpdateTime"}
                    ],
                    "columnDefs": [
                        {
                            "type": "html",
                            "targets": [3, 4],
                            "data": "ctrl_alarmPercent",
                            'render': function (data, type, full, meta) {
                                return data == null ? 'n/a' : formatDate(data);
                            }
                        }
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    searching: false,
                    paging: false,
                    "bInfo": false,
                    destroy: true,
                    "drawCallback": function (settings) {
                        $.unblockUI();
                    }
                });
            }

            function formatDate(dateString) {
                return dateString.substring(0, 16);
            }

            $(function () {
                $('[data-toggle="tooltip"]').tooltip();
                var interval = null;
                var countdownnumber = 30 * 60;
                var diff = 12;

                setLineObject();

                var saveModelName = $.cookie('lastPOInsert');
                var saveLineType = $.cookie('lastLineTypeSelect');
                if (saveModelName != null) {
                    $("#modelName").val(saveModelName);
                    $("#lineType").val(saveLineType);
                }

                $("input, select").not(":checkbox").addClass("form-control");
                $(":button").addClass("btn btn-default");

                initCountermeasureDialog({
                    queryUrl: "<c:url value="/CountermeasureController/findByBab" />",
                    saveUrl: "<c:url value="/CountermeasureController/update" />",
                    actionCodeQueryUrl: "<c:url value="/CountermeasureController/getActionCodeOptions" />",
                    errorCodeQueryUrl: "<c:url value="/CountermeasureController/getErrorCodeOptions" />"
                }, historyTable, countermeasureType, '${isAuthenticated ? user.jobnumber : null}');

                initDateTimePickerWiget();
                initSelectOption();

                //http://stackoverflow.com/questions/14493250/ajax-jquery-autocomplete-with-json-data
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabController/findAllModelName" />",
                    dataType: "json",
                    success: function (data) {
                        $("input#modelName").autocomplete({
                            width: 300,
                            max: 10,
                            delay: 100,
                            minLength: 3,
                            autoFocus: true,
                            cacheLength: 1,
                            scroll: true,
                            highlight: false,
                            source: function (request, response) {
                                var term = $.trim(request.term);
                                var matcher = new RegExp('^' + $.ui.autocomplete.escapeRegex(term), "i");

                                response($.map(data, function (v, i) {
                                    var text = v;
                                    if (text && (!request.term || matcher.test(text))) {
                                        return {
                                            label: v,
                                            value: v
                                        };
                                    }
                                }));
                            }
                        });
                    }
                });


                $("#searchAvailableBab").click(function () {
                    historyTable = getHistoryBab();
                    initTable(historyTable);
                });

                $("body").on('dblclick', '#babHistory tbody tr', function () {
                    var selectData = historyTable.row(this).data();
                    var bab_id = selectData.id;
                    var ModelName = selectData.modelName;
                    var isused = selectData.isused;
                    var lineType = $("#lineType2").val();

                    if (isused == -1) {
                        alert("此筆記錄無統計數據。");
                        return;
                    }

                    block();

                    $("#modelName").val(ModelName);
                    $("#lineType").val(lineType);

                    getBabCompare(bab_id, ModelName, lineType);

                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                    } else {
                        historyTable.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                    }

                });

                $("#send").on("click", function () {
                    var modelName = $("#modelName").val();
                    var lineType = $("#lineType").val();

                    if (modelName == null || modelName == "" || lineType == -1) {
                        return false;
                    }

                    $.cookie('lastPOInsert', modelName);
                    $.cookie('lastLineTypeSelect', lineType);

                    getBabCompare(null, modelName, lineType);
                });

                $("body").on('click', '.babSetting-detail', function () {
                    var selectData = historyTable.row($(this).parents('tr')).data();
                    editId = selectData.id;
                    var modal = $($(this).attr("data-target"));
                    modal.find(".modal-title").html(
                            "號碼: " + editId +
                            " / 工單: " + selectData.po +
                            " / 機種: " + selectData.modelName +
                            " / 線別: " + selectData.lineName +
                            " / 時間: " + formatDate(selectData.btime)
                            );
                    getBabSettingHistory(selectData.id);

                });

                var excelExport = function () {
                    var id = $(this).attr("id");
                    var url = id == "generateExcel" ? "getTotalInfoReport" : "getEfficiencyReport";

                    var lineType = $('#lineType2').val();
                    var sitefloor = $('#sitefloor').val();
                    var startDate = $('#fini').val();
                    var endDate = $('#ffin').val();
                    var aboveStandard = $("#aboveStandard").is(":checked");

                    $(".excel_export").attr("disabled", true);
                    $.fileDownload('<c:url value="/ExcelExportController/" />' + url + '?startDate=' + startDate + '&endDate=' + endDate + '&lineType_id=' + lineType + '&floor_id=' + sitefloor + '&aboveStandard=' + aboveStandard, {
                        preparingMessageHtml: "We are preparing your report, please wait...",
                        failMessageHtml: "No reports generated. No Survey data is available.",
                        successCallback: function (url) {
                            $(".excel_export").attr("disabled", false);
                        }
                        , failCallback: function (html, url) {
                            $(".excel_export").attr("disabled", false);
                        }
                    });
                };

                $("#generateExcel").click(excelExport);
                $("#excelForEfficiencyReport").click(excelExport);

                var babId = getQueryVariable("babId");
                if (babId != null) {
                    $("#modelName").val("");
                    getBabCompare(babId, null, null);

                    block();
                    setTimeout(function () {
                        window.location.hash = "#babDetailSearch";
                        $(".exp, .expBalanceDetail").addClass("expose");

                        $('.expose').css('z-index', '99999');
                        $('#overlay').fadeIn(300);
                        $('#overlay').delay(2000).fadeOut(300, function () {
                            $('.expose').css('z-index', '1');
                        });
                    }, 1000);
                }

                var lineType = getQueryVariable("lineType");
                var startDate = getQueryVariable("startDate");
                var endDate = getQueryVariable("endDate");

                if (startDate != null && endDate != null) {
                    beginTimeObj.data("DateTimePicker").date(startDate);
                    endTimeObj.data("DateTimePicker").date(endDate);
                    $("#searchAvailableBab").trigger("click");
                    historyTable.draw();
                }

                if (lineType != null) {
                    $("#lineType, #lineType2").find('option:contains(' + lineType + ')').attr("selected", "selected");
                }

                var readonly = getQueryVariable("readonly") == null ? false : (getQueryVariable("readonly").toUpperCase() == "TRUE");
                if (readonly || ${!isAuthenticated}) {
                    $("#editCountermeasure").off("click").attr("disabled", true);
                    $("#countermeasureEditHint").show();
                }

                $(window).on("blur", function () {
                    clearInterval(autoReloadInterval);
                });

                $("body").on("click", "#searchAvailableBab, #send", function () {
                    block();
                });

                $.fn.dataTable.ext.errMode = function (settings, helpPage, message) {
                    console.log(message);
                };
            });

        </script>
    </head>
    <body>
        <div>
            <!-- Modal -->
            <div id="myModal" class="modal fade" role="dialog">
                <div class="modal-dialog">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 id="titleMessage" class="modal-title"></h4>
                        </div>
                        <div class="modal-body">
                            <div>
                                <table id="countermeasureTable" cellspacing="10" class="table table-bordered">
                                    <tr>
                                        <td class="lab">Error Code</td>
                                        <td id="errorCode" > 
                                            <div class="checkbox">
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">Action Code</td>
                                        <td id="actionCode" > 
                                            <div class="checkbox">
                                                <label class="checkbox-inline">
                                                    <input type="checkbox" name="actionCode">
                                                </label>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">說明</td>
                                        <td id="errorCon" >
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">SOP資訊</td>
                                        <td>
                                            <label id="sopHint" hidden="">※請輸入完整Sop名稱及頁數:</label>
                                            <div id="sop"></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="lab">最後修改人員</td>
                                        <td id="responseUser" >
                                        </td>
                                    </tr>
                                </table>
                                <div id="dialog-msg" class="alarm"></div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <label id="countermeasureEditHint" for="editCountermeasure" hidden="">請<a href="<c:url value="/login" /> " target='_parent'>登入</a>做異常回覆</label>
                            <button type="button" id="editCountermeasure" class="btn btn-default" >Edit</button>
                            <button type="button" id="saveCountermeasure" class="btn btn-default">Save</button>
                            <button type="button" id="undoContent" class="btn btn-default">Undo</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Modal2 -->
            <div id="myModal2" class="modal fade" role="dialog">
                <div class="modal-dialog">

                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 id="titleMessage2" class="modal-title"></h4>
                        </div>
                        <div class="modal-body">
                            <div>
                                <table id="babSettingHistory" class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>station</th>
                                            <th>tagName</th>
                                            <th>jobnumber</th>
                                            <th>btime</th>
                                            <th>lastUpdateTime</th>
                                        </tr>
                                    </thead>
                                </table>
                                <div id="dialog-msg2" class="alarm"></div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>

                </div>
            </div>
            <!----->
            <div class="wiget-ctrl form-inline">
                <div id="bab_HistoryList">
                    <h3>可查詢歷史紀錄</h3>
                    <p class="alarm">※雙擊表格內的內容可直接於下方帶出資料。</p>
                    <div class="search-container">
                        <div class="ui-widget">
                            <select id="lineType2"> 
                                <option value="-1">all</option>
                            </select> /
                            <select id="sitefloor">
                                <option value="-1">all</option>
                            </select> /

                            日期:從
                            <div class='input-group date' id='beginTime'>
                                <input type="text" id="fini" placeholder="請選擇起始時間"> 
                            </div> 
                            到 
                            <div class='input-group date' id='endTime'>
                                <input type="text" id="ffin" placeholder="請選擇結束時間"> 
                            </div>
                            <label for="aboveStandard"><input type="checkbox" id="aboveStandard">只顯示數量大於十台</label>
                            <input type="button" id="searchAvailableBab" value="查詢">
                            <input type="button" id="generateExcel" class="excel_export" value="產出excel">
                        </div>
                    </div>
                    <div style="width: 90%; background-color: #F5F5F5">
                        <div style="padding: 10px">
                            <table id="babHistory" class="display" cellspacing="0" width="100%" style="text-align: center">
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <hr />
            <!----->
            <div class="wiget-ctrl form-inline">
                <h3 id="babDetailSearch">機種平衡率紀錄查詢</h3>
                <div class="search-container">
                    <div class="ui-widget">
                        <label for="modelName">請輸入機種號碼: </label>
                        <input type="text" id="modelName" />
                        <select id="lineType">
                            <option value=-1>請選擇線別</option>
                        </select>
                        <input type="button" id="send" value="查詢">
                    </div>
                </div>

                <div id="serverMsg"></div>
                <div>
                    <table id="lineBalnHistory" class="table table-bordered" hidden>
                    </table>
                </div>
            </div>
            <!----->
            <div class="wiget-ctrl">
                <div id="totalDetail" hidden>
                    <div class="ctrlDetail">
                        <div id="ctrlBalance" class="balance"></div>
                        <div id="ctrlWorktime" class="worktime"></div>
                        <h3>上次生產</h3>
                        <div class="detailTables">
                            <div class="detail">
                                <table id="ctrlBalanceDetail" class="table table-bordered">
                                    <tfoot>
                                        <tr>
                                            <th colspan="3" style="text-align:right">Total:</th>
                                            <th></th>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                            <div class="detail">
                                <table id="ctrlPcsDetail" class="table table-bordered">
                                </table>
                            </div>
                        </div>
                        <div id="chartContainer1" class="chartContainer">
                        </div>
                    </div>

                    <div class="expDetail">
                        <div id="expBalance" class="balance"></div>
                        <div id="expWorktime" class="worktime"></div>
                        <h3>本次生產</h3>
                        <div class="detailTables">
                            <div class="detail">
                                <table id="expBalanceDetail" class="table table-bordered">
                                    <tfoot>
                                        <tr>
                                            <th colspan="3" style="text-align:right">Total:</th>
                                            <th></th>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                            <div class="detail">
                                <table id="expPcsDetail" class="table table-bordered">
                                </table>
                            </div>
                        </div>
                        <div id="chartContainer2" class="chartContainer">
                        </div>
                    </div>
                </div>
            </div>
            <div id="overlay"></div>
        </div>
        <div style="clear: both"></div>
    </body>
</html>
