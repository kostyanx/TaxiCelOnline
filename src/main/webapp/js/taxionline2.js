var kfns;
var data = {};
var myMap;
var debuggingEnabled = true;
var debug = function() {
    if (debuggingEnabled && typeof console !== 'undefined' && typeof console.log !== 'undefined') {
        console.log.apply(console, arguments);
    }
};
$(function(){
    $('#data').data('data', data);
    var mskCoord = [55.76, 37.64];
    var mskPolygon = [[54.834159,35.816003],[56.649819,39.771122]];
    var searchBounds = mskPolygon;
    data.searchBounds = searchBounds;
    var st = { NEW: 0, CREATED: 1, ASSIGNED: 2, DRIVING: 3,
        WAITING: 4, TRANSPORTING: 5, COMPLETE: 6
    };
    var to = {
        state: st.NEW,
        create_order: false,
        order_id: null,
        map_pos: false, // трек машины по карте
        setState: function(newState) {
            switch(newState) {
                case st.NEW:
                    receivingEvents(false);
                    $('#div_neworder').removeClass('hidden');
                    $('#div_cancelorder').addClass('hidden');
                    break;
                case st.CREATED:
                    receivingEvents(true);
                    $('#div_neworder').addClass('hidden');
                    $('#div_cancelorder').removeClass('hidden');
                    break;
                case st.ASSIGNED:
                    receivingEvents(true);
                    $('#div_neworder').addClass('hidden');
                    $('#div_cancelorder').removeClass('hidden');
                    break;
                case st.DRIVING:
                    receivingEvents(true);
                    $('#div_neworder').addClass('hidden');
                    $('#div_cancelorder').removeClass('hidden');
                    break;
                case st.TRANSPORTING:
                    receivingEvents(true);
                    $('#div_neworder').addClass('hidden');
                    $('#div_cancelorder').removeClass('hidden');
                    break;
                case st.COMPLETE:
                    receivingEvents(false);
                    $('#div_neworder').removeClass('hidden');
                    $('#div_cancelorder').addClass('hidden');
                    break;
            }
        }
    };
    data.to = to;
    data.st = st;
    $('#map').css('height', '500px');
    var mapInit = function() {
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
        selectair('dom', 'аэропорт Домодедово', true);
        kfns.selectTrain($('#sl_tr').val());
        $('#map > ymaps').append('<div id="car_info" class="map_costs car_info hidden"></div>');
    };
//    var myMap;
    ymaps.ready(mapInit);
    $('#calc_from').yandexsearcher({
        map: myMap,
        searchBounds: searchBounds,
        div_house: '#div_house_from',
        dom: '#tx_house_from',
        korp: '#tx_korp_from',
        str: '#tx_str_from',
        vlad: '#tx_vlad_from',
        setstreet: function(event, ui) { $('#calc_from').parent().removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#calc_from').parent().removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#div_house_from input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#div_route_result').html('');
            var isAirport = ui.address.properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport';
            $('#calc_airport').prop('checked', isAirport);
            $('#data').data('data').addrFrom = ui.address;
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#div_house_from input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#data').data('data').addrFrom = null;
        }
    });
    $('#calc_to').yandexsearcher({
        map: myMap,
        searchBounds: searchBounds,
        div_house: '#div_house_to',
        dom: '#tx_house_to',
        korp: '#tx_korp_to',
        str: '#tx_str_to',
        vlad: '#tx_vlad_to',
        setstreet: function(event, ui) { $('#calc_to').parent().removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#calc_to').parent().removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#div_house_to input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#data').data('data').addrTo = ui.address;
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#div_house_to input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#data').data('data').addrTo = null;
        }
    });
    $('#calc_air').yandexsearcher({
        map: myMap,
        searchBounds: searchBounds,
        div_house: '#div_house_air',
        dom: '#tx_house_air',
        korp: '#tx_korp_air',
        str: '#tx_str_air',
        vlad: '#tx_vlad_air',
        setstreet: function(event, ui) { $('#calc_air').parent().removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#calc_air').parent().removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#div_house_air input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#data').data('data').airAddr = ui.address;
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#div_house_air input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#data').data('data').airAddr = null;
        }
    });
    $('#calc_tr').yandexsearcher({
        map: myMap,
        searchBounds: searchBounds,
        div_house: '#div_house_tr',
        dom: '#tx_house_tr',
        korp: '#tx_korp_tr',
        str: '#tx_str_tr',
        vlad: '#tx_vlad_tr',
        setstreet: function(event, ui) { $('#calc_tr').parent().removeClass('b-red').addClass('b-green'); },
        unsetstreet: function(event, ui) { $('#calc_tr').parent().removeClass('b-green').addClass('b-red'); },
        setaddress: function(event, ui) {
            $('#div_house_tr input').removeClass('b-red').addClass('b-green');
            if (ui.oldaddress) { myMap.geoObjects.remove(ui.oldaddress); }
            myMap.geoObjects.add(ui.address);
            myMap.setCenter(ui.address.geometry.getCoordinates(), 16);
            $('#data').data('data').trAddr = ui.address;
            calcOrder();
        },
        unsetaddress: function(event, ui) {
            $('#div_house_tr input').removeClass('b-green').addClass('b-red');
            myMap.geoObjects.remove(ui.address);
            $('#data').data('data').trAddr = null;
        }
    });
    
    // автоматический перерасчёт при изменени параметров заказа
    $('#ch_options input').parent().bind('click', function(){calcOrder();});
    $('#calc_universal').bind('change', function(event){
        setTimeout(function(){
        if ($(event.target).is(':checked')) {
            $('#calc_miniwan').attr('checked', false).prop('checked', false).parent().removeClass('checked');
        }},0);
    });
    $('#calc_miniwan').bind('change', function(event){
        setTimeout(function(){
        if ($(event.target).is(':checked')) {
            $('#calc_universal').attr('checked', false).prop('checked', false).parent().removeClass('checked');
        }},0);
    });
    var fn={};
    kfns = fn;
    fn.logLevels = ["trace", "debug", "info", "warn", "error", "crit"];
    fn.lv = {trace: 0, debug: 1, info: 2, warn: 3, error: 4, crit: 5};
    fn.logLevel = fn.lv.debug;
    fn.log = function(level) {
        if (typeof level === 'string') {
            level = fn.logLevels.indexOf(level);
        }
        if (typeof debug === 'function' && level >= fn.logLevel) {
            arguments[0] = level === -1 ? 'unknown' : fn.logLevels[level];
            debug.apply(null, arguments);
        }
    };
    fn.debug = function() {
        arguments.unshift('debug');
        fn.log.apply(null, arguments);
    };
    fn.searchFirst = function(query, response, searchBounds) {
        fn.log('debug', 'searchFirst');
//        var term = request.term;
        var term = query;
        var myGeocoder = ymaps.geocode(term, {results: 20, kind: 'street', boundedBy: searchBounds, strictBounds: true});
        myGeocoder.then(
            function (res) {
                var autocompleteData = [];
                res.geoObjects.each(function(obj) {
                    var name = obj.properties.get('text');
                    if (name.startsWith('Россия, ')) { name = name.substring(8); }
//                    if (name.startsWith('Московская область, ')) { name = name.substring(19); }
//                    if (name.startsWith('Москва, ')) { name = name.substring(8); }
                    var name2 = name;
                    var namearr = [];
                    $.each(name.split(','), function(index, str){
                        namearr.unshift($.trim(str));
                    });
                    var label = namearr.join(', ');
                    autocompleteData.push({label: label, value: name2, obj: obj});
                    
                });
                autocompleteData.sort(function(a, b) {
                    var la = fn.getLocalityName(a.obj);
                    var lb = fn.getLocalityName(b.obj);
                    if (la === 'Москва') {
                        return (lb === 'Москва') ? 0 : -1;
                    } else {
                        return (lb === 'Москва') ? 1 : 0;
                    }
                });
                response(autocompleteData.shift());
            },
            function (err) {
                response([]);
            }
        );
    };
    fn.searchAllMatched = function(query, response, searchBounds) {
        fn.log('debug', 'searchAllMatched');
//        var term = request.term;
        var term = query;
        var myGeocoder = ymaps.geocode(term, {results: 20, kind: 'street', boundedBy: searchBounds, strictBounds: true});
        myGeocoder.then(
            function (res) {
                var autocompleteData = [];
                res.geoObjects.each(function(obj) {
                    var name = obj.properties.get('text');
                    if (name.startsWith('Россия, ')) { name = name.substring(8); }
//                    if (name.startsWith('Московская область, ')) { name = name.substring(19); }
//                    if (name.startsWith('Москва, ')) { name = name.substring(8); }
                    var name2 = name;
                    var namearr = [];
                    $.each(name.split(','), function(index, str){
                        namearr.unshift($.trim(str));
                    });
                    var label = namearr.join(', ');
                    autocompleteData.push({label: label, value: name2, obj: obj});
                    
                });
                var ss = function(s1, s2) {
                    s1 = s1.toLowerCase();
                    s2 = s2.toLowerCase();
                    if (s1 < s2) { return -1; }
                    if (s1 > s2) { return 1; }
                    return 0;
                };
                autocompleteData.sort(function(a, b) {
                    var la = fn.getLocalityName(a.obj);
                    var lb = fn.getLocalityName(b.obj);
                    if (la === 'Москва') {
                        return (lb === 'Москва') ? ss(a.value, b.value) : -1;
                    } else {
                        return (lb === 'Москва') ? 1 : ss(a.value, b.value);
                    }
                });
                var data = [];
                $.each(autocompleteData, function(ind, el){
                    if (el.value.toLowerCase().indexOf(query.toLowerCase()) !== -1) {
                        data.push(el);
                    }
                });
                response(data);
            },
            function (err) {
                response([]);
            }
        );
    };
    fn.getLocalityName = function(go) {
        var md = go.properties.get('metaDataProperty');
        var country = fn.getVar(md, 'GeocoderMetaData.AddressDetails.Country');
        var ln = fn.getVar(country, 'Locality.LocalityName');
        if (!ln) { ln = fn.getVar(country, 'AdministrativeArea.Locality.LocalityName'); }
        if (!ln) { ln = fn.getVar(country, 'AdministrativeArea.SubAdministrativeArea.Locality.LocalityName'); }
        return ln;
    };
    fn.getVar = function(varr, name) {
        if (!varr) { return varr; }
        var varName = 'varr';
        var res = null;
        $.each(name.split('.'), function(index, value) {
            varName += '.'+value;
            if (res === null || res) {
                res = eval('('+varName+')');
            }
        });
        return res;
    };
    fn.selectTrain = function(search) {
        fn.searchFirst(search, function(res){
//            console.log(res);
            $('#sl_tr').data('address', res.obj);
            fn.searchJson({term: res.obj.properties.get('text')}, function(res) {
                $('#sl_tr').data('addressJson', res);
                calcOrder();
            });
        }, searchBounds);
    };
    fn.searchJson = function(request, response) {
//        console.log('searchJson');
        var term = request.term;
//        fn.debug('term-test', term);
        var myGeocoder = ymaps.geocode(term, {results: 10, kind: 'house', json: true, searchCoordOrder: 'latlong'});
        myGeocoder.then(
            function (res) {
                var result = null;
//                fn.debug('res-test', res, $.toJSON(res));
                var results = fn.getVar(res,"GeoObjectCollection.featureMember");
                if (results) {
                    $.each(results, function(ind, el){
                        if (el.GeoObject.metaDataProperty.GeocoderMetaData.text === term) {
                            result = el;
                        }
                    });
                }
//                fn.debug(result);
                response(result);
            },
            function (err) {
                response([]);
            }
        );
    };
    fn.activeTab = function() {
        return $('#calc-tabs').find('li.active').attr('id').substring(3);
    };
    fn.addr = function(tab, from) {
        switch(tab) {
            case 'city':
                return from ? $('#data').data('data').addrFrom : $('#data').data('data').addrTo;
                break;
            case 'airport':
                if ( (from && $("#calc_is_to_air").is(':checked')) || (!from && !$("#calc_is_to_air").is(':checked')) ) {
                    return $('#data').data('data').airAddr;
                } else {
                    return $('#rd_airport').data('address');
                }
                break;
            case 'train_station':
                if ( (from && $("#calc_is_to_tr").is(':checked')) || (!from && !$("#calc_is_to_tr").is(':checked')) ) {
                    return $('#data').data('data').trAddr;
                } else {
                    return $('#sl_tr').data('address');
                }
                break;
        }
    };
    fn.addrJson = function(tab, from) {
        switch(tab) {
            case 'city':
                return from ? $('#calc_from').yandexsearcher('option', 'addressJson') : $('#calc_to').yandexsearcher('option', 'addressJson');
                break;
            case 'airport':
                if ( (from && $("#calc_is_to_air").is(':checked')) || (!from && !$("#calc_is_to_air").is(':checked')) ) {
                    return $('#calc_air').yandexsearcher('option', 'addressJson');
                } else {
                    return $('#rd_airport').data('addressJson');
                }
                break;
            case 'train_station':
                if ( (from && $("#calc_is_to_tr").is(':checked')) || (!from && !$("#calc_is_to_tr").is(':checked')) ) {
                    return $('#calc_air').yandexsearcher('option', 'addressJson');
                } else {
                    return $('#sl_tr').data('addressJson');
                }
                break;
        }
    };
    fn.closeOrderWindow = function() {
        $('#msg-neworder').floatwin('hide');
    };
    fn.openOrderForm = function() {
        fn.setOrderDate();
        var addrFrom = fn.addr(fn.activeTab(), true);
        var addrTo = fn.addr(fn.activeTab(), false);
        $('#from-info').html(addrFrom.properties.get('name'));
        $('#to-info').html(addrTo.properties.get('name'));
        $('#msg-neworder').floatwin('show');
    };
    fn.createOrder = function() {
        newOrder();
    };
    fn.requestCode = function() {
        $.ajax({
            url: 'ajax/request_code',
            type: 'POST',
            dataType: 'json',
            data: {phone: $('#msg-phone').val(), name: $('#msg-name').val()},
            complete: function () {

            },
            success: function (res) {
                if (res.result && res.result === 'ok') {
                    $('#tx_confirm_code').val('');
                    $('#sn_client_name').html($('#msg-name').val());
                    $('#b_phone').html('+7'+$('#msg-phone').val());
                    $('#msg-confirm-phone').floatwin('show');
                } else {
                    custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
                }
            },
            error: function() {
                custom_alert('Ошибка', 'Произошла ошибка');
            }
        });
    };
    fn.confirmPhone = function() {
        phone=$('#msg-phone');
        $.ajax({
            url: 'ajax/confirm_phone',
            type: 'POST',
            data: { phone: phone.val(), code: $('#tx_confirm_code').val()},
            complete: function() {
                
            },
            success: function(res) {
                if (res.result && res.result === 'ok') {
                    $('#data').data('client', res.client);
                    $('#msg-phone').confirmphone('confirm');
                    $('#msg-confirm-phone').floatwin('close');
                    newOrder();
                } else {
                    custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
                }
            },
            error: function(res) {
                custom_alert('Ошибка', 'Произошла ошибка');
            }
        });
        
    };
    fn.setOrderDate = function() {
        var opts = {};
        var dt = new Date();
        dt.setTime(dt.getTime() + 10 * 60 * 1000);
        var minutes = Math.round((dt.getMinutes() + 3) / 5.0) * 5;
        dt.setMinutes(minutes);
        var fdt = $.format.date(dt, "dd MMMM").toLowerCase();
        var ff = $.format.date(dt,  "yyyy-MM-dd");
        $('#msg-date').html('');
        opts[ff] = fdt;
        $('#msg-date').append($('<option></option>').html(fdt).attr("value", ff));
        dt.setTime(dt.getTime() + 24 * 3600 * 1000);
        fdt = $.format.date(dt, "dd MMMM").toLowerCase();
        var ff = $.format.date(dt,  "yyyy-MM-dd");
        opts[ff] = fdt;
        $('#msg-date').append($('<option></option>').html(fdt).attr("value", ff));
        $('#msg-date').selectBox('options', opts);
        $('#msg-hour').selectBox('value', $.format.date(dt, 'HH'));
        $('#msg-minutes').selectBox('value', $.format.date(dt, 'mm'));
    };
    $('#calc_is_to_air,#calc_is_to_tr').bind('change', function(event){
        var checkbox = $(event.target);
        var checked = checkbox.is(':checked');
        var titleElement = checkbox.parent().siblings('.bottom-title');
        titleElement.html(checked ? 'Откуда' : 'Куда');
        calcOrder();
    });
    var ts = $('#sl_tr');
    ts.bind('change', function(event) {
//        console.log(ts.val());
        fn.selectTrain(ts.val());
    });
    
    // ##### WINDOWS #####
    $('.msg-overlay').floatwin({});
    // ##### /WINDOWS #####
    
    $('#msg-phone').confirmphone({name: '#msg-name', data: '#data', confirmwin: '#msg-confirm-phone'});
    // обработка информации о клиенте, если мы узнали клиента по кукам
    var client = $('#data').data('client');
    var phone = $('#msg-phone');
    var tx_name = $('#msg-name');
    if (client) {
        phone.val(client.phone.substring(1));
        tx_name.val(client.name);
    }
    $('#bt_cancelorder').bind('click', function() {
        cancelOrder();
        $('#msg-wait').floatwin('hide');
    });
    $('#bt_call_on_assign').bind('click', function(){
        var data = $('#data').data('data');
        $.ajax({
            url: 'ajax/call_on_assign',
            data: {order_id : data.order_id},
            complete: function() {
                to.setState(6);
                $('#msg-wait').floatwin('hide');
                custom_alert('Ожидайте звонка', 'В течении 8 минут мы перезвоним Вам и сообщим информацию о назначенном автомобиле');
            }
        });
        
    });
    $('#bt_cancelorder2').bind('click', function(){
        cancelOrder();
        $('#msg-car').floatwin('hide');
    });
    $('#bt_confirm').bind('click', function(){
        var data = $('#data').data('data');
        $.ajax({
            url: 'ajax/confirm_order',
            data: {order_id: data.order_id},
            type: 'POST',
            dataType: 'json',
            success: function(res) {
                if (res.result && res.result === 'ok') {
                    var car = res.car;
//                                            $('#div_route_result').html('<strong style="font-size: 1.2em">К вам подъедет '+car.color+" "+car.mark+" номер "+car.number+" в "+car.time+"</strong>");
                    $('#car_info').html('К вам подъедет '+car.color+' '+car.mark+' номер '+car.number+' в '+car.time);
                    data.to.setState(3);
                    $('#msg-car').floatwin('close');
                } else {
                    custom_alert("Ошибка", "Произошла ошибка при подтверждении заказа: "+res.error);
                }
            }
        });
    });
    
});



function calcWithProbki() {
    $('#calc_probki').attr('checked', true).prop('checked', true);
    calcOrder();
}

function calcWithoutProbki() {
    $('#calc_probki').attr('checked', false).prop('checked', false);
    calcOrder();
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
    
    var fn = {};
    
    fn.probki = function() {
        return $('#calc_probki').is(':checked');
    };
    fn.options = function() {
        var options = [];
        $('#ch_options :checkbox:checked').each(function(ind, el){options.push(el.id.substring(5));});
        return options;
    };
    fn.fillInfo = function(info) {
        $('#cost_dt_day,#cost_dt_day2').html(info.day_start);
        $('#cost_dt_night,#cost_dt_night2').html(info.night_start);
        $('#cost_day').html(info.cost_day);
        $('#cost_night').html(info.cost_night);
        $('#dist_km').html(info.dist);
        $('#dist_time').html(info.time);
    };
    fn.calcDist = function(aFrom, aTo, aFromJson, aToJson, dist, time, jamstime) {
        if (!fn.probki()) { jamstime = time; }
        if (aFrom.properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport'
            || aTo.properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport') {
                fn.calcDist2(aFrom, aTo, aFromJson, aToJson, dist, time, jamstime);
                return;
            }
        var options = fn.options();
        var fromRailway = aFrom.properties.get('metaDataProperty').GeocoderMetaData.kind === 'railway';
        if (fromRailway && options.indexOf('railway') === -1) { options.push('railway'); }
        $.ajax({
            url: 'ajax/calctariff',
            dataType: 'json',
            data: {dist: dist, time: time, jamstime: jamstime, options: options.join(',')},
            success: function(res) {
                var length = Math.round(dist/100.0)/10.0;
                var minutes = Math.round(jamstime/60);
                $('#map_costs').remove();
                var table = '<table>';
                table += '<tr><td>Длина маршрута</td><td colspan=2>'+length+' км</td></tr>';
                table += '<tr><td>Время в пути</td><td colspan=2>'+minutes+' мин</td></tr>';
                table += '<tr><th colspan="3">Расчёт стоимости<span style="color: yellow; font-size: 1.4em; line-height: 70%; font-weight: bolder" >*</span></tr>';
                table += '<tr><th>&nbsp;</th><th>утро-день</th><th>вечер-ночь</th></tr>';
                var day = null, night = null;
                $.each(res.data,function(ind, el) {
                    if (day === null) { day = Math.round(el.day); }
                    if (night === null) { night = Math.round(el.night); }
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
                          +'</div></tr>';
                table += '</table>';
                $('#map > ymaps').append('<div id="map_costs" class="map_costs">'+table+'</div>');
                $('#div_dist_info').removeClass('hidden');
                $('#div_cost_info').removeClass('hidden');
                fn.fillInfo({day_start: res.daystart, night_start: res.nightstart, cost_day: day, cost_night: night, dist: length, time: minutes });
            },
            error: function(err) {
                
            }
        });
    };
    fn.calcDist2 = function(aFrom, aTo, aFromJson, aToJson, dist, time, jamstime) {
        var options = fn.options();
//        console.log(aFrom.properties.get('metaDataProperty'));
        var fromAirport = aFrom.properties.get('metaDataProperty').GeocoderMetaData.kind === 'airport';
        if (fromAirport && options.indexOf('airport') === -1) { options.push('airport'); }
        $.ajax({
            url: 'ajax/calc_tariff_airport',
            dataType: 'json',
            data: {
                order: $.toJSON({
                    address_src: aFromJson,
                    address_dst: aToJson
                }),
                options: options.join(",")
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
                var day = null, night = null;
                $.each(res.data,function(ind, el) {
                    if (day === null) { day = Math.round(el.day); }
                    if (night === null) { night = Math.round(el.night); }
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
                $('#div_dist_info').removeClass('hidden');
                $('#div_cost_info').removeClass('hidden');
                fn.fillInfo({day_start: res.daystart, night_start: res.nightstart, cost_day: day, cost_night: night, dist: length, time: minutes });
            },
            error: function(err) {
                
            }
        });
    };
    var aFrom = kfns.addr(kfns.activeTab(), true);
    var aTo = kfns.addr(kfns.activeTab(), false);
    var aFromJson = kfns.addrJson(kfns.activeTab(), true);
    var aToJson = kfns.addrJson(kfns.activeTab(), false);
    if (!aFrom || !aTo) { return; }
    
    var src = aFrom.geometry.getCoordinates();
    var dst = aTo.geometry.getCoordinates();
    
    ymaps.route([src, dst], {avoidTrafficJams: fn.probki()}).then(
        function (route) {
            clearObjects(myMap);
            myMap.geoObjects.add(route);
            route.editor.start({
                addWayPoints: true});
            route.editor.events.add('routeupdate', function() {
                fn.calcDist(aFrom, aTo, aFromJson, aToJson, route.getLength(), route.getTime(), route.getJamsTime());
            });
            fn.calcDist(aFrom, aTo, aFromJson, aToJson, route.getLength(), route.getTime(), route.getJamsTime());
        },
        function (error) {
//            alert('Возникла ошибка: ' + error.message);
        }
    );
    myMap.setBounds(calcBounds(src,dst));
}

function custom_alert(title, message)
{
    var wnd = $('#msg-info').clone().attr('id', null);
    $(document.body).append(wnd);
    wnd.floatwin({title: title, content: message});
    wnd.bind('floatwinclose', function() { wnd.remove(); });
    wnd.floatwin('show');
}

function newOrder() {
    if ($('#msg-phone').confirmphone('requireConfirm')) {
        kfns.requestCode();
//        $('#msg-confirm-phone').floatwin('show');
        return;
    }
    var aFrom = kfns.addr(kfns.activeTab(), true);
    var aTo = kfns.addr(kfns.activeTab(), false);
    var aFromJson = kfns.addrJson(kfns.activeTab(), true);
    var aToJson = kfns.addrJson(kfns.activeTab(), false);
    if (!aFrom || !aTo) { return; }
//    return;
//    if (! $('#tx_phone').attr('readonly') || ! $('#data').data('client')) {
//        internetOrder.new_order_after_confirm = true;
//        $('#bt_request').trigger('click');
//        return;
//    }
    var fn = {};
    fn.options = function() {
        var options = [];
        $('#ch_options :checkbox:checked').each(function(ind, el){options.push(el.id.substring(5));});
        if (options.indexOf('probki') !== -1) {
            options.splice(options.indexOf('probki'), 1);
        }
        return options;
    };
    fn.nolater = function() {
        var dtStr = $('#msg-date').selectBox('value')+' '+$('#msg-hour').selectBox('value')+':'+$('#msg-minutes').selectBox('value')+':00';
        var dt = new Date(dtStr);
        var curr = new Date();
        return dt.getTime() - curr.getTime() <= 25 * 60 * 1000;
//        return $('#time_nolater').is(':checked');
    };
    fn.time = function() {
//        if (2 > 1) { return null; }
//        if (fn.nolater()) { return null; }
//        var dt = $('#tx_time').datetimepicker('getDate');
//        return $.datepicker.formatDate('yy-mm-dd', dt)+' '+dt.toTimeString().split(' ')[0];
        return $('#msg-date').selectBox('value')+' '+$('#msg-hour').selectBox('value')+':'+$('#msg-minutes').selectBox('value')+':00';
    };
    
    var src = aFromJson;
    var dst = aToJson;
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
                comment: '',//$('#ta_comment').val(),
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
                    var data = $('#data').data('data');
                    data.to.setState(6);
                    $('#msg-neworder').floatwin('close');
                    return;
                }
                $('#msg-neworder').floatwin('close');
                //custom_alert('', 'Заказ успешно создан!');
                receivingEvents(true);
                $('#msg-wait').floatwin('show');
//                $('#div_dg_waitcar').dialog({
//                    title: 'Назначение автомобиля',
//                    modal: true,
//                    width: 600,
//                    resizable: false,
//                    closeOnEscape: false,
//                    closeText: "Отменить заказ",
//                    buttons: {
//                        "Сообщить по телефону": function() {
//                            $.ajax({
//                                url: 'ajax/call_on_assign',
//                                data: {order_id : internetOrder.order_id},
//                                complete: function() {
////                                    internetOrder.setState(5);
//                                    $('#div_dg_waitcar').dialog('close');
//                                    custom_alert('Ожидайте звонка', 'В течении 8 минут мы перезвоним Вам и сообщим информацию о назначенном автомобиле');
//                                }
//                            });
//                            
//                        },
//                        "Отменить заказ": function() {
//                            cancelOrder();
//                        }
//                    },
//                    close: function() {
//                        if (internetOrder.state === 0) {
//                            cancelOrder();
//                        }
//                    }
//                });
//                $('#bt_cancelorder').attr('disabled', false);
                var data = $('#data').data('data');
                data.order_id = res.order_id;
                data.to.setState(data.st.CREATED);
            } else {
                custom_alert('Ошибка', 'Произошла ошибка: '+res.error);
//                bt_newo.attr('disabled', false);
            }
        },
        error: function() {
            custom_alert('Ошибка', 'Произошла ошибка при создании заказа, попробуйте ещё раз!');
//            bt_newo.attr('disabled', false);
        }
    });
}

function assignCar(event) {
    //
    var data = $('#data').data('data');
    var car = event.data.car;
    data.to.setState(2);
    $('#msg-wait').floatwin('close');
    $('#sn_car').html('К вам подъедет '+car.color+" "+car.mark+" номер "+car.number+" через "+car.minutes+" мин.");
    $('#msg-car').floatwin('show');
    //custom_alert("Назначен автомобиль", "На ваш заказ назначен автомобиль: "+car.color+" "+car.mark+" номер "+car.number);
//    $('#div_dg_confirmcar').dialog({
//        title: 'Подтверждение заказа',
//        modal: true,
//        width: 600,
//        resizable: false,
//        closeOnEscape: false,
//        close: function() {
//            if (internetOrder.state === 1) {
//                cancelOrder();
//            }
//        },
//        buttons: {
//            "Подтвердить": function() {
//                $.ajax({
//                    url: 'ajax/confirm_order',
//                    data: {order_id: internetOrder.order_id},
//                    type: 'POST',
//                    dataType: 'json',
//                    success: function(res) {
//                        if (res.result && res.result === 'ok') {
//                            var car = res.car;
////                                            $('#div_route_result').html('<strong style="font-size: 1.2em">К вам подъедет '+car.color+" "+car.mark+" номер "+car.number+" в "+car.time+"</strong>");
//                            $('#car_info').html('К вам подъедет '+car.color+' '+car.mark+' номер '+car.number+' в '+car.time);
//                            internetOrder.setState(2);
//                            $('#div_dg_confirmcar').dialog('close');
//                        } else {
//                            custom_alert("Ошибка", "Произошла ошибка при подтверждении заказа: "+res.error);
//                        }
//                    }
//                });
//            },
//            "Отменить заказ" : function() {
//                $(this).dialog('close');
//            }
//        }
//    });
    
}

function track(event) {
    var data = $('#data').data('data');
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
        if (!data.autoSized) {
            myMap.setBounds(calcBounds(ordp, carp));
            data.autoSized = true;
        }
    } else {
        myMap.geoObjects.add(ordm);
        myMap.setCenter(ordp, 16);
    }
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
                    assignCar(event);
                }
                if (event.data.event === 'track') {
                    track(event);
                }
                if (event.data.event === 'state') {
                    $('#data').data('data').to.setState(event.data.state);
                    
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

function cancelOrder() {
//    $('#bt_cancelorder').attr('disable', true);
    var data = $('#data').data('data');
    $.ajax({
        url: 'ajax/cancel_order',
        data: {order_id: data.order_id},
        type: 'POST',
        dataType: 'json',
        success: function(res) {
            if (res.result && res.result === 'ok') {
                data.to.setState(6);
//                $('#bt_cancelorder').attr('disabled', true);
//                $('#bt_neworder').attr('disabled', false);
//                $('#div_route_result').html('');
//                $('#div_dg_waitcar').dialog('close');
                custom_alert('Отмена заказа', 'Заказ успешно отменён');
            } else {
                custom_alert('Ошибка', 'Прозиошла ошибка при отмене заказа: '+res.error);
//                $('#bt_cancelorder').attr('disabled', false);
            }
        },
        error: function() {
            custom_alert('Ошибка', 'Прозиошла ошибка при отмене заказа');
//            $('#bt_cancelorder').attr('disabled', false);
        }
    });
}

function selecttab(tab) {
    $('.calc-tabs li.active').removeClass('active');
    $('#li_'+tab).addClass('active');
    $('.calc-direction:not(.hidden)').addClass('hidden');
    $('#div_'+tab).removeClass('hidden');
    calcOrder();
}

function selectair(air, search, selectFirst) {
    if (air === 'sher') {
        if ($('#rd_airport li.aterm.active').length === 0) {
            setterm('B');
        }
        search = search + ' ' + $('#rd_airport li.aterm.active a span').html();
    } else {
        setterm(null);
    }
    $('#rd_airport li.active:not(.aterm)').removeClass('active');
    var li = $('#li_'+air);
    li.addClass('active');
    if (selectFirst) {
        kfns.searchFirst(search, function(res){
//           console.log(res);
           $('#rd_airport').data('address', res.obj);
           kfns.searchJson({term: res.obj.properties.get('text')}, function(res) {
               $('#rd_airport').data('addressJson', res);
               calcOrder();
           });
        }, data.searchBounds);
    } else {
        kfns.searchAllMatched(search, function(res){
//           console.log(res);
           $('#rd_airport').data('address', res[0].obj);
           kfns.searchJson({term: res[0].obj.properties.get('text')}, function(res) {
               $('#rd_airport').data('addressJson', res);
               calcOrder();
           });
           
        }, data.searchBounds);
    }
    
}

function setterm(term) {
    $('#rd_airport li.aterm.active').removeClass('active');
    $('#rd_airport li.aterm').each(function(ind, el){
        el = $(el);
        if (el.find('a span').html() === term) {
            el.addClass('active');
        }
    });
    if (term !== null) {
        selectair('sher', 'Шереметьево терминал', false);
    }
}
