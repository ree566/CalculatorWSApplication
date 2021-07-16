/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        fadeIn: 0, 
        overlayCSS: {
            backgroundColor: '#FFFFFF',
            opacity: .3
        }
    });
}

$(document).ajaxStart(block).ajaxStop($.unblockUI);


