$(document).ready(function () {
    $.ajax({
        url: "/rest/user"
    }).then(function (data) {
        console.log(data);
        var navbar_nav = document.getElementById('navbar-nav');
        var nav_item = document.createElement('li');
        var button_login = document.createElement('a');
        if (data == '') {
            console.log('гость');

            button_login.setAttribute('href', '/login');
            button_login.innerHTML = "login";
        } else {
            console.log(data);
            var navbar_nav_sign = document.getElementById('navbar-nav');
            var nav_item_sign = document.createElement('li');
            //<p class="navbar-text">Signed in as Mark Otto</p>
            var signed_in = document.createElement('p');
            signed_in.setAttribute('class', 'navbar-text');
            signed_in.innerHTML = "Signed in as " + data.userName;
            nav_item_sign.appendChild(signed_in);
            navbar_nav_sign.appendChild(nav_item_sign);


            button_login.setAttribute('href', '/logout');
            button_login.innerHTML = "Logout";
        }
        nav_item.appendChild(button_login);
        navbar_nav.appendChild(nav_item);
    });
});