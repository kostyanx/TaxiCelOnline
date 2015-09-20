<%--
    Document   : online
    Created on : 31.08.2013, 16:44:11
    Author     : kostyanx
--%>
<%@page import="ru.kostyanx.taxicelonline.TaxiInfo"%>
<%@page import="ru.kostyanx.utils.KostyanxUtil"%>
<%@page import="ru.kostyanx.taxicelonline.database.JTOLClientElement"%>
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
<meta charset="utf-8">
<title>Такси Цель</title>
<link rel="stylesheet" href="css/flexslider.css">
<link rel="stylesheet" href="css/highslide.css">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dropdown_styles.css">
<!--<link rel="stylesheet" href="css/bootstrap.css">-->
<!--<link rel="stylesheet" href="css/bootstrap-theme.css">-->
<link rel="stylesheet" href="css/my-bootstrap-typeahead.css">
<!--[if lt IE 8]><link rel="stylesheet" href="css/old-ie.css"><![endif]-->
<link rel="stylesheet" href="css/online2.css">
<script src="js/html5.js"></script>
<script src="js/jquery-2.0.3.js"></script>
<script src="js/jquery.selectBox.js"></script>
<script src="js/jquery.flexslider-min.js"></script>
<script src="js/highslide-full.js"></script>
<!--[if lt IE 9]><script src="js/pie.js"></script><![endif]-->
<!-- custom -->
<script src="js/bootstrap.js"></script>
<script src="js/bootstrap3-typeahead.js"></script>
<!-- /custom -->
<script src="js/functions.js"></script>
<script src="js/calc-functions.js"></script>
<script src="js/foat-functions.js"></script>

<script src="js/jquery.json-2.4.js"></script>
<script src="js/jquery-dateFormat.js"></script>
<script src="http://api-maps.yandex.ru/2.0-stable/?load=package.full&lang=ru-RU"></script>
<script src="js/jquery-ui-1.10.3.custom-widget-only.js"></script>
<script src="js/jquery.ui.yandexsearcher.js"></script>
<script src="js/jquery.ui.floatwin.js"></script>
<script src="js/jquery.ui.confirmphone.js"></script>
<% if (client != null) { %>
<script>$(function(){
    var data = '<%=client.toJSONString().replaceAll("'", "\\'")%>';
    $('#data').data('client', $.evalJSON(data));
});</script>
<% } %>
<!--<script src="js/taxionline.js"></script>-->
<script src="js/taxionline2.js"></script>
</head>

<body id="inner-page">

<!--WINDOWS-->
<div id="msg-neworder" class="msg-overlay hidden">
    <div class="float-block"><div class="wrapper">
        <div class="block-title"><div class="close"></div>Заказ такси</div>
        <div class="order-form">
            <div class="name"><label for="msg-name">Ваше имя</label><div class="box"><input type="text" name="msg-name" id="msg-name" value=""></div></div>
            <div class="phone"><label for="msg-phone">Ваш телефон</label><div class="box"><span>+7</span><div class="number"><input type="text" name="msg-phone" id="msg-phone" value=""></div></div></div>
            <div class="date"><label for="msg-date">Дата и время</label><div class="box"><select name="msg-date" id="msg-date"></select></div></div>
            <div class="hour"><label for="msg-hour">Час.</label><div class="box">
                <select name="msg-hour" id="msg-hour">
                    <option>00</option><option>01</option><option>02</option><option>03</option><option>04</option>
                    <option>05</option><option>06</option><option>07</option><option>08</option><option>09</option>
                    <option>10</option><option>11</option><option>12</option><option>13</option><option>14</option>
                    <option>15</option><option>16</option><option>17</option><option>18</option><option>19</option>
                    <option>20</option><option>21</option><option>22</option><option>23</option>
                </select></div></div>
            <div class="minutes"><label for="msg-minutes">Мин.</label><div class="box">
                <select name="msg-minutes" id="msg-minutes">
                    <option>00</option><option>05</option><option>10</option><option>15</option><option>20</option>
                    <option>25</option><option>30</option><option>35</option><option>40</option><option>45</option>
                    <option>50</option><option>55</option>
                </select></div></div>
        </div>
        <div class="route">
            <div class="title">Маршрут</div>
            <div class="w1"><div class="w2">
                <div class="left">Откуда<br>Куда</div>
<!--                <div class="right"><span id="from-info"></span>Дмитровское шоссе, дом 48, корпус 2<br>Проспект Мира, дом 57</div>-->
                <div class="right"><span id="from-info"></span><br><span id="to-info"></span></div>
            </div></div>
            <div class="change"><a href="javascript:kfns.closeOrderWindow();">Изменить маршрут</a></div>
        </div>
        <div class="send"><a href="javascript: kfns.createOrder();"><span><span>Заказать</span></span></a></div>
        <div class="footnote">Все поля обязательны для заполнения</div>
    </div></div>
</div>
<!--/WINDOWS-->

<!--WINDOWS-->
<div id="msg-confirm-phone" class="msg-overlay hidden">
    <div class="float-block"><div class="wrapper">
        <div class="block-title"><div class="close"></div>Подтверждение заказа</div>
        <div class="text-block"><p>
            <b><span id="sn_client_name"></span>, спасибо, что выбрали такси &laquo;Цель&raquo;!</b><br>
            На ваш мобильный телефон <b id="b_phone"></b> было отправлено СМС с кодом подтверждения.
        </p></div>
        <div class="sms-captcha"><div>Введите код из СМС<br><input id="tx_confirm_code" type="text" value=""><br>для подверждения заказа</div></div>
        <div class="send2"><a href="javascript: kfns.confirmPhone();"><span><span>Подтвердить</span></span></a></div>
    </div></div>
</div>
<!--/WINDOWS-->

<!--WINDOWS-->
<div id="msg-wait" class="msg-overlay hidden">
    <div class="float-block"><div class="wrapper">
        <div class="block-title"><div class="close"></div><span>Заказ принят</span></div>
        <div class="text-block with-logo"><p>
            <!-- <b>Спасибо, мы получили ваш заказ — </b><br>
           через 1 минуту наш оператор свяжется с вами. -->
            Происходит поиск автомобиля, пожалуйста подождите...<br>
            <button id="bt_call_on_assign">Сообщить по телефону</button> <button id="bt_cancelorder">Отменить заказ</button>
        </p></div>
    </div></div>
</div>
<!--/WINDOWS-->

<!--WINDOWS-->
<div id="msg-car" class="msg-overlay hidden">
    <div class="float-block"><div class="wrapper">
        <div class="block-title"><div class="close"></div><span>Автомобиль найден</span></div>
        <div class="text-block with-logo"><p>
                <span id="sn_car"></span><br>
            <button id="bt_cancelorder2">Отменить заказ</button> <button id="bt_confirm">Подтвердить</button>
        </p></div>
    </div></div>
</div>
<!--/WINDOWS-->

<!--WINDOWS-->
<div id="msg-info" class="msg-overlay hidden">
    <div class="float-block"><div class="wrapper">
        <div class="block-title"><div class="close"></div><span></span></div>
        <div class="text-block with-logo"><p></p></div>
    </div></div>
</div>
<!--/WINDOWS-->

<!--HEADER-->
<header id="header-box"><div class="wrapper">
    <div class="logo"><a href="#"><img src="img/taxi-target-logo.gif" alt="Такси Цель"></a></div>
    <div class="call-center"><span>Call-центр</span> +7 495 22 108 22</div>
    <div class="menu"><ul>
        <li><a href="#">On line заказ</a></li>
        <li><a href="#">Skype</a></li>
        <li><a href="#" class="dashed" onclick="return hs.htmlExpand(this, { contentId: 'popup-boomerang-1' })">Бумеранг</a></li>
        <li><a href="#">Звонок с сайта</a></li>
        <li><a href="#" class="dashed" onclick="return hs.htmlExpand(this, { contentId: 'popup-qr-1' })">QR-код</a></li>
    </ul></div>
    <div class="social-networks"><div class="w1"><div class="w2">
        <a href="#"><img src="img/ico-facebook.gif" alt="Facebook"></a>
        <a href="#"><img src="img/ico-twitter.gif" alt="Twitter"></a>
        <a href="#"><img src="img/ico-vkontakte.gif" alt="VKontakte"></a>
        <a href="#"><img src="img/ico-odnoklassniki.gif" alt="Odnoklassniki"></a>
        <a href="#"><img src="img/ico-google-plus.gif" alt="Google Plus"></a>
        <a href="#"><img src="img/ico-livejournal.gif" alt="Live Journal"></a>
        <a href="#"><img src="img/ico-plus.gif" alt="More"></a>
        <div class="count">1234</div>
    </div></div></div>
</div></header>
<div class="popup-box popup-qr" id="popup-qr-1"><div class="wrapper"><img src="images/img-qr-big.gif" alt="QR"></div></div>
<div class="popup-box popup-boomerang" id="popup-boomerang-1"><div class="wrapper">
    <div class="title">Услуга &laquo;Бумеранг&raquo;</div>
    <p class="text1">Вызывайте &laquo;Такси Цель&raquo;,<br>даже если баланс на нуле!</p>
    <p class="text2">Наберите <span>+7 925 744 69 22</span><br>и мы перезвоним!</p>
    <p class="text3">Наша программа автоматически определя- ет номер сбрасывает вызов и перезванива- ет. Даже если на номер Бумеранга пришла смс, он также перезвонит.</p>
</div></div>
<!--/HEADER-->

<!--TOP MENU-->
<nav id="top-menu-box"><div class="wrapper1"><div class="wrapper2"><div class="wrapper3"><table><tr>
    <td><a href="#"><img src="img/tm-ico-home.gif" alt="На главную"></a></td>
    <td class="current"><a href="#"><span>Расчет стоимости</span></a></td>
    <td><a href="#"><span>Тарифы</span></a><div class="sub size-2"><div class="w1"><div class="w2"><div class="w3"><div class="w4"><div class="w5"><div class="w6"><ul>
        <li><a href="#">Тарифы по Москве</a></li>
        <li><a href="#">Тарифы в аэропорт</a></li>
    </ul></div></div></div></div></div></div></div></td>
    <td><a href="#"><span>Вакансии</span></a></td>
    <td><a href="#"><span>Партнеры</span></a></td>
    <td><a href="#"><span>Корпоративным клиентам</span></a></td>
    <td><a href="#"><span>Отзывы</span></a></td>
    <td><a href="#"><span>Правила</span></a></td>
</tr></table></div></div></div></nav>
<!--/TOP MENU-->

<!--CONTENT-->
<div id="content-box">

<!--content > breadcrumbs-->
<div class="breadcrumbs"><a href="#">Главная</a> Расчет поездки</div>
<!--/content > breadcrumbs-->

<!--content > calculator-->
<main class="inner-page-text">
    <div id="data" style="display: none"></div>
    <div class="block-title"><h1>Расчет поездки</h1></div>

    <div id="calc-tabs" class="calc-tabs"><ul>
        <li id="li_city" class="active"><a href="javascript:selecttab('city');">По городу</a></li>
        <li id="li_airport"><a href="javascript:selecttab('airport');">Аэропорт</a></li>
        <li id="li_train_station"><a href="javascript:selecttab('train_station');">Вокзал</a></li>
    </ul></div>

    <div id="div_city" class="calc-direction">
        <div class="top-title">Откуда</div>
        <div class="top-input">
            <div><input type="text" value="" name="calc_from" id="calc_from"
                        data-provide="typehead"></div>
            <span>Например: Дмитровское шоссе, дом 48, корпус 2</span></div>
        <div id="div_house_from" class="house hidden">
            Дом: <input id="tx_house_from" type="text">
            Корпус: <input id="tx_korp_from" type="text">
            Строение: <input id="tx_str_from" type="text">
            Владение: <input id="tx_vlad_from" type="text">
            Подъезд <input id="tx_pod_from" type="text">
        </div>
        <div class="bottom-title">Куда</div>
        <div class="bottom-input">
            <div><input type="text" value="" name="calc_to" id="calc_to"></div>
            <span>Например: Проспект Мира, дом 57</span></div>
        <div id="div_house_to" class="house_to hidden">
            Дом: <input id="tx_house_to" type="text">
            Корпус: <input id="tx_korp_to" type="text">
            Строение: <input id="tx_str_to" type="text">
            Владение: <input id="tx_vlad_to" type="text">
        </div>
    </div>

    <div id="div_airport" class="calc-direction hidden">
        <div class="from-to"><input type="checkbox" name="calc_is_to_air" id="calc_is_to_air" checked></div>
        <div class="target-type">аэропорт</div>
        <div id="rd_airport" class="target-switch"><ul>
            <li id="li_dom" class="active"><a href="javascript:selectair('dom', 'аэропорт Домодедово', true)"><span>Домодедово</span></a></li>
            <li id="li_sher"><a href="javascript:selectair('sher', 'Шереметьево терминал', false)" style="margin-right: 0px;"><span>Шереметьево</span></a></li>
            <li class="aterm"><a href="javascript:setterm('B')" style="margin: 0px"><span>B</span></a></li>
            <li class="aterm"><a href="javascript:setterm('C')" style="margin: 0px"><span>C</span></a></li>
            <li class="aterm"><a href="javascript:setterm('D')" style="margin: 0px"><span>D</span></a></li>
            <li class="aterm"><a href="javascript:setterm('E')" style="margin: 0px"><span>E</span></a></li>
            <li class="aterm"><a href="javascript:setterm('F')" style="margin: 0px"><span>F</span></a></li>
            <li id="li_vnuk"><a href="javascript:selectair('vnuk', 'аэропорт Внуково', true)"><span>Внуково</span></a></li>
        </ul></div>
        <div class="bottom-title">Откуда</div>
        <div class="bottom-input"><div><input type="text" value="" name="calc_air" id="calc_air"></div><span>Например: Дмитровское шоссе, дом 48, корпус 2</span></div>
        <div id="div_house_air" class="house_to hidden">
            Дом: <input id="tx_house_air" type="text">
            Корпус: <input id="tx_korp_air" type="text">
            Строение: <input id="tx_str_air" type="text">
            Владение: <input id="tx_vlad_air" type="text">
        </div>
    </div>

    <div id="div_train_station" class="calc-direction hidden">
        <div class="from-to"><input type="checkbox" name="calc_is_to_tr" id="calc_is_to_tr" checked></div>
        <div class="target-type">вокзал</div>
        <div class="target-select"><select id="sl_tr">
            <option>Белорусский вокзал</option>
            <option>Казанский вокзал</option>
            <option>Киевский вокзал</option>
            <option>Курский вокзал</option>
            <option>Ленинградский вокзал</option>
            <option>Павелецкий вокзал</option>
            <option>Рижский вокзал</option>
            <option>Савеловский вокзал</option>
            <option>Ярославский вокзал</option>
        </select></div>
        <div class="bottom-title">Откуда</div>
        <div class="bottom-input"><div><input type="text" value="" name="calc_tr" id="calc_tr"></div><span>Например: Дмитровское шоссе, дом 48, корпус 2</span></div>
        <div id="div_house_tr" class="house_to hidden">
            Дом: <input id="tx_house_tr" type="text">
            Корпус: <input id="tx_korp_tr" type="text">
            Строение: <input id="tx_str_tr" type="text">
            Владение: <input id="tx_vlad_tr" type="text">
        </div>
    </div>

    <div id="ch_options" class="calc-options"><div class="w1"><div class="w2">

        <div class="options"><table>
            <tr class="dark">
                <td class="title">Универсал</td>
                <td class="desc">Представляет собой автомобиль с увеличенным багажным отделением и дополнительной подъёмной дверью в задней части автомобиля.</td>
                <td class="price">200 руб.</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_universal" id="calc_universal"></div></td>
            </tr>
            <tr>
                <td class="title">Минивен</td>
                <td class="desc">Легковой автомобиль на 5-7 пассажирских мест.</td>
                <td class="price">400 руб.</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_miniwan" id="calc_miniwan"></div></td>
            </tr>
            <tr class="dark">
                <td class="title">Перевозка<br>животных</td>
                <td class="desc">Допускается перевоз только кошек и собак (в контейнере при обязательном наличии подстилки) в соответствии с тарифами.</td>
                <td class="price"><strike>&nbsp; 200 &nbsp;</strike> 0 руб.</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_" id="calc_"></div></td>
            </tr>
            <tr>
                <td class="title">Перевозка<br>лыж</td>
                <td class="desc">Перевозка одной пары лыж с палками.</td>
                <td class="price">150 руб.</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_ski" id="calc_ski"></div></td>
            </tr>
            <!--<tr style="display: none">-->
            <tr class="dark">
                <td class="title">Учитывать<br>пробки</td>
                <td class="desc">Учитывать текущие пробки: маршруты объезда и время простоя в пробке</td>
                <td class="price">-</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_probki" id="calc_probki" checked="checked"></div></td>
            </tr>
            <tr style="display: none">
                <td class="title">Перевозка<br>лыж</td>
                <td class="desc">Перевозка одной пары лыж с палками.</td>
                <td class="price">150 руб.</td>
                <td class="checkbox"><div><input type="checkbox" name="calc_airport" id="calc_airport"></div></td>
            </tr>
        </table></div>
        <div class="buttons hidden"><a href="javascript:calcWithProbki()"><span><span>С учетом текущих пробок</span></span></a>
            <a href="javascript:calcWithoutProbki()"><span><span>Расчитать</span></span></a></div>

        <div class="total">
            <div id="div_dist_info" class="data hidden"><span class="title">Расчет вашей поездки</span><ul>
                    <li>Расстояние: <b><span id="dist_km"></span> км.</b></li>
                <!--<li>Из них по МКАД: <b>2 км.</b></li>-->
                <!--<li>Из них за МКАД: <b>0 км.</b></li>-->
                <li>Время в пути: <b><span id="dist_time"></span> мин.</b></li>
            </ul></div>
            <div id="div_cost_info" class="cost hidden">
                <div class="title">Стоимость поездки <span><span>*</span> Расчетные данные могут расходиться с фактическими</span></div>
                <div class="box"><div class="w">
                        <div class="price"> с <span id="cost_dt_day"></span> до <span id="cost_dt_night"></span>: <b><span id="cost_day">34</span><span class="a">*</span> руб.</b><br>
                            с <span id="cost_dt_night2"></span> до <span id="cost_dt_day2"></span>: <b><span id="cost_night"></span><span class="a">*</span> руб.</b></div>
                    <div id="div_neworder" class="order"><a href="javascript: kfns.openOrderForm();"><span><span>Заказать онлайн</span></span></a></div>
                    <div id="div_cancelorder" class="order hidden"><a href="javascript: cancelOrder();"><span><span>Отменить заказ</span></span></a></div>
                </div></div>
            </div>
        </div>

    </div></div></div>

    <div class="calc-map"><div id="map" class="w"></div></div>

    <div class="calc-desc">
        <div class="left">
            <p>Стоянка-ожидание клиента, простой на светофорах, пробках и движение со скоростью менее 5 км/ч — <b>9 руб./мин (540 руб./час).</b><br>
            Первые 10 минут ожидания — <b>бесплатно.</b><br>
            При поездке на нашем такси <b>все клиенты застрахованы </b>автоматически.</p>
        </div>
        <div class="right">
            <p>Отказ от поездки, если автомобиль прибыл по адресу — <b>150 руб.</b><br>
            При недостаточности места в багажнике, перевоз багажа в салоне автомобиля (кроме ручной клади), тарифицируется дополнительно <b>40 руб. за каждое место.</b></p>
        </div>
    </div>

</main>
<!--/content > calculator-->

<!--content > contact variants-->
<aside class="contact-variants-horiz"><div class="wrapper1"><div class="wrapper2"><ul>
    <li><div class="call-center" onclick="return hs.htmlExpand(this, { contentId: 'popup-qr-2' })">Позвоните в call-центр<br><b>+7 495 22 108 22</b></div></li>
    <li><div class="call-from-site"><a href="#">Сделайте<br><b>звонок с сайта</b></a></div></li>
    <li><div class="send-order"><a href="#">Отправьте<br><b>онлайн заявку</b></a><div class="orr"></div></div></li>
    <li><div class="call-by-skype"><a href="#">Позвоните<br><b>через Скайп</b></a><div class="orr"></div></div></li>
    <li><div class="boomerang" onclick="return hs.htmlExpand(this, { contentId: 'popup-boomerang-2' })">Бумеранг!</div></li>
</ul></div></div></aside>

<div class="popup-box popup-qr" id="popup-qr-2"><div class="wrapper"><img src="images/img-qr-big.gif" alt="QR"></div></div>
<div class="popup-box popup-boomerang" id="popup-boomerang-2"><div class="wrapper">
    <div class="title">Услуга &laquo;Бумеранг&raquo;</div>
    <p class="text1">Вызывайте &laquo;Такси Цель&raquo;,<br>даже если баланс на нуле!</p>
    <p class="text2">Наберите <span>+7 925 744 69 22</span><br>и мы перезвоним!</p>
    <p class="text3">Наша программа автоматически определя- ет номер сбрасывает вызов и перезванива- ет. Даже если на номер Бумеранга пришла смс, он также перезвонит.</p>
</div></div>
<!--/content > contact variants-->

</div>
<!--END OF CONTENT-->

<!--FOOTER-->
<footer id="footer-box"><div class="wrapper">
    <nav class="menu">
        <ul>
            <li class="link"><a href="#"><span>Главная</span></a></li>
            <li class="link active"><a href="#"><span>Расчет стоимости</span></a></li>
            <li class="link"><a href="#"><span>Тарифы по городу</span></a></li>
            <li class="link"><a href="#"><span>Тарифы в аэропорт</span></a></li>
            <li class="link"><a href="#"><span>Вакансии</span></a></li>
            <li class="link"><a href="#"><span>Корпоративным клиентам</span></a></li>
        </ul>
        <ul>
            <li class="link"><a href="#"><span>Партнеры</span></a></li>
            <li class="link"><a href="#"><span>Отзывы</span></a></li>
            <li class="link"><a href="#"><span>Правила оказания услуг</span></a></li>
            <li class="link"><a href="#"><span>Пожаловаться</span></a></li>
            <li class="link"><a href="#"><span>Обратная связь</span></a></li>
        </ul>
        <ul>
            <li class="title">Контакты</li>
            <li class="email"><b>E-mail:</b> <a href="mailto:info@taxicel.ru">info@taxicel.ru</a></li>
            <li class="address"><b>Наш адрес:</b><br>г. Москва, 1-ая улица Энтузиастов,<br>дом 22, офис №404 <a href="#">на карте</a><br><span>ОГРН 87933456706554</span></li>
        </ul>
    </nav>
    <div class="qr"><img src="images/img-qr-footer.png" alt="QR-код"></div>
    <div class="copyright">&copy; 2013, Такси &laquo;Цель&raquo;<br>Все права защищены</div>
    <div class="counters"><a href="#"><img src="images/img-counter.gif" alt=""></a></div>
    <div class="developer"><span class="desc">Разработка<br>и продвижение</span><span class="link"><a href="#">tvoyseo.ru</a></span></div>
</div></footer>
<!--/FOOTER-->
</body>
</html>
