<%-- 
    Document   : test
    Created on : 2015/11/20, 上午 11:36:50
    Author     : Wei.Cheng
https://datatables.net/forums/discussion/20388/trying-to-access-rowdata-in-render-function-with-ajax-datasource-getting-undefined
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="../../images/favicon.ico"/>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">
        <link rel="stylesheet" href="../../css/jquery-ui.css">
        <link rel="stylesheet" href="../../css/bootstrap-datetimepicker.min.css">
        <link rel="stylesheet" href="../../css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="../../css/fixedHeader.dataTables.min.css">
        <link rel="stylesheet" href="../../css/buttons.dataTables.min.css">
        <style>
            body {
                font-family: 微軟正黑體;
            }
            table th{
                text-align: center;
            }
        </style>
        <script src="../../js/jquery-1.11.3.min.js"></script>
        <script src="../../js/bootstrap.min.js"></script>
        <script src="../../js/jquery-ui-1.10.0.custom.min.js"></script>
        <script src="../../js/moment.js"></script>
        <script src="../../js/bootstrap-datetimepicker.min.js"></script>
        <script src="../../js/jquery.dataTables.min.js"></script>
        <script src="../../js/dataTables.fixedHeader.min.js"></script>
        <script src="../../js/jquery-datatable-button/dataTables.buttons.min.js"></script>
        <script src="../../js/jquery-datatable-button/buttons.flash.min.js"></script>
        <script src="../../js/jquery-datatable-button/jszip.min.js"></script>
        <script src="../../js/jquery-datatable-button/pdfmake.min.js"></script>
        <script src="../../js/jquery-datatable-button/vfs_fonts.js"></script>
        <script src="../../js/jquery-datatable-button/buttons.html5.min.js"></script>
        <script src="../../js/jquery-datatable-button/buttons.print.min.js"></script>
        <script src="../../js/urlParamGetter.js"></script>
        <script>
            var maxProductivity = 200;
            var table;
            var beginTimeObj, endTimeObj;
            var lockDays = 30;

            var jobnumber;
            var startDate;
            var endDate;
            var onlyFailRecord;

            $(document).ready(function () {

                var momentFormatString = 'YYYY-MM-DD';
                $(":text,input[type='number'],select").addClass("form-control");
                $(":button").addClass("btn btn-default");
                var options = {
                    useCurrent: true,
                    //locale: "zh-tw",
                    format: momentFormatString,
                    extraFormats: [momentFormatString]
                };

                //以免把時間區間定在六日沒有資料，此狀況會在moment是禮拜一時發生
                var prevDay = moment().add(-2, "days");
                if (prevDay.day() == 6 || prevDay.day() == 7) {
                    prevDay.add(-2, "days");
                }

                options.defaultDate = prevDay;
                var beginTimeObj = $('#fini').datetimepicker(options);

                options.defaultDate = moment();
                var endTimeObj = $('#ffin').datetimepicker(options);

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

                $("#send").click(function () {

                    console.log("I'm clicked");

                    var startDate = $('#fini').val();
                    var endDate = $('#ffin').val();

                    getDetail(startDate, endDate);
                });

                $("body").on('click', '#testDetail tbody tr', function () {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                    } else {
                        $('#testDetail tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                    }
                });

                jobnumber = getQueryVariable("jobnumber");
                startDate = getQueryVariable("startDate");
                endDate = getQueryVariable("endDate");
                onlyFailRecord = getQueryVariable("onlyFailRecord") == null ? false : (getQueryVariable("onlyFailRecord").toUpperCase() == "TRUE");

                //date is null -> find jobnumber if null not search else search & filter
                //date is not null -> search -> find jobnumber if not null -> filter
                //date & jobnumber is null -> not search
                //date & jobnumber is not null -> search -> filter

                if ((startDate != null && endDate != null)) {
                    beginTimeObj.data("DateTimePicker").date(startDate);
                    endTimeObj.data("DateTimePicker").date(endDate);
                }

                if ((startDate != null && endDate != null) || jobnumber != null) {
                    $("#send").trigger("click");
                }

                if (table != null && (!onlyFailRecord || jobnumber != null)) {
                    console.log(1);
                    table.draw();
                }


            });

            function getDetail(startDate, endDate) {

                table = $("#testDetail").DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        'copy', 'excel', 'print'
                    ],
                    "processing": true,
                    "serverSide": false,
                    fixedHeader: {
                        headerOffset: 0
                    },
                    "ajax": {
                        "url": "../../GetClosedInfo",
                        "type": "POST",
                        data: {
                            startDate: startDate,
                            endDate: endDate,
                            action: "getTest"
                        }
                    },
                    "columns": [
                        {data: "id", visible: false},
                        {data: "user_id"},
                        {data: "user_name"},
                        {data: "productivity"},
                        {data: "table_id"},
                        {data: "saveTime"}
                    ],
                    "columnDefs": [
                        {
                            "targets": 3,
                            'render': function (data, type, full, meta) {
                                var productivity = getPercent(data);
                                return (productivity > maxProductivity ? maxProductivity : productivity) + "%";
                            }
                        },
                        {
                            "targets": 5,
                            'render': function (data, type, full, meta) {
                                return formatDate(data);
                            }
                        }
                    ],
                    "oLanguage": {
                        "sLengthMenu": "顯示 _MENU_ 筆記錄",
                        "sZeroRecords": "無符合資料",
                        "sInfo": "目前記錄：_START_ 至 _END_, 總筆數：_TOTAL_"
                    },
                    bAutoWidth: false,
                    displayLength: -1,
                    lengthChange: false,
                    filter: true,
                    info: true,
                    paginate: false,
                    destroy: true,
                    "initComplete": function (settings, json) {
                        $("#testDetail").show();
                    },
                    "order": [[5, "asc"]]
                });
            }

            function formatDate(dateString) {
                return dateString.substring(0, 16);
            }

            function getPercent(val) {
                return roundDecimal((val * 100), 0);
            }

            function roundDecimal(val, precision) {
                var size = Math.pow(10, precision);
                return Math.round(val * size) / size;
            }

            $.fn.dataTable.ext.search.push(
                    function (settings, searchData, index, rowData, counter) {
                        var productivity = rowData.productivity; // using the data from the 4th column
                        var user = rowData.user_id;
                        if ((!onlyFailRecord && jobnumber == null) ||
                                (onlyFailRecord && productivity < 0.8 && jobnumber == null) ||
                                (!onlyFailRecord && jobnumber != null && user == jobnumber) ||
                                (onlyFailRecord && jobnumber != null && productivity < 0.8 && user == jobnumber)
                                )
                        {
                            return true;
                        }
                        return false;
                    }
            );

        </script>
    </head>
    <body>
        <div class="container">
            <h3>測試線別資料擷取紀錄查詢</h3>
            <div>
                <table id="leaveRequest" class="table">
                    <tr>
                        <td>
                            <div class="form-group form-inline">
                                日期:從
                                <div class='input-group date' id='beginTime'>
                                    <input type="text" id="fini" placeholder="請選擇起始時間"> 
                                </div> 
                                到 
                                <div class='input-group date' id='endTime'>
                                    <input type="text" id="ffin" placeholder="請選擇結束時間"> 
                                </div>
                                <input type="button" id="send" value="確定(Ok)">
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div>
                <table id="testDetail" class="display" cellspacing="0" width="100%" style="text-align: center" hidden>
                    <thead>
                        <tr>
                            <th>id</th>
                            <th>人員工號</th>
                            <th>人員姓名</th>
                            <th>效率</th>
                            <th>桌次</th>
                            <th>儲存時間</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>
    </body>
</html>
