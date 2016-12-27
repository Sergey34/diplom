$(document).ready(function () {
    $.ajax({
        url: rootUrl + "/rest/menu"
    }).then(function (data) {
        document.getElementById('addAirf').setAttribute('href', rootUrl + "/adminka/addAirfoil.html");
        document.getElementById('adm').setAttribute('href', rootUrl + "/adminka/adminka.html");
        data.forEach(logArrayElements);
        function logArrayElements(element, index, array) {
            console.log(element.header);
            // элемент-список UL
            var list = document.getElementById('list');
            // новый элемент
            var li = document.createElement('H3');
            li.innerHTML = element.header;
            // добавление в конец
            list.appendChild(li);


            element.menuItems.forEach(logMenuElements)
        }

        function logMenuElements(element, index, array) {
            // console.log(element.name + '   ' + element.url);
            // элемент-список UL
            var list = document.getElementById('list');
            // новый элемент
            var li = document.createElement('LI');
            // <a href="/plotter/index">Airfoil plotter</a>
            li.innerHTML = '<a href="' + rootUrl + '/airfoilList.html?prefix=' + element.urlCode + '\">' + element.name + '</a>';
            // добавление в конец
            list.appendChild(li)
        }
    });
});
