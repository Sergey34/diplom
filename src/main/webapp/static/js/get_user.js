$(document).ready(function () {
    $.ajax({
        url: rootUrl + "/rest/userInfo"
    }).then(function (data) {
        console.log(data);
        let navbar_nav = document.getElementById('navbar-nav');
        let nav_item = document.createElement('li');
        let button_login = document.createElement('a');
        if (data == '') {
            console.log('гость');

            button_login.setAttribute('href', rootUrl + '/login.html');
            button_login.innerText = "login";
        } else {
            console.log(data);
            let navbar_nav_sign = document.getElementById('navbar-nav');
            let nav_item_sign = document.createElement('li');
            //<p class="navbar-text">Signed in as Mark Otto</p>
            let signed_in = document.createElement('p');
            signed_in.setAttribute('class', 'navbar-text');
            signed_in.innerText = "Signed in as " + data.userName;
            nav_item_sign.appendChild(signed_in);
            navbar_nav_sign.appendChild(nav_item_sign);


            button_login.setAttribute('href', rootUrl + '/logout');
            button_login.innerText = "Logout";
        }
        nav_item.appendChild(button_login);
        navbar_nav.appendChild(nav_item);
    });
});