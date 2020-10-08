<%-- 
    Document   : modelSopRemark
    Created on : 2018/5/22, 上午 11:28:40
    Author     : Wei.Cheng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication var="user" property="principal" />
<sec:authorize access="hasRole('ADMIN')"  var="isAdmin" />
<sec:authorize access="hasRole('OPER_MFG')"  var="isMfgOper" />
<sec:authorize access="hasRole('BACKDOOR_4876_')"  var="isBackDoor4876" />
<sec:authorize access="hasRole('OPER_IE')"  var="isIeOper" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${initParam.pageTitle}</title>
        <link rel="shortcut icon" href="../../images/favicon.ico"/>
        <link rel="stylesheet" href="<c:url value="/webjars/jquery-ui-themes/1.12.1/redmond/jquery-ui.min.css" />" >
        <link rel="stylesheet" href="<c:url value="/webjars/datatables/1.10.16/css/jquery.dataTables.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-datetimepicker.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/buttons.dataTables.min.css" />">
        <link rel="stylesheet" href="<c:url value="/css/select2.min.css" />">
        <style>
            #wigetCtrl{
                margin: 0 auto;
                width: 98%;
            }
            body {
                padding-top: 70px;
                /* Required padding for .navbar-fixed-top. Remove if using .navbar-static-top. Change if height of navigation changes. */
            }
            td.details-control {
                background: url('<c:url value="/images/details_open.png" />') no-repeat center center;
                cursor: pointer;
            }
            tr.shown td.details-control {
                background: url('<c:url value="/images/details_close.png" />') no-repeat center center;
            }
            table td {
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            table {
                table-layout:fixed;
            }
        </style>
        <script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js" />"></script>
        <script src="<c:url value="/js/jquery-ui-1.10.0.custom.min.js" />"></script>
        <script src="<c:url value="/webjars/datatables/1.10.16/js/jquery.dataTables.min.js" /> "></script>
        <script src="<c:url value="/webjars/jquery-blockui/2.70/jquery.blockUI.js" /> "></script>
        <script src="<c:url value="/webjars/momentjs/2.18.1/moment.js" /> "></script>
        <script src="<c:url value="/js/param.check.js"/>"></script>
        <script src="<c:url value="/js/dataTables.cellEdit.js"/>"></script>
        <script src="<c:url value="/js/select2.min.js"/>"></script>
        <script src="<c:url value="/js/jquery.fileDownload.js" />"></script>

        <script>
            ﻿$(function () {
                var table, subTable;
                var sopPageRegex = /[!@#\$%\^\&*\)\(+=_<>]/gm;
                var standardTimeEditable = '${isAdmin || isMfgOper || isBackDoor4876 || isIeOper}';

                //This function will load the datatable
                LoadTable();
                initSubTable();

                $("#modelName, #sopName").on("keyup change", function () {
                    $(this).val($(this).val().toUpperCase());
                });

                //Hook up the click event for the add/edit customer button
                $("#RemarkInfoPopup").click(function () {
                    //clear form
                    ClearForm();

                    //open modal window
                    $('#addEditRemark').modal('toggle');
                });

                //Hook up the click event for the add/edit customer button
                $("#RemarkDetailPopup").click(function () {
                    //clear form
                    ClearSubForm();

                    //open modal window
                    $('#addEditRemarkDetail').modal('toggle');
                }).attr("disabled", true);

                //Hook up the click event for the save button on the add/edit popup window
                $("#AddRemarkButton").click(function () {
                    //validate that name and last name were entered

                    var modelName = $("#modelName").val();
                    var remark = $("#remark").val();

                    var errorMsg = "";
                    if (!modelName) {
                        errorMsg += "\n* enter the customer first name";
                    }

                    if (errorMsg != "") {
                        errorMsg = "The following errors were found: \n" + errorMsg + "\n\n Please enter the required information and try again.";
                        alert(errorMsg);
                    } else {
                        //JQuery ajax call
                        $.ajax({
                            method: "POST",
                            url: "<c:url value="/ModelSopRemarkController/saveOrUpdate" />",
                            dataType: "html",
                            data: {
                                id: $("#currentID").val(),
                                modelName: modelName,
                                remark: remark
                            },
                            success: function (response) {
                                if (response == "success") {
                                    table.ajax.reload(); //refresh the datatable to reflect the changes    
                                    subTable.clear().draw();
                                    $("#RemarkDetailPopup").attr("disabled", true);
                                    $('#addEditRemark').modal('hide'); //hide the popup window
                                    alert(response);
                                }
                            },
                            error: function (xhr, ajaxOptions, thrownError) {
                                alert(xhr.responseText);
                            }
                        });
                    }
                });

                //Hook up the click event for the save button on the add/edit popup window
                $("#AddRemarkDetailButton").click(function () {
                    //validate that name and last name were entered

                    var station = $("#station").val();
                    var sopName = $("#sopName").val();
                    var sopPage = $("#sopPage").val();
                    var standardTime = $("#standardTime").val();
                    var remark = $("#detailRemark").val();

                    var errorMsg = "";
                    if (!station) {
                        errorMsg += "\n* enter the station";
                    }

                    if (!sopName) {
                        errorMsg += "\n* enter the sopName";
                    }

                    if (!sopPage) {
                        errorMsg += "\n* enter the sopPage";
                    }

                    if (validSopPage(sopPage) == false) {
                        errorMsg += "\n* sopPage 只允許小寫逗號, 分號, 數字1-9, 大小寫P開頭";
                    }

                    if (errorMsg != "") {
                        errorMsg = "The following errors were found: \n" + errorMsg + "\n\n Please enter the required information and try again.";
                        alert(errorMsg);
                    } else {
                        //JQuery ajax call
                        var sopRemarkRow = table.rows('.selected').data()[0];

                        $.ajax({
                            method: "POST",
                            url: "<c:url value="/ModelSopRemarkController/saveOrUpdateDetail" />",
                            dataType: "html",
                            data: {
                                "modelSopRemark.id": sopRemarkRow.id,
                                id: $("#subTableCurrentID").val(),
                                station: station,
                                sopName: sopName,
                                sopPage: sopPage,
                                standardTime: standardTime,
                                remark: remark
                            },
                            success: function (response) {
                                if (response == "success") {
                                    loadSubTable(sopRemarkRow.id); //refresh the datatable to reflect the changes                         
                                    $('#addEditRemarkDetail').modal('hide'); //hide the popup window
                                    alert(response);
                                }
                            },
                            error: function (xhr, ajaxOptions, thrownError) {
                                alert(xhr.responseText);
                            }
                        });
                    }
                });

                //hook up event for edit record buttons
                $(document).on('click', '.EditButton', function (event) { //any element with the class EditButton will be handled here
                    var data = table.row($(this).parents('tr')).data();
                    ClearForm();

                    $("#modelName").val(data.modelName);
                    $("#remark").val(data.remark);
                    $("#currentID").val(data.id);

                    $('#addEditRemark').modal('show');
                });

                //hook up event for edit record buttons
                $(document).on('click', '.EditSubButton', function (event) { //any element with the class EditButton will be handled here
                    var data = subTable.row($(this).parents('tr')).data();
                    ClearSubForm();

                    $("#station").val(data.station);
                    $("#sopName").val(data.sopName);
                    $("#sopPage").val(data.sopPage);
                    $("#standardTime").val(data.standardTime);
                    $("#detailRemark").val(data.remark);
                    $("#subTableCurrentID").val(data.id);

                    $('#addEditRemarkDetail').modal('show');
                });

                //hook up event for edit record buttons
                $(document).on('click', '.EditDetail', function (event) { //any element with the class EditButton will be handled here
                    var data = table.row($(this).parents('tr')).data();
                    loadSubTable(data.id);

                    var parentsTr = $(this).parents("tr");
                    table.$('tr.selected').removeClass('selected');
                    parentsTr.addClass('selected');
                    $("#editModelName").html(data.modelName);
                    $("#RemarkDetailPopup").attr("disabled", false);
                });

                if (standardTimeEditable == 'false') {
                    $("#standardTimeField").hide().attr("disabled", true);
                }

                $("#DownloadData").click(function () {
                    var btn = $(this);
                    $.fileDownload('<c:url value="/ExcelExportController/getAssyModelSopStandardTimeSetting" />', {
                        preparingMessageHtml: "We are preparing your report, please wait...",
                        failMessageHtml: "No reports generated. No Survey data is available.",
                        successCallback: function (url) {
                            btn.attr("disabled", false);
                        }
                        , failCallback: function (html, url) {
                            btn.attr("disabled", false);
                        }
                    });
                });

                function ClearForm() {//blank the add/edit popup form
                    var dialog = $("#addEditRemark");
                    dialog.find(":text, textarea").val("");
                    dialog.find("#currentID").val(0);
                }

                function ClearSubForm() {//blank the add/edit popup form
                    var dialog = $("#addEditRemarkDetail");
                    dialog.find(":text, textarea, input[type='number']").val("");
                    dialog.find("#subTableCurrentID").val(0);
                }

                function LoadTable() {
                    table = $('#remark-info').DataTable({
                        dom: 'Bfrtip',
                        "processing": true,
                        "serverSide": false,
                        "ajax": {
                            "url": "<c:url value="/ModelSopRemarkController/findAll" />",
                            "type": "GET"
                        },
                        "columnDefs": [
                            {//this definition is set so the column with the action buttons is not sortable
                                "targets": -1, //this references the last column of the data
                                "orderable": false
                            },
                            {
                                "width": "45px",
                                "targets": 0
                            },
                            {
                                "width": "150px",
                                "targets": [1, 2]
                            }
                        ],
                        "columns": [
                            {data: "id", title: "id"},
                            {data: "modelName", title: "機種"},
                            {data: "remark", title: "備註"},
                            {
                                "data": "id", 
                                title: "action",
                                "width": "20%",
                                "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
                                    return '<div class="btn-toolbar"><button class="btn btn-sm btn-primary EditButton">Edit</button><button class="btn btn-sm btn-danger DeleteButton">Delete</button></div>';
                                }
                            },
                            {
                                "data": "id", 
                                title: "帶出詳細",
                                "width": "20%",
                                "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
                                    return '<div class="btn-toolbar"><button class="btn btn-sm btn-primary EditDetail">詳細</button></div>';
                                }
                            }
                        ],
                        "displayLength": 10,
                        "pageLength": 10,
                        "lengthChange": true,
                        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
                        "order": [[0, 'desc']]
                    });
                }

                function initSubTable() {
                    subTable = $("#remark-detail").DataTable({
                        dom: 'Bfrtip',
                        "processing": true,
                        "serverSide": false,
                        "columnDefs": [{//this definition is set so the column with the action buttons is not sortable
                                "targets": -1, //this references the last column of the data
                                "orderable": false
                            }],
                        "columns": [
                            {data: "id", title: "id"},
                            {data: "station", title: "station"},
                            {data: "sopName", title: "sopName"},
                            {data: "sopPage", title: "sopPage"},
                            {
                                data: "standardTime",
                                title: "標工",
                                "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
                                    return data == null ? 'n/a' : data;
                                }
                            },
                            {
                                data: "remark",
                                title: "備註",
                                "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
                                    return data == null ? 'n/a' : data;
                                }
                            },
                            {
                                "data": "id",
                                title: "action",
                                "width": "20%",
                                "render": function (data, type, full, meta) { //this column is redefinied to show the action buttons
                                    return '<div class="btn-toolbar"><button class="btn btn-sm btn-primary EditSubButton">Edit</button><button class="btn btn-sm btn-danger DeleteSubButton">Delete</button></div>';
                                }
                            }
                        ]
                    });
                }

                function loadSubTable(rowId) {
                    $.ajax({
                        type: "GET",
                        url: "<c:url value="/ModelSopRemarkController/findDetail" />",
                        dataType: "json",
                        data: {
                            id: rowId
                        },
                        success: function (response) {
                            var d = response.data;
                            subTable.clear().draw();
                            if (d.length) {
                                subTable.rows.add(d).draw();
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            alert(xhr.responseText);
                        }
                    });
                }

                $(document).on('click', '.DeleteButton', function (event) {
                    if (confirm('This action will delete the selected record. Plese click OK to confirm.')) {
                        var data = table.row($(this).parents('tr')).data();
                        //load from database
                        $.ajax({
                            method: "POST",
                            url: "<c:url value="/ModelSopRemarkController/delete" />",
                            dataType: "html",
                            data: {
                                id: data.id
                            },
                            success: function (response) {
                                if (response == "success") {
                                    table.ajax.reload();
                                    subTable.clear().draw();
                                    $("#RemarkDetailPopup").attr("disabled", true);
                                    alert("Record deleted successfully.");
                                }
                            },
                            error: function (xhr, ajaxOptions, thrownError) {
                                alert(xhr.responseText);
                            }
                        });
                    }
                });

                $(document).on('click', '.DeleteSubButton', function (event) {
                    if (confirm('This action will delete the selected record. Plese click OK to confirm.')) {
                        var data = subTable.row($(this).parents('tr')).data();
                        //load from database
                        $.ajax({
                            method: "POST",
                            url: "<c:url value="/ModelSopRemarkController/deleteDetail" />",
                            dataType: "html",
                            data: {
                                id: data.id
                            },
                            success: function (response) {
                                if (response == "success") {
                                    $("#remark-info .selected .EditDetail").trigger("click");
                                    alert("Record deleted successfully.");
                                }
                            },
                            error: function (xhr, ajaxOptions, thrownError) {
                                alert(xhr.responseText);
                            }
                        });
                    }
                });

                function validSopPage(str) {
                    return sopPageRegex.test(str) == false;
                }
            });


        </script>
    </head>
    <body>
        <c:import url="/temp/admin-header.jsp" />
        <div id="wigetCtrl">
            <div>
                <div class="row">
                    <div class="col-lg-6 text-right">
                        <button type="button" class="btn btn-primary btn-lg" id="DownloadData">Download data</button>              
                        <button type="button" class="btn btn-primary btn-lg" id="RemarkInfoPopup">Add Remark</button>              
                    </div>
                    <div class="col-lg-6 text-right">
                        <button type="button" class="btn btn-primary btn-lg" id="RemarkDetailPopup">Add Detail</button>              
                    </div>
                </div>

                <div class="control-label col-sm-6">
                    <div class="alert">
                        <h3>Model Sop 備忘錄</h3>
                    </div>
                    <table id="remark-info" class="table table-bordered display cell-border">
                    </table>
                </div>

                <div class="control-label col-sm-6">
                    <div class="alert alert-warning">
                        <h3>正在編輯<font id="editModelName"></font>的Detail</h3>
                    </div>
                    <table id="remark-detail" class="table table-bordered display cell-border">
                    </table>
                </div>
            </div>

            <!-- add/edit popup window -->
            <div class="modal fade" id="addEditRemark" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">                
                            <h4 class="modal-title" id="myModalLabel">Add/Edit Remark</h4>
                        </div>
                        <div class="modal-body">
                            <fieldset>
                                <!-- Form Name -->
                                <!-- Text input-->
                                <label class="control-label col-sm-3" for="modelName">機種: </label>
                                <div class="input-group col-sm-9">
                                    <input id="modelName" name="modelName" type="text" class="input-xlarge form-control">
                                </div><br />
                                <label class="control-label col-sm-3" for="remark">備註: </label>
                                <div class="input-group col-sm-9">
                                    <textarea id="remark" name="remark" class="input-xlarge form-control"></textarea>
                                </div><br />
                                <input type="hidden" id="currentID" value="" />                         
                            </fieldset>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" id="AddRemarkButton">Save changes</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>    
            <!-- end popup window -->

            <!-- add/edit popup window -->
            <div class="modal fade" id="addEditRemarkDetail" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">                
                            <h4 class="modal-title" id="myModalLabel2">Add/Edit RemarkDetail</h4>
                        </div>
                        <div class="modal-body">
                            <fieldset>
                                <!-- Form Name -->
                                <!-- Text input-->
                                <div>
                                    <label class="control-label col-sm-3" for="station">站別: </label>
                                    <div class="input-group col-sm-9">
                                        <input id="station" name="station" type="number" class="input-xlarge form-control">
                                    </div>
                                </div>
                                <br />
                                <div>
                                    <label class="control-label col-sm-3" for="sopName">SopName: </label>
                                    <div class="input-group col-sm-9">
                                        <input type="text" id="sopName" name="sopName" class="input-xlarge form-control">
                                    </div>
                                </div>
                                <br />
                                <div>
                                    <label class="control-label col-sm-3" for="sopPage">SopPage: </label>
                                    <div class="input-group col-sm-9">
                                        <input id="sopPage" type="text" class="input-xlarge js-example-basic-multiple form-control" >
                                    </div>
                                </div>
                                <br />
                                <div id="standardTimeField">
                                    <label class="control-label col-sm-3" for="standardTime">標工: </label>
                                    <div class="input-group col-sm-9">
                                        <input id="standardTime" type="text" class="input-xlarge form-control" >
                                    </div>
                                </div>
                                <div id="standardTimeField">
                                    <label class="control-label col-sm-3" for="standardTime">備註: </label>
                                    <div class="input-group col-sm-9">
                                        <textarea id="detailRemark" type="text" class="input-xlarge form-control" ></textarea>
                                    </div>
                                </div>
                                <input type="hidden" id="subTableCurrentID" value="" />                         
                            </fieldset>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" id="AddRemarkDetailButton">Save changes</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>    
            <!-- end popup window -->
        </div>
    </body>
</html>
