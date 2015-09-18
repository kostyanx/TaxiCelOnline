
function req_operators() {
    $('#bt_ok').button('disable');
    $.ajax({
        url: 'ajax/operators',
        type: 'POST',
        dataType: 'json',
        data: {from: $('#tx_from').val()+':00', to: $('#tx_to').val() + ':00'},
        success: function(res) {
            if (res.result === 'ok') {
                var table = '<table>';
                table += '<tr><td>Оператор</td><td>Всего заказов</td><td>Успешно заказов</td><td>Смен открыто</td></tr>';
                $.each(res.data, function(index, element) {
                    table += '<tr><td>'+element[0]+'</td><td>'+element[1]+
                            '</td><td>'+element[2]+'</td><td>'+element[3]+'</td></tr>';
                });
                table += '</table>';
                $('#div_content').html(table);
            }
        },
        error: function() {
            
        },
        complete: function() {
            $('#bt_ok').button('enable');
        }
    });
}

$(function(){
    $('#bt_ok').button();
    $('#tx_from,#tx_to').datetimepicker({
        dateFormat: 'yy-mm-dd',
        timeFormat: 'HH:mm',
        onSelect: function() {

        },
        onClose: function() {

        }
    });
});

