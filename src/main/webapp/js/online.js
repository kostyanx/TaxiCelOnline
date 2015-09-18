var mskCoord = [55.76, 37.64];
var mskPolygon = [[54.834159,35.816003],[56.649819,39.771122]];
var debuggingEnabled = true;
var searchBounds = mskPolygon;

var internetOrder = {
    state: 0,
    new_order_after_confirm: false,
    order_id: null,
    autoSized: false, // трек машины по карте
    setState: function(newState) {
        switch(newState) {
            case 0:
                receivingEvents(true);
                $('#order-info-inactive')
                    .css({width: $('#order-info').width(),
                          height: $('#order-info').height()})
                    .removeClass('ui-helper-hidden');
                break;
            case 1:
                receivingEvents(false);
                $('#data').data('title').setTitle('Автомобиль назначен');
                $('#data').data('title').blink(true);
                break;
            case 2:
                this.autoSized = false;
                receivingEvents(true);
                $('#car_info').removeClass('ui-helper-hidden');
                $('#data').data('title').blink(false);
                $('#data').data('title').setTitle('Заказ такси онлайн');
                break;
            case 3:
                receivingEvents(true);
                break;
            case 4:
                receivingEvents(true);
                break;
            case 5:
                receivingEvents(false);
                $('#car_info').addClass('ui-helper-hidden');
                $('#bt_neworder').attr('disabled', false);
                $('#order-info-inactive').addClass('ui-helper-hidden');
                $('#bt_cancelorder').attr('disabled', true);
                break;
        }
        this.state = newState;
    }
};

function debug() {
    if (debuggingEnabled && typeof console !== 'undefined' && typeof console.log !== 'undefined') {
        console.log.apply(console, arguments);
    }
}

var myMap;
ymaps.ready(mapInit);
function mapInit() {
    myMap = new ymaps.Map ("map", {
            center: mskCoord, 
            zoom: 10,
            behaviors: ["default", "scrollZoom"]
    });
    myMap.controls.add('mapTools');
    myMap.controls.add('routeEditor');
    myMap.controls.add('typeSelector');
    myMap.controls.add('zoomControl');
    myMap.controls.add('scaleLine');
//    myMap.controls.add('searchControl');
    myMap.controls.add('trafficControl');
//    searchBounds = myMap.getBounds();
//    debug(searchBounds);
    $('#tx_src').yandexsearcher({
        map: myMap,
        dom: 'tx_src_dom',
        korp: 'tx_src_korp',
        str: 'tx_src_str',
        pod: 'tx_src_pod',
        span_dom: 'sn_src_dom',
        setstreet: function(event, ui) { $('#tx_src').removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#tx_src').removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#sn_src_dom input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#bt_calcorder').attr('disabled', $('#tx_dst').yandexsearcher('option', 'address') ? false : true);
            $('#div_route_result').html('');
            var isAirport = ui.address.properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport';
            $('#airport').prop('checked', isAirport);
            $('#ch_options').buttonset('refresh');
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#sn_src_dom input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#bt_calcorder').attr('disabled', true);
            $('#div_route_result').html('');
        }
    });
    $('#tx_dst').yandexsearcher({
        map: myMap,
        dom: 'tx_dst_dom',
        korp: 'tx_dst_korp',
        str: 'tx_dst_str',
        pod: 'tx_dst_pod',
        span_dom: 'sn_dst_dom',
        setstreet: function(event, ui) { $('#tx_dst').removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#tx_dst').removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#sn_dst_dom input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#bt_calcorder').attr('disabled', $('#tx_src').yandexsearcher('option', 'address') ? false : true);
            $('#div_route_result').html('');
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#sn_dst_dom input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#bt_calcorder').attr('disabled', true);
            $('#div_route_result').html('');
        }
    });
    $('#map > ymaps').append('<div id="car_info" class="map_costs car_info ui-helper-hidden"></div>');
}

function custom_alert(title, message)
{
    if (!title)
        title = 'Alert';

    if (!message)
        message = 'No Message to Display.';

    $("<div></div>").html(message).dialog({
        title: title,
        resizable: false,
        modal: true,
        width: 600,
        buttons: {
            "OK": function() 
            {
                $( this ).dialog( "close" );
            }
        }
    });
}

$(function() {
    var client = $('#data').data('client');
    var phone = $('#tx_phone');
    var tx_name = $('#tx_name');
    var bt_req = $('#bt_request');
    if (client) {
        phone.val(client.phone).attr('readonly', true);
        tx_name.val(client.name).attr('readonly', true);
        bt_req.html('Изменить');
    }
    bt_req.bind('click', function(event) {
        if (phone.attr('readonly')) {
            phone.attr('readonly', false);
            tx_name.attr('readonly', false);
            bt_req.html('Отправить смс с кодом');
        } else {
            bt_req.attr('disabled', true);
            $.ajax({
                url: 'ajax/request_code',
                type: 'POST',
                dataType: 'json',
                data: {phone: phone.val(), name: tx_name.val()},
                complete: function () {
                    bt_req.attr('disabled', false);
                },
                success: function (res) {
                    if (res.result && res.result === 'ok') {
                        $('#tx_code').val('');
                        $('#div_dg_confirm').dialog({
                            title: 'Подтверждение телефона',
                            width: 600,
                            modal: true,
                            resizable: false,
                            buttons: {
                                "Закрыть": function() {
                                    $(this).dialog('close');
                                    internetOrder.new_order_after_confirm = false;
                                }
                            }
                        });
                    } else {
                        custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
                    }
                }
            });
        }
    });
    var bt_conf = $('#bt_confirm');
    bt_conf.bind('click', function(event) {
        bt_conf.attr('disabled', true);
        $.ajax({
            url: 'ajax/confirm_phone',
            type: 'POST',
            data: { phone: phone.val(), code: $('#tx_code').val()},
            complete: function() {
                bt_conf.attr('disabled', false);
            },
            success: function(res) {
                if (res.result && res.result === 'ok') {
                    phone.attr('readonly', true);
                    tx_name.attr('readonly', true);
                    bt_req.html('Изменить');
                    $('#div_dg_confirm').dialog('close');
                    $('#data').data('client', res.client);
                    $('#tx_phone').val(res.client.phone);
                    if (internetOrder.new_order_after_confirm) {
                        newOrder();
                        internetOrder.new_order_after_confirm = false;
                    }
                } else {
                    custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
                }
            }
        });
    });
    $('#ta_comment').autosize();
    $('.jqradio').buttonset();
    $('#universal').bind('change', function(event){
        if ($(event.target).is(':checked')) {
            $('#miniven').prop('checked', false);
            $('#ch_options').buttonset('refresh');
        }
    });
    $('#miniven').bind('change', function(event){
        if ($(event.target).is(':checked')) {
            $('#universal').prop('checked', false);
            $('#ch_options').buttonset('refresh');
        }
    });
    $('#rd_probki input, #ch_options input').bind('change', function(){calcOrder();});
    $('#time_nolater,#time_exact').bind('change',function(event){
        if ($('#time_exact').is(':checked')) {
            $('#div_timefield').css('display', 'block').children('input').focus();
            $('#bt_neworder').html('Заказать такси на '+$('#tx_time').val());
        } else {
            $('#div_timefield').css('display', 'none');
            $('#bt_neworder').html('Заказать такси на ближайшее время');
        }
    });
    var dt25 = new Date();
    dt25.setTime(dt25.getTime() + 25 * 60 * 1000);
    var dtsut = new Date();
    dtsut.setTime(dtsut.getTime() + 48 * 60 * 60 * 1000);
    $('#tx_time').datetimepicker({
        dateFormat: 'd M yy,',
        stepMinute: 5,
        minDateTime: dt25,
        maxDateTime: dtsut
    }).datetimepicker('setDate', dt25);
    $('#tx_time').bind('change', function(event) {
        $('#bt_neworder').html('Заказать такси на '+$(event.target).val());
    });
});

function newOrder() {
    if (! $('#tx_phone').attr('readonly') || ! $('#data').data('client')) {
        internetOrder.new_order_after_confirm = true;
        $('#bt_request').trigger('click');
        return;
    }
    var fn = {};
    fn.options = function() {
        var options = [];
        $('#ch_options :checkbox:checked').each(function(ind, el){options.push(el.id);});
        return options;
    };
    fn.nolater = function() {
        return $('#time_nolater').is(':checked');
    };
    fn.time = function() {
        if (fn.nolater()) { return null; }
        var dt = $('#tx_time').datetimepicker('getDate');
        return $.datepicker.formatDate('yy-mm-dd', dt)+' '+dt.toTimeString().split(' ')[0];
    };
    var src = $('#tx_src').yandexsearcher('option', 'addressJson');
    var dst = $('#tx_dst').yandexsearcher('option', 'addressJson');
    if (!src || !dst) {
        custom_alert('Ошибка','Не указан адрес откуда или адрес куда!');
        return;
    }
    var bt_newo = $('#bt_neworder');
    bt_newo.attr('disabled', true);
    $.ajax({
        url: 'ajax/neworder',
        type: 'POST',
        dataType: 'json',
        data: {
            order: $.toJSON({
                address_src: src,
                address_dst: dst,
                nolater: fn.nolater(),
                time: fn.time(),
                client_id: $('#data').data('client').id,
                comment: $('#ta_comment').val(),
                options: fn.options()
            })
        },
        complete: function() {
//            bt_newo.attr('disabled', false);
        },
        success: function(res) {
            if (res.result && res.result === 'ok') {
                if (!fn.nolater()) {
                    custom_alert('Предварительный заказ', 'В течении нескольких минут вам перезвонит оператор и уточнит детали заказа');
                    internetOrder.setState(5);
                    return;
                }
                $('#div_dg_waitcar').dialog({
                    title: 'Назначение автомобиля',
                    modal: true,
                    width: 600,
                    resizable: false,
                    closeOnEscape: false,
                    closeText: "Отменить заказ",
                    buttons: {
                        "Сообщить по телефону": function() {
                            $.ajax({
                                url: 'ajax/call_on_assign',
                                data: {order_id : internetOrder.order_id},
                                complete: function() {
                                    internetOrder.setState(5);
                                    $('#div_dg_waitcar').dialog('close');
                                    custom_alert('Ожидайте звонка', 'В течении 8 минут мы перезвоним Вам и сообщим информацию о назначенном автомобиле');
                                }
                            });
                            
                        },
                        "Отменить заказ": function() {
                            cancelOrder();
                        }
                    },
                    close: function() {
                        if (internetOrder.state === 0) {
                            cancelOrder();
                        }
                    }
                });
                $('#bt_cancelorder').attr('disabled', false);
                internetOrder.order_id = res.order_id;
                internetOrder.setState(0);
            } else {
                custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
                bt_newo.attr('disabled', false);
            }
        },
        error: function() {
            custom_alert('Ошибка', 'Произошла ошибка при создании заказа, попробуйте ещё раз!');
            bt_newo.attr('disabled', false);
        }
    });
}

function cancelOrder() {
    $('#bt_cancelorder').attr('disable', true);
    $.ajax({
        url: 'ajax/cancel_order',
        data: {order_id: internetOrder.order_id},
        type: 'POST',
        dataType: 'json',
        success: function(res) {
            if (res.result && res.result === 'ok') {
                internetOrder.setState(5);
                $('#bt_cancelorder').attr('disabled', true);
                $('#bt_neworder').attr('disabled', false);
                $('#div_route_result').html('');
                $('#div_dg_waitcar').dialog('close');
                custom_alert('Отмена заказа', 'Заказ успешно отменён');
            } else {
                custom_alert('Ошибка', 'Прозиошла ошибка при отмене заказа: '+res.error);
                $('#bt_cancelorder').attr('disabled', false);
            }
        },
        error: function() {
            custom_alert('Ошибка', 'Прозиошла ошибка при отмене заказа');
            $('#bt_cancelorder').attr('disabled', false);
        }
    });
}

var setIntervalId = null;

function receivingEvents(state) {
    if (!state && setIntervalId) {
        clearInterval(setIntervalId);
        setIntervalId = null;
        return;
    }
    if (state && !setIntervalId) {
        var processingEvents = false;
        var processEvents = function(events) {
            debug($.toJSON(events));
            $.each(events, function(index, event) {
                if (event.data.event === 'assigncar') {
                    internetOrder.setState(1);
                    $('#div_dg_waitcar').dialog('close');
                    var car = event.data.car;
                    $('#sn_car').html('К вам подъедет '+car.color+" "+car.mark+" номер "+car.number+" через "+car.minutes+" мин.");
//                    custom_alert("Назначен автомобиль", "На ваш заказ назначен автомобиль: "+car.color+" "+car.mark+" номер "+car.number);
                    $('#div_dg_confirmcar').dialog({
                        title: 'Подтверждение заказа',
                        modal: true,
                        width: 600,
                        resizable: false,
                        closeOnEscape: false,
                        close: function() {
                            if (internetOrder.state === 1) {
                                cancelOrder();
                            }
                        },
                        buttons: {
                            "Подтвердить": function() {
                                $.ajax({
                                    url: 'ajax/confirm_order',
                                    data: {order_id: internetOrder.order_id},
                                    type: 'POST',
                                    dataType: 'json',
                                    success: function(res) {
                                        if (res.result && res.result === 'ok') {
                                            var car = res.car;
//                                            $('#div_route_result').html('<strong style="font-size: 1.2em">К вам подъедет '+car.color+" "+car.mark+" номер "+car.number+" в "+car.time+"</strong>");
                                            $('#car_info').html('К вам подъедет '+car.color+' '+car.mark+' номер '+car.number+' в '+car.time);
                                            internetOrder.setState(2);
                                            $('#div_dg_confirmcar').dialog('close');
                                        } else {
                                            custom_alert("Ошибка", "Произошла ошибка при подтверждении заказа: "+res.error);
                                        }
                                    }
                                });
                            },
                            "Отменить заказ" : function() {
                                $(this).dialog('close');
                            }
                        }
                    });
                }
                if (event.data.event === 'track') {
                    var carp = event.data.car_point;
                    var ordp = event.data.order_point;
                    var carm = (carp === null) ? null :
                        new ymaps.Placemark(carp, {}, { preset: 'twirl#carIcon' });
                    var ordm = (ordp === null) ? null :
                        new ymaps.Placemark(ordp, {}, { preset: 'twirl#houseIcon' });
                    clearObjects(myMap);
                    if (carm !== null) {
                        myMap.geoObjects.add(ordm);
                        myMap.geoObjects.add(carm);
                        if (!internetOrder.autoSized) {
                            myMap.setBounds(calcBounds(ordp, carp));
                            internetOrder.autoSized = true;
                        }
                    } else {
                        myMap.geoObjects.add(ordm);
                        myMap.setCenter(ordp, 16);
                    }
                }
                if (event.data.event === 'state') {
                    internetOrder.setState(event.data.state);
                }
                $.ajax({
                    url: 'ajax/complete_events',
                    data: {events: $.toJSON([event.id])},
                    type: 'POST'
                });
            });
            
        };
        var requestEvents = function() {
            if (processingEvents) { return; }
            processingEvents = true;
            $.ajax({
                url: 'ajax/getevents',
                type: 'POST',
                dataType: 'json',
                success: function(res){
                    if (res.result === 'ok') {
                        processEvents(res.data);
                    }
                    processingEvents = false;
                },
                error: function() {
                    processingEvents = false;
                }
            });
        };
        setIntervalId = setInterval(requestEvents, 1000);
    }
}

function clearObjects(map) {
    map.geoObjects.each(function(obj)
    {myMap.geoObjects.remove(obj); }, this);
}

function calcBounds(src, dst) {
    var min = function(a, b) {
        return (a <= b)? a : b;
    };
    var max = function(a, b) {
        return (a >= b)? a : b;
    };
    var cmin = [min(src[0], dst[0]), min(src[1], dst[1])];
    var cmax = [max(src[0], dst[0]), max(src[1], dst[1])];
    var dx = cmax[0] - cmin[0];
    var dy = cmax[1] - cmin[1];
    cmin[0] = cmin[0] - dx * 0.1;
    cmin[1] = cmin[1] - dy * 0.1;
    cmax[0] = cmax[0] + dx * 0.1;
    cmax[1] = cmax[1] + dy * 0.1;
    var res = [cmin, cmax];
    return res;
}

function calcOrder() {
    if ($('#bt_calcorder').attr('disabled')) { return; }
    
    var src = $('#tx_src').yandexsearcher('option', 'address').geometry.getCoordinates();
    var dst = $('#tx_dst').yandexsearcher('option', 'address').geometry.getCoordinates();
    var fn = {};
    fn.probki = function() {
        return $('#rd_probki :radio:checked').attr('id') === 'probki1';
    };
    fn.options = function() {
        var options = [];
        $('#ch_options :checkbox:checked').each(function(ind, el){options.push(el.id);});
        return options;
    };
    fn.calcDist = function(dist, time, jamstime) {
        if (!fn.probki()) { jamstime = time; }
        if ($('#tx_src').yandexsearcher('option', 'address').properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport'
            || $('#tx_dst').yandexsearcher('option', 'address').properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport') {
                fn.calcDist2(dist, time, jamstime);
                return;
            }
        $.ajax({
            url: 'ajax/calctariff',
            dataType: 'json',
            data: {dist: dist, time: time, jamstime: jamstime, options: fn.options().join(',')},
            success: function(res) {
                var length = Math.round(dist/100.0)/10.0;
                var minutes = Math.round(jamstime/60);
                $('#map_costs').remove();
                var table = '<table>';
                table += '<tr><td>Длина маршрута</td><td colspan=2>'+length+' км</td></tr>';
                table += '<tr><td>Время в пути</td><td colspan=2>'+minutes+' мин</td></tr>';
                table += '<tr><th colspan="3">Расчёт стоимости<span style="color: yellow; font-size: 1.4em; line-height: 70%; font-weight: bolder" >*</span></tr>'
                table += '<tr><th>&nbsp;</th><th>утро-день</th><th>вечер-ночь</th></tr>';
                $.each(res.data,function(ind, el) {
                    table += '<tr><td>'+el.name+'</td><td>'+Math.round(el.day)+' руб.</td><td>'+Math.round(el.night)+' руб.</td></tr>';
                });
                if (res.nacenki && res.nacenki.length > 0) {
                    table += '<tr><th colspan="3">включая наценки:</th></tr>';
                    $.each(res.nacenki, function(ind, el) {
                        table += '<tr><td>'+el.name+'</td><td colspan="2">'+Math.round(el.cost)+' руб.</td></tr>';
                    });
                }
                table += '<tr><td colspan="3"><div style="width: 260px; text-align: left">'
//                          +'* Расчёт является приблизительным. Точная стоимость поездки определяется по таксометру, установленному в автомобиле, в конце поездки'
                        +'* Рас&shy;чёт яв&shy;ля&shy;ет&shy;ся при&shy;бли&shy;зи&shy;тель&shy;ным. '
                        +'Точ&shy;ная сто&shy;и&shy;мость по&shy;езд&shy;ки опре&shy;де&shy;ля&shy;ется по так&shy;со&shy;ме&shy;тру, '
                        +'уста&shy;но&shy;влен&shy;но&shy;му в ав&shy;то&shy;мо&shy;би&shy;ле, в ко&shy;нце по&shy;езд&shy;ки'
                          +'</div></tr>';;
                table += '</table>';
                $('#map > ymaps').append('<div id="map_costs" class="map_costs">'+table+'</div>');
            },
            error: function(err) {
                
            }
        });
    };
    fn.calcDist2 = function(dist, time, jamstime) {
        $.ajax({
            url: 'ajax/calc_tariff_airport',
            dataType: 'json',
            data: {
                order: $.toJSON({
                    address_src: $('#tx_src').yandexsearcher('option', 'addressJson'),
                    address_dst: $('#tx_dst').yandexsearcher('option', 'addressJson')
                }),
                options: fn.options().join(",")
            },
            success: function(res) {
                var length = Math.round(dist/100.0)/10.0;
                var minutes = Math.round(jamstime/60);
                $('#map_costs').remove();
                var table = '<table>';
                table += '<tr><td>Длина маршрута</td><td colspan=2>'+length+' км</td></tr>';
                table += '<tr><td>Время в пути</td><td colspan=2>'+minutes+' мин</td></tr>';
                table += '<tr><th colspan="3">Расчёт стоимости</tr>'; // <span style="color: yellow; font-size: 1.4em; line-height: 70%; font-weight: bolder" >*</span>
                table += '<tr><th>&nbsp;</th><th>утро-день</th><th>вечер-ночь</th></tr>';
                $.each(res.data,function(ind, el) {
                    table += '<tr><td>'+el.name+'</td><td>'+Math.round(el.day)+' руб.</td><td>'+Math.round(el.night)+' руб.</td></tr>';
                });
                if (res.nacenki && res.nacenki.length > 0) {
                    table += '<tr><th colspan="3">включая наценки:</th></tr>';
                    $.each(res.nacenki, function(ind, el) {
                        table += '<tr><td>'+el.name+'</td><td colspan="2">'+Math.round(el.cost)+' руб.</td></tr>';
                    });
                }
                table += '</table>';
                $('#map > ymaps').append('<div id="map_costs" class="map_costs">'+table+'</div>');
            },
            error: function(err) {
                
            }
        });
    };
    ymaps.route([src, dst], {avoidTrafficJams: fn.probki()}).then(
        function (route) {
            clearObjects(myMap);
            myMap.geoObjects.add(route);
            route.editor.start({
                addWayPoints: true});
            route.editor.events.add('routeupdate', function() {
                fn.calcDist(route.getLength(), route.getTime(), route.getJamsTime());
            });
            fn.calcDist(route.getLength(), route.getTime(), route.getJamsTime());
        },
        function (error) {
//            alert('Возникла ошибка: ' + error.message);
        }
    );
    myMap.setBounds(calcBounds(src,dst));
}


$(function(){
    var title = {
        mainWindow: null,
        titleVisible: true,
        intervalId: null,
        currTitle: null,
        origTitle: null,
        setExtTitle: function(title) {
            if (this.mainWindow !== null) {
                this.mainWindow.postMessage({event: 'set_title', value: title}, '*');
            }
        },
        setTitle: function(title) {
            var self = this;
            self.currTitle = title;
            window.document.title = title;
            this.setExtTitle(title);
        },
        blink: function(enable) {
            var self = this;
            var roundTitle = self.currTitle;
            if (enable && !this.intervalId) {
                self.origTitle = self.currTitle;
                this.intervalId = window.setInterval(function(){
                    self.titleVisible = ! self.titleVisible;
                    if (self.titleVisible) {
//                        var value = roundTitle;
//                        var len = value.length;
//                        value = value.substring(1, len) + value.substring(0, 1);
//                        roundTitle = value;
//                        self.setTitle(value);
                        self.setTitle(self.origTitle);
                    } else {
                        self.setTitle('**********************************');
                    }
                }, 500);
            }
            if (!enable && this.intervalId) {
                window.clearInterval(this.intervalId);
                this.intervalId = null;
                self.setTitle(self.origTitle);
            }
        }
    };
    $('#data').data('title', title);
    $(window).bind('message', function(event){
        if (event.originalEvent.data === 'mainWindow') {
            title.mainWindow = event.originalEvent.source;
            $(window).unbind('message');
        }
    });
    $('#div_tabs_src').tabs();
    $('#div_tabs_dst').tabs();
});

function test() {
    $('#data').data('title').setTitle('Автомобиль назначен');
    $('#data').data('title').blink(true);
}

