let id;
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
                if (data == '') {
                    document.getElementById('airfoilDetailInfo').innerText = "Airfoil не найден";
                } else {
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

    let edit = document.getElementById('edit_link');
    edit.setAttribute("href", "adminka/edit_airfoil.html?airfoilId=" + id);

    let name = document.getElementById('name_detail');
    name.innerText = data.name;

    let downloadStl = document.getElementById('downloadStl');
    downloadStl.setAttribute("href", data.stlFilePath);

    let image = document.getElementById('imgDetail');
    image.setAttribute("src", data.image);

    let description = document.getElementById('descr_detail');
    description.innerText = data.description;

    let fileCsvName = data.fileCsvName;

    for (let i = 0, j = 1; i < fileCsvName.length; i++, j++) {

        let csvItemChBox = document.getElementById('checkbox' + j);
        csvItemChBox.setAttribute("fileName", fileCsvName[i].fileName);

        let csvRenolds = document.getElementById('Renolds' + j);
        csvRenolds.innerHTML = fileCsvName[i].renolgs;

        let csvNCrit = document.getElementById('NCrit' + j);
        csvNCrit.innerHTML = fileCsvName[i].nCrit;

        let csvMaxClCd = document.getElementById('MaxClCd' + j);
        csvMaxClCd.innerHTML = fileCsvName[i].maxClCd;

        let linkItem = document.getElementById('link_file' + j);
        linkItem.setAttribute("href", "/resources/tmpCsv/" + fileCsvName[i].fileName);
        linkItem.innerText = fileCsvName[i].fileName;
    }

    let imgCsvName = data.imgCsvName;
    let imgCsvBox = document.getElementById('imgCsvBox');
    for (let i = 0; i < imgCsvName.length; i++) {
        let imgCsv = document.createElement('div');
        let img = document.createElement('img');
        img.setAttribute('src', imgCsvName[i]);
        imgCsv.appendChild(img);
        imgCsvBox.appendChild(imgCsv);
    }
}

function fillEditableContentDetailInfo(data) {

    document.getElementById("name").setAttribute("value", data.name);
    document.getElementById("ShortName").setAttribute("value", data.shortName);
    document.getElementById("description").setAttribute("value", data.description);
    let img_detail = document.getElementById("img_detail");
    let img = document.createElement('img');
    img.setAttribute("src", data.image);
    img_detail.appendChild(img);

    let fileCsvName = data.fileCsvName;
    let links = document.getElementById('graf');
    for (let i = 0; i < fileCsvName.length; i++) {
        let linkItem = document.createElement('a');
        linkItem.setAttribute("href", "/resources/tmpCsv/" + fileCsvName[i].fileName);
        linkItem.innerHTML = fileCsvName[i].fileName;
        links.appendChild(linkItem);
        links.appendChild(document.createElement('br'))
    }
}

function refreshiframe() {
    console.log("refreshiframe");
    let actions = document.getElementsByName("activ");
    let checkeds = new Array(10);
    for (let i = 0; i < actions.length; i++) {
        if (actions[i].checked) {
            checkeds[i] = actions[i].getAttribute('filename');
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
                let imgCsvBox = document.createElement('div');
                imgCsvBox.id = "imgCsvBox";
                for (let i = 0; i < data.length; i++) {
                    let imgCsv = document.createElement('div');
                    let img = document.createElement('img');
                    img.setAttribute('src', data[i] + "?" + Math.random());
                    imgCsv.appendChild(img);
                    imgCsvBox.appendChild(imgCsv);
                }
                document.getElementById("airfoilDetailInfo").appendChild(imgCsvBox);
            }
        });
    });
}
