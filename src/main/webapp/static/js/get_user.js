var LOG_IN = '<a href="login" target="_blank"> Авторизоваться</a>';
var LOG_OUT = '<a href="logout">Выйти</a>';
$(document).ready(function () {
    $.ajax({
        url: "/rest/userInfo"
    }).then(function (data) {
        console.log(data);
        if (data == '') {
            console.log('гость');
            $('.user_hello').append('Гость');
            $('.user_action').append(LOG_IN);
        } else {
            console.log(data);
            $('.user_hello').append(data);
            $('.user_action').append(LOG_OUT);
        }
    });
});