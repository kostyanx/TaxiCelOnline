if (typeof String.prototype.startsWith !== 'function') {
  // see below for better implementation!
  String.prototype.startsWith = function (str){
    return this.indexOf(str) === 0;
  };
}
(function($) {
    var fn = {};
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
    fn.getChar = function(event) {
        fn.log('debug', 'getChar');
        if (event.keyCode === 8) { return 'backspace'; }
        if (event.which === null) {  // IE
            if (event.keyCode < 32) return null; // спец. символ
            return String.fromCharCode(event.keyCode);
        }
        if (event.which !== 0 && event.charCode !== 0) { // все кроме IE    
            if (event.which < 32) return null; // спец. символ
            return String.fromCharCode(event.which); // остальные
        }
        return null; // спец. символ
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
    fn.searchAddress = function(query, response, searchBounds, dataElement) {
        fn.log('debug', 'searchAddress');
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
                while(autocompleteData.length > 10) {
                    autocompleteData.pop();
                }
                $(dataElement).data('searchResults', autocompleteData);
                var data = [];
                $.each(autocompleteData, function(ind, el) {
                    data.push(el.label);
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
    fn.getStreetName = function(go) {
        fn.log('debug', 'getStreetName');
        var md = go.properties.get('metaDataProperty');
        var country = fn.getVar(md, 'GeocoderMetaData.AddressDetails.Country');
        var sn = fn.getVar(country, 'Locality.Thoroughfare.ThoroughfareName');
        if (!sn) { sn = fn.getVar(country, 'AdministrativeArea.Locality.Thoroughfare.ThoroughfareName'); }
        if (!sn) { sn = fn.getVar(country, 'AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare.ThoroughfareName'); }
        return sn;
    };
    fn.getHouse = function(go) {
        fn.log('debug', 'getStreetName');
        var md = go.properties.get('metaDataProperty');
        var country = fn.getVar(md, 'GeocoderMetaData.AddressDetails.Country');
        var house = fn.getVar(country, 'Locality.Thoroughfare.Premise.PremiseNumber');
        if (!house) { house = fn.getVar(country, 'AdministrativeArea.Locality.Thoroughfare.Premise.PremiseNumber'); }
        if (!house) { house = fn.getVar(country, 'AdministrativeArea.SubAdministrativeArea.Locality.Thoroughfare.Premise.PremiseNumber'); }
        return house;
    };
    fn.searchHouse = function(request, response) {
        fn.log('debug', 'searchHouse');
        var term = request.term;
        if ($.trim(term) === '') { response([]); return; }
        var addr = request.street;
        var street = addr.properties.get('text');
        var streetName = fn.getStreetName(addr);
        fn.log('debug', 'streetName', streetName);
        term = street + ', ' +term;
//        fn.debug('term', term);
        var myGeocoder = ymaps.geocode(term, {results: 10, kind: 'house'});
        myGeocoder.then(
            function (res) {
                var autocompleteData = [];
                res.geoObjects.each(function(obj) {
                    var metaData = obj.properties.get('metaDataProperty');
                    fn.log('debug', metaData);
                    var house = fn.getHouse(obj);
                    if (!house) { return; }
                    var streetCheckName = fn.getStreetName(obj);
                    fn.log('debug', 'streetName', streetName, 'streetCheckName', streetCheckName);
                    if (streetName === streetCheckName) {
                        autocompleteData.push({label: house, value: house, obj: obj});
                    }
                });
                response(autocompleteData);
            },
            function (err) {
                response([]);
            }
        );
    };
    fn.searchJson = function(request, response) {
        fn.log('debug', 'searchJson');
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
    fn.setAddress = function(self, o, address) {
        fn.log('debug', 'setAddress');
        var oldaddress = o.address;
        o.address = address;
        if (address) {
            fn.searchJson({term: address.properties.get('text')}, function(res) {
                o.addressJson = res;
                self._trigger("setaddress", null, {address: o.address, oldaddress: oldaddress});
            });
        } else {
            o.addressJson = null;
            self._trigger("unsetaddress", null, {address: oldaddress});
        }
    };
    fn.setStreet = function(self, o, street) {
        fn.log('debug', 'setStreet');
        var oldstreet = o.street;
        o.street = street;
        if (street) {
            self._trigger("setstreet", null, {street: o.street, oldstreet: oldstreet});
        } else {
            self._trigger("unsetstreet", null, {street: oldstreet});
        }
    };
    $.widget("ui.yandexsearcher", {
        options: {
            dom: null,
            korp: null,
            str: null,
            vlad: null,
            map: null,
            div_house: null,
            address: null,
            street: null,
            addressJson: null,
            searchBounds: null
        },
        _create: function() {
            var self = this,
                o =  self.options,
                el = self.element;
            if (typeof o.div_house === 'string') { o.div_house = $(o.div_house); }
            if (typeof o.dom === 'string') { o.dom = $(o.dom); }
            if (typeof o.korp === 'string') { o.korp = $(o.korp); }
            if (typeof o.str === 'string') { o.str = $(o.str); }
            if (typeof o.vlad === 'string') { o.vlad = $(o.vlad); }
            el.typeahead({
                source: function(query, response) { fn.searchAddress(query, response, o.searchBounds, el); },
                sorter: function(items) { return items; },
                matcher: function(item) { return true; },
                updater: function(item) {
                    var row = null;
                    $.each(el.data('searchResults'), function(ind, r) {
                        if (r.label === item) {
                            row = r;
                        }
                    });
                    if (row !== null) {
                        var obj = row.obj;
                        var metaData = obj.properties.get('metaDataProperty');
                        fn.setStreet(self, o, obj);
                        if (metaData.GeocoderMetaData.kind === 'street') {
                            o.div_house.removeClass('hidden');
                            o.dom.focus();
                            $([o.dom[0], o.korp[0], o.str[0], o.vlad[0]]).val('');
                        } else {
                            fn.setAddress(self, o, obj);
                        }
                        return row.value;
                    }
                    return item;
                },
                items: 10
            }).bind('keydown.yandexsearcher', function(event){
                var char = fn.getChar(event);
                if (char !== '' && char !== null ) {
                    o.div_house.addClass('hidden');
                    if (o.address !== null) {
                        fn.setAddress(self, o, null);
                    }
                    if (o.street !== null) {
                        fn.setStreet(self, o, null);
                    }
                }
            });
//            el.autocomplete({
//                source: fn.searchAddress,
//                select: function(event, ui) {
//                    var obj = ui.item.obj;
//                    var metaData = obj.properties.get('metaDataProperty');
//                    fn.setStreet(self, o, obj);
//                    if (metaData.GeocoderMetaData.kind === 'street') {
//                        o.div_house.css('display', 'block');
//                        o.dom.focus();
//                        $([o.dom[0], o.korp[0], o.str[0], o.pod[0]]).val('');
//                        return;
//                    }
//                    fn.setAddress(self, o, obj);
//                }
//            }).bind('keydown.yandexsearcher', function(event) {
//                var char = fn.getChar(event);
//                if (char !== '' && char !== null ) {
//                    o.div_house.css('display', 'none');
//                    if (o.address !== null) {
//                        fn.setAddress(self, o, null);
//                    }
//                    if (o.street !== null) {
//                        fn.setStreet(self, o, null);
//                    }
//                }
//            });
            
            $([o.dom[0], o.korp[0], o.str[0]]).bind('keydown.yandexsearcher', function(event){
                setTimeout(function(){
                    var dom = $.trim(o.dom.val());
                    var korp = $.trim(o.korp.val());
                    var str = $.trim(o.str.val());
                    var vlad = $.trim(o.vlad.val());
                    if (korp && korp !== '') { dom += 'к'+korp; }
                    if (str && str !== '') { dom += 'c'+str; }
                    if (vlad && vlad !== '') { dom += 'вл'+vlad; }
//                    if ($.trim(request.term) === '') { response([]); }
                    
                    fn.searchHouse({term: dom, street: o.street}, function(res) {
                        if (res && res.length > 0) {
                            fn.setAddress(self, o, res[0].obj);
                        } else {
                            if (o.address !== null) {
                                fn.setAddress(self, o, null);
                            }
                        }
                    });
                },0);
            });
            
        },
        destroy: function() {
            var self = this,
                o =  self.options,
                el = self.element;
            el.unbind('.yandexsearcher');
            $([o.dom[0], o.korp[0], o.str[0]]).unbind('.yandexsearcher');
        }
    });
})(jQuery);



