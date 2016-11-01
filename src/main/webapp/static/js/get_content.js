$(document).ready(function () {
    $.ajax({
        url: "/rest/getContext/A/0/0"
    }).then(function (data) {
        console.log(data);
        data.forEach(logArrayElements);
        function logArrayElements(element, index, array) {
            var airfoil_list = document.getElementById('airfoil_list');
            var div = document.createElement('div');
            div.id = 'airfoil_item';
            var name = document.createElement('div');
            name.innerHTML = element.name;
            var image = document.createElement('div');
            image.innerHTML = element.image;
            var description = document.createElement('div');
            description.innerHTML = element.description;
            var links = document.createElement('div');
            links.innerHTML = element.links;

            div.appendChild(name);
            div.appendChild(image);
            div.appendChild(description);
            div.appendChild(links);
            // добавление в конец
            airfoil_list.appendChild(div);

        }
    });
});
