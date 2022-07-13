var shift_retrieve_url = "";

function generateCookie(name, value) {
    var result = retrieveShiftInfo();
    var shift_endtime = moment(result['SHIFT_END']);
    var cookie_expired_time = shift_endtime.add(10, 'minutes');
    $.cookie(name, value, {expires: cookie_expired_time.toDate()});
}

function retrieveShiftInfo() {
    var result;
    $.ajax({
        type: "GET",
        url: shift_retrieve_url,
        data: {
        },
        dataType: "json",
        async: false,
        success: function (response) {
            result = response;
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(xhr.responseText);
        }
    });
    return result;
}

//shift_retrieve_url = "UserShiftController/findDateShiftInfo";
//var result = retrieveShiftInfo();
//var shift_starttime = moment(result['SHIFT_START']['millis']);
//var shift_endtime = moment(result['SHIFT_END']['millis']);
//var shift_name = result['SHIFT_TYPE'];
//console.log(shift_starttime.format('MMMM Do YYYY, hh:mm:ss a'));
//console.log(shift_endtime.format('MMMM Do YYYY, hh:mm:ss a'));
//console.log(shift_name);