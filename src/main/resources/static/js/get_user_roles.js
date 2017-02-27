$(document).ready(function () {
    $.ajax({
        url: "/rest/userRoles"
    }).then(function (data) {
        console.log(data);
        select = document.getElementById('role');
        data.forEach(logArrayElements);
        function logArrayElements(element, index, array) {
            var opt = document.createElement('option');
            opt.innerHTML = element.authority;
            select.appendChild(opt);
        }
    });
});