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
    var csvRenolds = document.createElement('div');
    csvRenolds.innerHTML = "Renolds";
    csvRenolds.className = "renolgs";
    links.appendChild(csvRenolds);
    var NCrit = document.createElement('div');
    NCrit.innerHTML = "NCrit";
    NCrit.className = "NCrit";
    links.appendChild(NCrit);
    var MaxClCd = document.createElement('div');
    MaxClCd.innerHTML = "MaxClCd";
    MaxClCd.className = "MaxClCd";
    links.appendChild(MaxClCd);
    links.appendChild(document.createElement('br'));

    for (var i = 0; i < fileCsvName.length; i++) {
        var csvItem = document.createElement('div');
        var csvItemChBox = document.createElement('input');
        csvItemChBox.setAttribute("type", "checkbox");
        csvItemChBox.setAttribute("class", "csvItemChBox");
        csvItemChBox.setAttribute("id", fileCsvName[i].fileName);
        csvItemChBox.setAttribute("name", "activ");
        csvItemChBox.setAttribute("checked", "checked");
        csvItem.appendChild(csvItemChBox);

        var csvRenolds = document.createElement('div');
        csvRenolds.innerHTML = fileCsvName[i].renolgs;
        csvRenolds.className = "renolgs";
        csvItem.appendChild(csvRenolds);
        var csvNCrit = document.createElement('div');
        csvNCrit.innerHTML = fileCsvName[i].nCrit;
        csvNCrit.className = "NCrit";
        csvItem.appendChild(csvNCrit);
        var csvMaxClCd = document.createElement('div');
        csvMaxClCd.innerHTML = fileCsvName[i].maxClCd;
        csvMaxClCd.className = "MaxClCd";
        csvItem.appendChild(csvMaxClCd);

        var fileName = document.createElement('div');
        fileName.className = "fileName";
        var linkItem = document.createElement('a');
        linkItem.setAttribute("href", "/resources/tmpCsv/" + fileCsvName[i].fileName);
        linkItem.innerHTML = fileCsvName[i].fileName;
        fileName.appendChild(linkItem);
        csvItem.appendChild(fileName);
        links.appendChild(csvItem);
        links.appendChild(document.createElement('br'))
    }
    var btnUpdatePlot = document.createElement('input');
    btnUpdatePlot.setAttribute("type", "button");
    btnUpdatePlot.setAttribute("value", "Обновить");
    btnUpdatePlot.setAttribute("onclick", "refreshiframe();");
    links.appendChild(btnUpdatePlot)

    var imgCsvName = data.imgCsvName;
    var imgCsvBox = document.createElement('div');
    imgCsvBox.id = "imgCsvBox";
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
    adf.appendChild(links);
    adf.appendChild(imgCsvBox);

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


}

function refreshiframe() {
    console.log("refreshiframe");
    // var actions = document.querySelector('.csvItemChBox:checked');
    var actions = document.getElementsByName("activ");
    var checkeds = new Array(10);
    for (var i = 0; i < actions.length; i++) {
        if (actions[i].checked) {
            checkeds[i] = actions[i].id;
        }
    }
    console.log(checkeds);

    $(document).ready(function () {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/rest/updateGraf/" + id,
            data: JSON.stringify(checkeds),
            dataType: 'json',
            timeout: 600000,
            error: function (e) {
                console.log("ERROR: ", e);
            },
            success: function (data) {
                console.log("SUCCESS: ", data);
                document.getElementById("airfoilDetailInfo").removeChild(document.getElementById("imgCsvBox"));
                var imgCsvBox = document.createElement('div');
                imgCsvBox.id = "imgCsvBox";
                for (var i = 0; i < data.length; i++) {
                    var imgCsv = document.createElement('div');
                    var img = document.createElement('img');
                    img.setAttribute('src', data[i] + "?" + Math.random());
                    imgCsv.appendChild(img);
                    imgCsvBox.appendChild(imgCsv);
                }
                document.getElementById("airfoilDetailInfo").appendChild(imgCsvBox);
            }
        });
    });
}
