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
        <link rel="stylesheet" href="<c:url value="/webjars/jquery-ui-themes/1.12.1/redmond/jquery-ui.min.css" />" >
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
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/js/jquery-ui-1.10.0.custom.min.js" />"></script>
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

        <script>
            var table;
            const urlLineType = getQueryVariable("lineType");
            const lineTypeId = (urlLineType == null || urlLineType == "ASSY" ? 1 : 3);
            $(function () {
                const countermeasureType = "Bab_Abnormal_LineProductivity";

                initLineObject();
                initCountermeasureDialog({
                    queryUrl: "<c:url value="/CountermeasureController/findByBab" />",
                    saveUrl: "<c:url value="/CountermeasureController/update" />",
                    actionCodeQueryUrl: "<c:url value="/CountermeasureController/getActionCodeOptions" />",
                    errorCodeQueryUrl: "<c:url value="/CountermeasureController/getErrorCodeOptions" />"
                }, table, countermeasureType, '${isAuthenticated ? user.jobnumber : null}');

                const momentFormatString = 'YYYY-MM-DD';
                $(":text,input[type='number'],select").addClass("form-control");
                $(":button").addClass("btn btn-default");
                const options = {
                    defaultDate: moment(),
                    useCurrent: true,
                    //locale: "zh-tw",
                    format: momentFormatString,
                    extraFormats: [momentFormatString]
                };

                const beginTimeObj = $('#fini').datetimepicker(options);
                const endTimeObj = $('#ffin').datetimepicker(options);

                $("#send").click(function () {
                    getDetail();
                });

                const lockDays = 180;
                beginTimeObj.on("dp.change", function (e) {
                    endTimeObj.data("DateTimePicker").minDate(e.date);
                    const beginDate = e.date;
                    const endDate = endTimeObj.data("DateTimePicker").date();
                    const dateDiff = endDate.diff(beginDate, 'days');
                    if (dateDiff > 30) {
                        endTimeObj.data("DateTimePicker").date(beginDate.add(lockDays, 'days'));
                    }
                });

                endTimeObj.on("dp.change", function (e) {
                    const beginDate = beginTimeObj.data("DateTimePicker").date();
                    const endDate = e.date;
                    const dateDiff = endDate.diff(beginDate, 'days');
                    if (dateDiff > 30) {
                        beginTimeObj.data("DateTimePicker").date(endDate.add(-lockDays, 'days'));
                    }
                });

                $(":text").keyup(function () {
                    $(this).val($(this).val().toUpperCase());
                });

                $.ajax({
                    type: "Get",
                    url: "<c:url value="/BabController/findAllModelName" />",
                    dataType: "json",
                    success: function (data) {
                        $("#modelName").autocomplete({
                            width: 300,
                            max: 10,
                            delay: 100,
                            minLength: 3,
                            autoFocus: true,
                            cacheLength: 1,
                            scroll: true,
                            highlight: false,
                            source: function (request, response) {
                                const term = $.trim(request.term);
                                const matcher = new RegExp('^' + $.ui.autocomplete.escapeRegex(term), "i");

                                response($.map(data, function (v, i) {
                                    let text = v;
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

                const urlLineType = getQueryVariable("lineType");

                if (urlLineType != null) {
                    $("#lineType").val(urlLineType);
                }

            <c:if test="${isBackDoor4876 || isAdmin}">

                // Handle click on "Select all" control
                $('body').on('click', '#select_all', function () {
                    // Get all rows with search applied
                    const rows = table.rows({'search': 'applied'}).nodes();
                    // Check/uncheck checkboxes for all rows in the table
                    $('input[type="checkbox"]', rows).prop('checked', this.checked);
                });

                // Handle click on checkbox to set state of "Select all" control
                $('body').on('change', '#BabDetail tbody', 'input[type="checkbox"]', function () {
                    // If checkbox is not checked
                    if (!this.checked) {
                        let el = $('#select_all').get(0);
                        // If "Select all" control is checked and has 'indeterminate' property
                        if (el && el.checked && ('indeterminate' in el)) {
                            // Set visual state of "Select all" control
                            // as 'indeterminate'
                            el.indeterminate = true;
                        }
                    }
                });

                // Handle form submission event
                $('body').on('click', '#babDel', function () {
                    var arr = table.$('input[type="checkbox"]:checked').map(function () {
                        return this.value;
                    }).get();
                    var ids = arr.join(",");

                    if (!confirm("確定刪除所選紀錄?\n紀錄將被隱藏")) {
                        return false;
                    }

                    $.ajax({
                        type: "POST",
                        url: "<c:url value="/BabLineProductivityExcludeController/insert" />",
                        data: {
                            "id[]": ids
                        },
                        success: function (response) {
                            if (response == "success") {
                                table.ajax.reload();
                            }
                            alert(response);
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            showMsg(xhr.responseText);
                        }
                    });
                });
            </c:if>

                if (${!isAuthenticated}) {
                    $("#editCountermeasure").off("click").attr("disabled", true);
                    $("#countermeasureEditHint").show();
                }

            });

            function getDetail() {
                $("#send").attr("disabled", true);
                table = $("#BabDetail").DataTable({
                    dom: 'Bfrtip',
                    buttons: [
                        'copy',
                        {
                            extend: 'excel',
                            exportOptions: {
                                columns: 'th:not(:first-child):not(:last-child)'
                            }
                        },
                        'print'
                    ],
                    "processing": true,
                    "serverSide": false,
                    fixedHeader: {
                        headerOffset: 50
                    },
                    "ajax": {
                        "url": "<c:url value="/SqlViewController/findBabLineProductivity" />",
                        "type": "Get",
                        data: {
                            po: $("#po").val(),
                            modelName: $("#modelName").val(),
                            line_id: $("#line").val(),
                            lineType_id: lineTypeId,
                            jobnumber: $("#jobnumber").val(),
                            minPcs: $("#aboveMinPcs").is(":checked") ? 20 : null,
                            startDate: $('#fini').val(),
                            endDate: $('#ffin').val()
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    },
                    "columns": [
                        {data: "id"},
                        {data: "id"},
                        {data: "po"},
                        {data: "modelName"},
                        {data: "lineName"},
                        {data: "sitefloor"},
                        {data: "people"},
                        {data: "s1tag", visible: false},
                        {data: "s1usr", visible: false},
                        {data: "s1time", visible: false},
                        {data: "s2tag", visible: false},
                        {data: "s2usr", visible: false},
                        {data: "s2time", visible: false},
                        {data: "s3tag", visible: false},
                        {data: "s3usr", visible: false},
                        {data: "s3time", visible: false},
                        {data: "s4tag", visible: false},
                        {data: "s4usr", visible: false},
                        {data: "s4time", visible: false},
                        {data: "s5tag", visible: false},
                        {data: "s5usr", visible: false},
                        {data: "s5time", visible: false},
                        {data: "s6tag", visible: false},
                        {data: "s6usr", visible: false},
                        {data: "s6time", visible: false},
                        {data: "totalPcs"},
                        {data: "standardTime"},
                        {data: "timeCost"},
                        {data: "productivity"},
                        {data: "btime"},
                        {data: "lastUpdateTime"},
                        {data: "solution", visible: false},
                        {data: "code", visible: false},
                        {data: "lastEditor", visible: false},
                        {data: "replyFlag"}
                    ],
                    "columnDefs": [
                        {
                            'targets': 0,
                            'searchable': false,
                            'orderable': false,
                            'className': 'dt-body-center',
                            'render': function (data, type, full, meta) {
                                return '<input type="checkbox" name="id[]" value="' + $('<div/>').text(data).html() + '">';
                            }
                        },
                        {
                            "type": "html",
                            "targets": [7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
                            'render': function (data, type, full, meta) {
                                return data == null ? 0 : data;
                            }
                        },
                        {
                            "type": "html",
                            "targets": [29, 30],
                            'render': function (data, type, full, meta) {
                                return formatDate(data);
                            }
                        },
                        {
                            "type": "html",
                            "targets": [27],
                            'render': function (data, type, full, meta) {
                                return roundDecimal(data, 2);
                            }
                        },
                        {
                            "type": "html",
                            "targets": [28],
                            'render': function (data, type, full, meta) {
                                return getPercent(data);
                            }
                        },
                        {
                            "type": "html",
                            "targets": [34],
                            'render': function (data, type, full, meta) {
                                switch (data) {
                                    case 1:
                                        return "<input type='button' class='cm-detail btn btn-info btn-sm' data-toggle= 'modal' data-target='#myModal' value='檢視詳細' />";
                                    case - 1:
                                        return "<input type='button' class='cm-detail btn btn-danger btn-sm' data-toggle= 'modal' data-target='#myModal' value='檢視詳細' />";
                                    case 0:
                                        return "達到標準";
                                    default:
                                        return "黑人問號";
                                }
                            }
                        }
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
                    filter: false,
                    info: true,
                    paginate: true,
                    pageLength: 10,
                    destroy: true,
                    "initComplete": function (settings, json) {
                        $("#send").attr("disabled", false);
                        $("#BabDetail").show();
                    },
                    "order": [[1, "asc"]]
                });
            }

            function initLineObject() {
                $.ajax({
                    type: "GET",
                    url: "<c:url value="/BabLineController/findAll" />",
                    dataType: "json",
                    success: function (response) {
                        const lineWiget = $("#line");
                        const arr = response;
                        for (let i = 0; i < arr.length; i++) {
                            const {id, name} = arr[i];
                            lineWiget.append("<option value=" + id + ">" + name + "</option>");
                        }
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        showMsg(xhr.responseText);
                    }
                });
            }

            function formatDate(dateString) {
                return moment(dateString).format('YYYY-MM-DD HH:mm');
            }

            function getPercent(val) {
                return roundDecimal((val * 100), 2) + '%';
            }

            function roundDecimal(val, precision) {
                const size = Math.pow(10, precision);
                return Math.round(val * size) / size;
            }

        </script>
    </head>
    <body>
        <c:import url="/temp/admin-header.jsp" />
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

        <div class="container">
            <div>
                <h3 class="title">線體效率查詢</h3>
                <h5 class="subTitle">※僅會顯示機種未排外的紀錄</h5>
            </div>
            <div class="row form-inline">
                <div class="col form-group">
                    <input type="text" id="jobnumber" placeholder="工號" />
                </div>
                <div class="col form-group">
                    <input type="text" id="po" placeholder="工單" />
                </div>
                <div class="col form-group">
                    <input type="text" id="modelName" placeholder="機種" />
                </div>
                <div class="col form-group">
                    <select id="line" class="form-control">
                        <option value="-1">請選擇線別</option>
                    </select>
                </div>

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

            </div>

            <div class="row form-inline">
                <div class="col form-group">
                    <input type="checkbox" id="aboveMinPcs" > 
                    <label for="aboveMinPcs">大於20Pcs</label>
                </div>
                <div class="col form-group">
                    <input type="button" id="send" value="確定(Ok)">
                </div>
            </div>

            <div class="row">
                <table id="BabDetail" class="table table-striped" cellspacing="0" width="100%" style="text-align: center" hidden>
                    <thead>
                        <tr>
                            <th><input name="select_all" value="1" id="select_all" type="checkbox"></th>
                            <th>id</th>
                            <th>工單</th>
                            <th>機種</th>
                            <th>線別</th>
                            <th>樓層</th>
                            <th>人數</th>
                            <th>站1</th>
                            <th>站1人員</th>
                            <th>站1時間</th>
                            <th>站2</th>
                            <th>站2人員</th>
                            <th>站2時間</th>
                            <th>站3</th>
                            <th>站3人員</th>
                            <th>站3時間</th>
                            <th>站4</th>
                            <th>站4人員</th>
                            <th>站4時間</th>
                            <th>站5</th>
                            <th>站5人員</th>
                            <th>站5時間</th>
                            <th>站6</th>
                            <th>站6人員</th>
                            <th>站6時間</th>
                            <th>數量</th>
                            <th>標工</th>
                            <th>總耗時(分)</th>
                            <th>效率</th>
                            <th>開始</th>
                            <th>結束</th>
                            <th>異常原因</th>
                            <th>code</th>
                            <th>回覆人</th>
                            <th>異常回覆</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>
                                <c:if test="${isBackDoor4876 || isAdmin}">
                                    <input type="button" value="Del" class="btn btn-danger" id="babDel" />
                                </c:if>
                            </th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>

        <c:import url="/temp/admin-footer.jsp" />
    </body>
</html>
