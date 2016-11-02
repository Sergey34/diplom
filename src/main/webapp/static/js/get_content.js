$(document).ready(function () {
    $.ajax({
        url: "/rest/getContext/A/0/0"
    }).then(function (data) {
        console.log(data);
        data.forEach(logArrayElements);
        function logArrayElements(element, index, array) {
            var id_airfoil = element.id;
            var airfoil_list = document.getElementById('airfoil_list');
            var div = document.createElement('div');
            div.id = 'airfoil_item';

            var name = document.createElement('div');
            extracted(name, element.name, 'name_' + id_airfoil);

            var id = document.createElement('div');
            id.style.display = "none";
            extracted(id, element.id, 'airfoil_id_' + id_airfoil);

            var image = document.createElement('div');
            extracted(image, element.image, 'airfoil_image_' + id_airfoil);

            var description = document.createElement('div');
            extracted(description, element.description, 'airfoil_description_' + id_airfoil);

            var links = document.createElement('div');
            extracted(links, element.links, 'airfoil_links_' + id_airfoil);

            var but = document.createElement('input');
            but.type = 'button';
            but.setAttribute('onclick', 'save(' + element.id + ');');
            but.value = "Сохранить";

            div.appendChild(name);
            div.appendChild(image);
            div.appendChild(description);
            div.appendChild(links);
            div.appendChild(id);
            div.appendChild(but);
            // добавление в конец
            airfoil_list.appendChild(div);

        }
    });
});
function extracted(element, value, id) {
    element.innerHTML = value;
    element.setAttribute('class', 'editable');
    element.id = id;
}

function save(id) {
    var name = document.getElementById('name_' + id);
    console.log(name.innerHTML);
    //todo собрать все элементы с полученным id и передать в post() запросе на сервер
}