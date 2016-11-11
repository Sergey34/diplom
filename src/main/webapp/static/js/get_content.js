var currentPrefix = '';
function getContent() {
    var prefix = $.getUrlVar('prefix') != undefined ? $.getUrlVar('prefix') : 'A';

    console.log(currentPrefix);
    if (currentPrefix != '') {
        console.log('updatePage');
        document.getElementById("contentid").removeChild(document.getElementById("airfoil_list"));
    }
    currentPrefix = prefix;
    $(document).ready(function () {
        $.ajax({
            url: "/rest/getContext/" + prefix + "/0/0"
        }).then(function (data) {
            console.log(data);
            var airfoil_list = document.createElement('ul');
            airfoil_list.id = 'airfoil_list';
            if (data.length == 0) {
                airfoil_list.innerHTML = "не удалось загрузить airfoil с перфиксом " + prefix
            } else {
                data.forEach(logArrayElements);
            }
            function logArrayElements(element, index, array) {
                var div = document.createElement('div');
                div.id = 'airfoil_item';

                var name = document.createElement('div');
                extracted(name, element.name, 'name');

                var image = document.createElement('div');
                var img = document.createElement('img');
                img.setAttribute("src", "/resources/airfoil_img" + element.image);
                extracted(image, '', 'img');
                image.appendChild(img);

                var description = document.createElement('div');
                extracted(description, element.description, 'descr');

                var links = document.createElement('div');
                links.setAttribute('class', 'link');
                for (var i = 0; i < element.links.length; i++) {
                    var linkItem = document.createElement('p');
                    var innerHtml = '<a href="' + element.links[i].link + '">' + element.links[i].name + '</a>';
                    extracted(linkItem, innerHtml);
                    links.appendChild(linkItem);
                }

                div.appendChild(name);
                div.appendChild(image);
                div.appendChild(description);
                div.appendChild(links);
                // добавление в конец
                airfoil_list.appendChild(div);
            }

            document.getElementById('contentid').appendChild(airfoil_list);
        });
    });
}
function extracted(element, value, className) {
    element.innerHTML = value;
    element.setAttribute('class', className);
}

