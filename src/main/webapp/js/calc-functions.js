/*Calculator fields functions*/

$(document).ready(function(){
    $(".calc-direction input[type='checkbox'], .calc-options input[type='checkbox']").each( function() {
        makeCheckbox($(this).parent());
    });
    $(".calc-direction .target-select select").selectBox();
});

function makeCheckbox(obj) {
    elm = obj.find("input");
    if (elm.is(":checked")) { obj.toggleClass("checked"); }
    obj.bind("click", function(event) {
        var checkbox = $(event.target);
        checkbox.toggleClass("checked");
        var checked = checkbox.hasClass('checked');
        checkbox.find('input').attr('checked', checked).prop('checked', checked).trigger('change');
    });
}