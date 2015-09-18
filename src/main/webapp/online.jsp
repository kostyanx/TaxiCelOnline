<%--
    Document   : online
    Created on : 31.08.2013, 16:44:11
    Author     : kostyanx
--%>
<%@page import="taxiinfo.TaxiInfo"%>
<%@page import="taxiinfo.database.JTOLClientElement"%>
<%@page import="kostyanxutil.KostyanxUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Integer clientId = null;
    JTOLClientElement client = null;
    if (request.getCookies() != null) {
        for(Cookie cookie: request.getCookies()) {
            if ("client_id".equals(cookie.getName())) {
                clientId = KostyanxUtil.get().i(cookie.getValue());
                break;
            }
        }
    }
    if (clientId != null) {
        Cookie c = new Cookie("client_id", clientId.toString());
        c.setMaxAge(30 * 24 * 3600); // 30 дней
        c.setPath(request.getContextPath()+"/");
        response.addCookie(c);
        client = new JTOLClientElement(TaxiInfo.get().getTolDb());
        client.getById(clientId);
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <title id="h_title">Заказ такси онлайн</title>
        <link href="css/online.css" rel="stylesheet">
        <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet">
        <link href="css/jquery-ui-timepicker-addon.css" rel="stylesheet">
        <!--<link href="css/css_49.css" rel="stylesheet">-->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/i18n/jquery-ui-i18n.js"></script>
        <script src="js/i18n/jquery.ui.datepicker-ru.js"></script>
        <script src="js/jquery-ui-timepicker-addon.js"></script>
        <script src="js/i18n/jquery-ui-timepicker-ru.js"></script>
        <script src="http://api-maps.yandex.ru/2.0-stable/?load=package.full&lang=ru-RU"></script>
        <script src="js/jquery.ui.yandexsearcher.js"></script>
        <script src="js/jquery.json-2.4.js"></script>
        <script src="js/jquery.autosize.js"></script>
        <script src="js/jquery.taxionline.js"></script>
        <% if (client != null) { %>
        <script>$(function(){
            var data = '<%=client.toJSONString().replaceAll("'", "\\'")%>';
            $('#data').data('client', $.evalJSON(data));
        });</script>
        <% } %>
        <script src="js/online.js"></script>
    </head>
    <body>
        <div id="data" class="display: none"></div>
        <div id="div_dg_confirm" style="display: none">
            На ваш телефон отправлено СМС с кодом подтверждения.
            Введите его в поле &quot;Код&quot; и нажмите кнопку &quot;Подтвердить&quot;<br/>
            Код: <input type="text" id="tx_code" style="width: 60px" />
            <button id="bt_confirm">Подтвердить</button></div>
        <div id="div_dg_waitcar" style="display: none">
            <img src="img/ajax-loader.gif" style="float:left" />
            <strong>Производится поиск автомобиля. Пожалуйста дождитесь назначения автомобиля.</strong><br/>
            <small><i>Если вы хотите, чтобы мы сообщили о назначенном на ваш заказ автомобиле <strong>по телефону</strong>,
                    то нажмите кнопку &quot;Сообщить по телефону&quot;<br/>
                    Вы можете отменить заказ нажав кнопку &quot;Отменить заказ&quot;</i></small>
        </div>
        <div id="div_dg_confirmcar" style="display: none">
            <span id="sn_car"></span><br/>
            Если Вас всё устраивает, нажмите &quot;Подтвердить&quot;<br/>
            <small><i>Вы можете отменить заказ нажав кнопку &quot;Отменить заказ&quot;</i></small>
        </div>
        <div id="order-info" style="overflow: hidden; max-width: 710px">
            <div id="order-info-inactive" class="inactive ui-helper-hidden"></div>
                <div class="param">Телефон<span>*</span></div>
                <div class="value content phone"><input type="text" id="tx_phone" /></div>
                <div class="param name">Имя</div>
                <div class="value content namevalue"><input type="text" id="tx_name" /><button id="bt_request">Отправить смс с кодом</button></div>
                <div class="param">Откуда<span>*</span></div>
                <div id ="div_tabs_src" class="value content" style="font-size: 0.8em">
                    <ul>
                        <li><a href="#div_tabs_src_addr">Адрес</a></li>
                        <li><a href="#div_tabs_src_airport">Аэропорт</a></li>
                        <li><a href="#div_tabs_src_railway">Вокзал</a></li>
                    </ul>
                    <div id="div_tabs_src_addr">
                        <input type="text" id="tx_src" />
                        <div class="form-field house" id="sn_src_dom" style="display: none">
                            дом:<input type="text" id="tx_src_dom" class="house">
                            корпус:<input type="text" id="tx_src_korp" class="house">
                            строение:<input type="text" id="tx_src_str" class="house">
                            подъезд:<input type="text" id="tx_src_pod" class="house">
                        </div>
                    </div>
                    <div id="div_tabs_src_airport">Аэропорт</div>
                    <div id="div_tabs_src_railway">Вокзал</div>
                </div>

                <div class="param">Куда<span>*</span></div>
                <div id ="div_tabs_dst" class="value content" style="font-size: 0.8em">
                    <ul>
                        <li><a href="#div_tabs_dst_addr">Адрес</a></li>
                        <li><a href="#div_tabs_dst_airport">Аэропорт</a></li>
                        <li><a href="#div_tabs_dst_railway">Вокзал</a></li>
                    </ul>
                    <div id="div_tabs_dst_addr">
                        <input type="text" id="tx_dst" />
                        <div class="form-field house" id="sn_dst_dom" style="display: none">
                            дом:<input type="text" id="tx_dst_dom" class="house">
                            корпус:<input type="text" id="tx_dst_korp" class="house">
                            строение:<input type="text" id="tx_dst_str" class="house">
                            подъезд:<input type="text" id="tx_dst_pod" class="house">
                        </div>
                    </div>
                    <div id="div_tabs_dst_airport">Аэропорт</div>
                    <div id="div_tabs_dst_railway">Вокзал</div>
                </div>
                <div class="param">Комментарий</div>
                <div class="value content"><textarea id="ta_comment"></textarea></div>
                <div class="param jqp">Пробки</div>
                <div class="value content jq">
                    <div id="rd_probki" class="jqradio">
                        <input type="radio" id="probki1" name="radio" checked="checked" /><label for="probki1">Учитывать</label>
                        <input type="radio" id="probki2" name="radio" /><label for="probki2">Не учитывать</label>
                    </div>
                </div>
                <div class="param jqp">Наценки</div>
                <div class="value content jq">
                    <div id="ch_options" class="jqradio">
                        <input type="checkbox" id="universal" name="universal" /><label for="universal">Универсал</label>
                        <input type="checkbox" id="miniven" name="miniven" /><label for="miniven">Минивен</label>
                        <input type="checkbox" id="zhivotnie" name="zhivotnie" /><label for="zhivotnie">Перевозка животных</label>
                        <input type="checkbox" id="lyzhi" name="lyzhi" /><label for="lyzhi">Перевозка лыж</label>
                        <input type="checkbox" id="airport" name="airport" /><label for="airport">Встреча в аэропорту</label>
                        <input type="checkbox" id="tablichka" name="tablichka" /><label for="tablichka">Встреча с табличкой</label>
                    </div>
                </div>
                <div class="param jqp">Время</div>
                <div class="value content jq">
                    <div style="float: left; width: 31em; ">
                        <div id="rd_time" class="jqradio">
                            <input type="radio" id="time_nolater" name="time" checked="checked" /><label for="time_nolater">Ближайшее время</label>
                            <input type="radio" id="time_exact" name="time" /><label for="time_exact">Предварительный заказ</label>
                        </div>
                    </div>
                    <div id="div_timefield" style="float: left; display: none"><input type="text" id="tx_time" readonly="readonly" /></div>
                </div>

        </div>
        <!--<br clear="both"/>-->
        <button id="bt_calcorder" style="clear: both; display: none" onclick="calcOrder();" disabled="disabled">Расчет стоимости<span class="red">*</span></button>
        <div style="float: left"><button id="bt_neworder" style="clear: both; width: 340px;" onclick="newOrder();">Заказать такси на ближайшее время</button></div>
        <div style="float: left"><button id="bt_cancelorder" style="clear: both; width: 340px;" onclick="cancelOrder();" disabled="disabled">Отменить заказ</button></div>
<!--        <div style="float: left; clear: both"><button id="bt_test" style="clear: both; width: 340px;" onclick="test();">Тест</button></div>
        -->
        <div id="map" style="clear: both; width: 703px; height: 400px; border: 1px solid #8e8e8e; margin: 5px 0px 0px 0px"></div>
    </body>
</html>
