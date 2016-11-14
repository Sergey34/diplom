$(document).ready(function () {
    $.ajax({
        url: "/rest/menu"
    }).then(function (data) {
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
            li.innerHTML = '<a href="/context?prefix=' + element.urlCode + '\">' + element.name + '</a>';
            // добавление в конец
            list.appendChild(li)
        }
    });
});
