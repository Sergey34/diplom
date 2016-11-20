var id;
function getDetailInfo(forEdit) {
    id = $.getUrlVar('airfoilId');
    if (id == undefined || id == '') {
        self.close()
    } else {
        console.log('id = ' + id);
        $(document).ready(function () {
            $.ajax({
                url: "/rest/getDetailInfo/" + id
            }).then(function (data) {
                console.log(data);
                if (data.error != true) {
                    if (forEdit) {
                        fillEditableContentDetailInfo(data);
                    } else {
                        fillContentDetailInfo(data)
                    }
                }
            });
        });
    }
}

function fillContentDetailInfo(data) {
    var adf = document.createElement('div');
    adf.id = 'airfoilDetailInfo';

    if (data.warnMessage = undefined) {
        adf.innerHTML = warnMessage;
    }

    var edit = document.createElement('a');
    edit.setAttribute("href", "/editAirfoil?airfoilId=" + id);
    edit.innerHTML = 'Редактировать';
    adf.appendChild(edit);

    var name = document.createElement('div');
    extracted(name, data.name, 'name_detail');

    var image = document.createElement('div');
    var img = document.createElement('img');
    img.setAttribute("src", data.image);
    extracted(image, '', 'img_detail');
    image.appendChild(img);

    var description = document.createElement('div');
    extracted(description, data.description, 'descr_detail');

    var fileCsvName = data.fileCsvName;
    var links = document.createElement('div');
    for (var i = 0; i < fileCsvName.length; i++) {
        var linkItem = document.createElement('a');
        linkItem.setAttribute("href", "/resources/tmpCsv/" + fileCsvName[i]);
        linkItem.innerHTML = fileCsvName[i];
        links.appendChild(linkItem);
        links.appendChild(document.createElement('br'))
    }
    var imgCsvName = data.imgCsvName;
    var imgCsvBox = document.createElement('div');
    for (var i = 0; i < imgCsvName.length; i++) {
        var imgCsv = document.createElement('div');
        var img = document.createElement('img');
        img.setAttribute('src', imgCsvName[i]);
        imgCsv.appendChild(img);
        imgCsvBox.appendChild(imgCsv);
    }


    adf.appendChild(name);
    adf.appendChild(image);
    adf.appendChild(description);
    adf.appendChild(imgCsvBox);
    adf.appendChild(links);

    // добавление в конец
    document.getElementById('contentid').appendChild(adf);
}
function extracted(element, value, className) {
    element.innerHTML = value;
    element.setAttribute('class', className);
}

function createNewInput(name, value) {
    var input = document.createElement('input');
    input.value = value;
    input.name = name;
    input.setAttribute('type', 'text');
    return input;
}
function fillEditableContentDetailInfo(data) {
    var adf = document.getElementById("airfoilDetailInfo");
    if (data.warnMessage = undefined) {
        adf.innerHTML = warnMessage;
    }
    document.getElementById("name").setAttribute("value", data.name);
    document.getElementById("ShortName").setAttribute("value", data.shortName);
    document.getElementById("description").setAttribute("value", data.description);
    var img_detail = document.getElementById("img_detail");
    var img = document.createElement('img');
    img.setAttribute("src", data.image);
    img_detail.appendChild(img);


    var fileCsvName = data.fileCsvName;
    var links = document.getElementById('graf');
    for (var i = 0; i < fileCsvName.length; i++) {
        var linkItem = document.createElement('a');
        linkItem.setAttribute("href", "/resources/tmpCsv/" + fileCsvName[i]);
        linkItem.innerHTML = fileCsvName[i];
        links.appendChild(linkItem);
        links.appendChild(document.createElement('br'))
    }
    /*    var imgCsvName = data.imgCsvName;
     var imgCsvBox = document.createElement('div');
     for (var i = 0; i < imgCsvName.length; i++) {
     var imgCsv = document.createElement('div');
     var img = document.createElement('img');
     img.setAttribute('src', imgCsvName[i]);
     imgCsv.appendChild(img);
     imgCsvBox.appendChild(imgCsv);
     }*/


}
