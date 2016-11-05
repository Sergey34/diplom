function getContext() {
    $(document).ready(function () {
        $.ajax({
            url: "/rest/getContext/A/0/0"
        }).then(function (data) {
            console.log(data);
            var airfoil_list = document.createElement('ul');
            airfoil_list.id = 'airfoil_list';
            data.forEach(logArrayElements);
            function logArrayElements(element, index, array) {
                var id_airfoil = element.id;
                var div = document.createElement('div');
                div.id = 'airfoil_item';

                var name = document.createElement('div');
                extracted(name, element.name, 'name_' + id_airfoil, 'name');

                var id = document.createElement('div');
                id.style.display = "none";
                extracted(id, element.id, 'airfoil_id_' + id_airfoil, 'id');
                var prefix = document.createElement('div');
                prefix.style.display = "none";
                extracted(prefix, element.prefix.prefix, 'prefix_' + id_airfoil, 'prefix');

                var image = document.createElement('div');
                extracted(image, element.image, 'airfoil_image_' + id_airfoil, 'img');

                var description = document.createElement('div');
                extracted(description, element.description, 'airfoil_description_' + id_airfoil, 'descr');

                var links = document.createElement('div');
                links.id = 'link_' + id_airfoil;
                links.setAttribute('class', 'link');
                for (var i = 0; i < element.links.length; i++) {
                    var linkItem = document.createElement('p');
                    var innerHtml = '<a href="' + element.links[i].link + '">' + element.links[i].name + '</a>';
                    extracted(linkItem, innerHtml, 'airfoil_links_' + id_airfoil);
                    links.appendChild(linkItem);
                }

                var divInput = document.createElement('div');
                divInput.setAttribute('class', 'div_input');
                divInput.id = 'div_input_' + id_airfoil;
                var but = document.createElement('input');
                but.type = 'button';
                but.id = 'edit_btn_' + id_airfoil;
                but.setAttribute('onclick', 'edit(' + element.id + ');');
                but.value = "Редактировать";
                divInput.appendChild(but);

                div.appendChild(name);
                div.appendChild(image);
                div.appendChild(description);
                div.appendChild(links);
                div.appendChild(id);
                div.appendChild(prefix);
                div.appendChild(divInput);
                // добавление в конец
                airfoil_list.appendChild(div);

            }

            document.getElementById('contentid').appendChild(airfoil_list);
        });
    });
}
function extracted(element, value, id, className) {
    element.innerHTML = value;
    element.setAttribute('class', 'editable ' + className);
    element.id = id;
}

