<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <title>Статистика по операторам</title>
        <link href="css/smoothness/jquery-ui-1.10.3.custom.css" rel="stylesheet">
        <link href="css/jquery-ui-timepicker-addon.css" rel="stylesheet">
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/i18n/jquery-ui-i18n.js"></script>
        <script src="js/i18n/jquery.ui.datepicker-ru.js"></script>
        <script src="js/jquery-ui-timepicker-addon.js"></script>
        <script src="js/i18n/jquery-ui-timepicker-ru.js"></script>
        <script src="index.js"></script>
    </head>
    <body>
        Период с <input type="text" id="tx_from"> по <input type="text" id="tx_to"><br/>
        <button id="bt_ok" onclick="req_operators();">Показать</button>
        <div id="div_content"></div>
    </body>
</html>
