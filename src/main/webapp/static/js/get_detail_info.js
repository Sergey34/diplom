let id;
function getDetailInfo(forEdit) {
    id = $.getUrlVar('airfoilId');
    if (id == undefined || id == '') {
        self.close()
    } else {
        console.log('id = ' + id);
        $(document).ready(function () {
            $.ajax({
                url: rootUrl + "/rest/getDetailInfo/" + id
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

    let name = document.getElementById('airfoil_name');
    name.innerText = data.name;

    let downloadStl = document.getElementById('downloadStl');
    downloadStl.setAttribute("href", data.stlFilePath);

    let image = document.getElementById('imgDetail');
    image.setAttribute("src", data.image);

    let description = document.getElementById('descr_detail');
    description.innerText = data.description;

    let Polars_for = document.getElementById('Polars_for');
    Polars_for.innerText = Polars_for.textContent + data.name;

    let fileCsvName = data.fileCsvName;


    let table = document.getElementById('table1');
    for (let i = 0, j = 1; i < fileCsvName.length; i++, j++) {

        let tr = document.createElement('tr');
        tr.id = j;
        /*
         <td><input type="checkbox" id="checkbox1" class="csvItemChBox" name="activ"
         checked="checked" filename="xf-a18-il-50000-n5.csv"></td>
         <td id="Renolds1">50,000</td>
         <td id="NCrit1">5</td>
         <td id="MaxClCd1">44.6 at α=4.75°</td>
         <td id="fileName1"><a id="link_file1"
         href="http://localhost:8080/resources/tmpCsv/xf-a18-il-50000-n5.csv">xf-a18-il-50000-n5.csv</a>
         </td>*/
        let td = document.createElement('td');
        let checkbox = document.createElement('input');
        checkbox.setAttribute('type', 'checkbox');
        checkbox.setAttribute('name', 'activ');
        checkbox.setAttribute('checked', 'checked');
        checkbox.setAttribute('filename', fileCsvName[i].fileName);
        checkbox.id = 'checkbox' + j;
        td.appendChild(checkbox);
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "Renolds" + j;
        td.innerHTML = fileCsvName[i].renolgs;
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "NCrit" + j;
        td.innerHTML = fileCsvName[i].nCrit;
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "MaxClCd" + j;
        td.innerHTML = fileCsvName[i].maxClCd;
        tr.appendChild(td);

        td = document.createElement('td');
        let link_file = document.createElement('a');
        link_file.id = "link_file" + j;
        link_file.innerText = fileCsvName[i].fileName;
        link_file.setAttribute('href', fileCsvName[i].fileName);
        td.appendChild(link_file);
        tr.appendChild(td);

        table.appendChild(tr);
    }

    let imgCsvName = data.imgCsvName;
    let carousel_indicators = document.getElementById('carousel-indicators');
    let carousel_inner = document.getElementById('carousel-inner');
    for (let i = 0; i < imgCsvName.length; i++) {
        // <li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>

        let li = document.createElement('li');
        li.setAttribute('data-target', "#carousel-example-generic");
        li.setAttribute('data-slide-to', i + '');
        if (i == 0) {
            li.setAttribute('class', 'active');
        }
        carousel_indicators.appendChild(li);

        let item = document.createElement('div');
        if (i == 0) {
            item.setAttribute('class', "item active")
        } else {
            item.setAttribute('class', "item");
        }

        let img = document.createElement('img');
        img.setAttribute('src', imgCsvName[i]);
        img.setAttribute('class', "slide-image");
        img.setAttribute('alt', "");
        item.appendChild(img);
        // console.log(item);
        carousel_inner.appendChild(item);
    }
}

function fillEditableContentDetailInfo(data) {

    document.getElementById("airfoilName").setAttribute("value", data.name);
    document.getElementById("shortName").setAttribute("value", data.shortName);
    document.getElementById("description").setAttribute("value", data.description);
    let img_detail = document.getElementById("img_detail");
    let img = document.createElement('img');
    img.setAttribute("src", data.image);
    img_detail.appendChild(img);

    fillEditableContentDetailInfoEditableTable(data);

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
            url: rootUrl + "/rest/updateGraf/" + id,
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

let number = 0;
function fillEditableContentDetailInfoEditableTable(data) {
    let tablesView = document.createElement("div");
    tablesView.id = 'tablesView';
    let viewCsv = data.coordView;
    let rows = viewCsv.split('\n');


    jQuery('#viewTab').tabularInput({
        'rows': rows.length - 1,
        'columns': 2,
        'minRows': 10,
        'newRowOnTab': true,
        'columnHeads': ['x', 'y'],
        'name': 'view',
        'animate': true
    });
    for (let i = 0; i < rows.length - 1; i++) {
        let item = rows[i].split(',');
        document.getElementsByName('view[0][' + (i + 1) + ']')[0].setAttribute('value', item[0]);
        document.getElementsByName('view[1][' + (i + 1) + ']')[0].setAttribute('value', item[1]);
    }
    let coordinates = data.coordinates;
    number = coordinates.length;
    for (let i = 0; i < number; i++) {
        (function (i) {
            setTimeout(function () {
                let tabular = document.createElement('div');
                tabular.id = 'tabular' + i;
                let tableDiv = document.createElement('div');
                tableDiv.setAttribute("class", 'example');
                let btn = document.createElement('input');
                btn.setAttribute("type", 'button');
                btn.setAttribute("class", 'btn btn-default');
                btn.setAttribute("value", 'Add New Row');
                btn.setAttribute("onClick", 'javascript:$("#tabular' + i + '").tabularInput("addRow")');
                let btn2 = document.createElement('input');
                btn2.setAttribute("type", 'button');
                btn2.setAttribute("class", 'btn btn-default');
                btn2.setAttribute("value", 'Delete Last Row');
                btn2.setAttribute("onClick", 'javascript:$("#tabular' + i + '").tabularInput("deleteRow")');
                document.getElementById('editableTables').appendChild(btn);
                document.getElementById('editableTables').appendChild(btn2);
                let coordinatesItem = coordinates[i];


                let tableCsv = document.createElement('div');
                tableCsv.id = 'tableCsv' + i;


                let Reynolds_number = createLabel('Reynolds_number', "Reynolds number", i, coordinatesItem.renolgs);
                let Ncrit = createLabel('Ncrit', "Ncrit", i, coordinatesItem.nCrit);
                let Mach = createLabel('Mach', "Mach", i, "Mach");
                let MaxClCd = createLabel('MaxClCd', "Max Cl/Cd", i, coordinatesItem.maxClCd);
                let MaxClCdalpha = createLabel('MaxClCdalpha', "Max Cl/Cd alpha", i, "MaxClCdalpha");

                document.getElementById('editableTables').appendChild(Reynolds_number);
                document.getElementById('editableTables').appendChild(Ncrit);
                document.getElementById('editableTables').appendChild(Mach);
                document.getElementById('editableTables').appendChild(MaxClCd);
                document.getElementById('editableTables').appendChild(MaxClCdalpha);

                let coordinatesJson = coordinatesItem.coordinatesJson.split('\n');


                document.getElementById('editableTables').appendChild(tabular);
                jQuery('#tabular' + i).tabularInput({
                    'rows': coordinatesJson.length - 12,
                    'columns': 7,
                    'minRows': 10,
                    'newRowOnTab': true,
                    'columnHeads': ['alpha', 'CL', 'CD', 'CDp', 'CM', 'Top_Xtr', 'Bot_Xtr'],
                    'name': 'tabular' + i,
                    'animate': true
                });
                document.getElementById('input_Mach' + i).setAttribute('value', coordinatesJson[5].split(',')[1]);
                document.getElementById('input_MaxClCdalpha' + i).setAttribute('value', coordinatesJson[7].split(',')[1]);

                for (let j = 11, l = 1; j < coordinatesJson.length - 1; j++, l++) {
                    let items = coordinatesJson[j].split(',');
                    for (let k = 0; k < items.length; k++) {
                        document.getElementsByName('tabular' + i + '[' + k + '][' + l + ']')[0].setAttribute('value', items[k]);
                    }
                }


            }, 0);
        })(i);

    }
}


function createLabel(id, value, number, valueInput) {
    let label = document.createElement('label');
    label.id = id + number;
    label.innerText = value;
    let input = document.createElement('input');
    input.id = 'input_' + id + number;
    input.setAttribute('type', 'text');
    input.setAttribute('value', valueInput);
    label.appendChild(input);
    label.appendChild(document.createElement('Br'));
    return label;
}


function updateWab() {
    let resultCSVList = [];
    for (let i = 0; i < number; i++) {
        let resultCSV = "Xfoil polar. Reynolds number fixed. Mach  number fixed\n";
        let airfoilName = document.getElementById('shortName').value;
        let Reynolds_number = document.getElementById('input_Reynolds_number' + i).value;
        let fileName = "xf-" + airfoilName + "-" + Reynolds_number;
        resultCSV += "Polar key," + fileName + "\n";
        resultCSV += "Airfoil," + airfoilName + "\n";
        resultCSV += "Reynolds number," + Reynolds_number + "\n";
        resultCSV += "Ncrit," + document.getElementById('input_Ncrit' + i).value + "\n";
        resultCSV += "Mach," + document.getElementById('input_Mach' + i).value + "\n";
        resultCSV += "Max Cl/Cd," + document.getElementById('input_MaxClCd' + i).value + "\n";
        resultCSV += "Max Cl/Cd alpha," + document.getElementById('input_MaxClCdalpha' + i).value + "\n";
        resultCSV += "Url,url" + "\n";
        resultCSV += "\n";
        resultCSV += 'Alpha,Cl,Cd,CDp,Cm,Top_Xtr,Bot_Xtr\n';


        function tableLength(i) {
            return document.getElementById(i).getElementsByTagName('tbody')[0].childElementCount
        }

        function getTableItem(i, j, number, name) {
            return document.getElementsByName(name + number + "[" + i + "][" + j + "]")[0].value;
        }

        for (let j = 1; j <= tableLength('tabular' + i); j++) {
            for (let k = 0; k < 7; k++) {
                resultCSV += getTableItem(k, j, i, "tabular");
                if (k != 6) {
                    resultCSV += ',';
                }
            }
            resultCSV += '\n';
        }
        let resultCsvObj = {};
        resultCsvObj["fileName"] = fileName + document.getElementById('input_Ncrit' + i).value;
        resultCsvObj["data"] = resultCSV;
        resultCsvObj["reynolds"] = Reynolds_number;
        resultCsvObj["nCrit"] = document.getElementById('input_Ncrit' + i).value;
        resultCsvObj["maxClCd"] = document.getElementById('input_MaxClCd' + i).value;


        //console.log(resultCSV);
        resultCSVList[i] = resultCsvObj;
    }

    let viewCsv = "";
    for (let j = 1; j <= tableLength('viewTab'); j++) {
        for (let k = 0; k < 2; k++) {
            viewCsv += getTableItem(k, j, '', "view");
            if (k != 1) {
                viewCsv += ',';
            }
        }
        viewCsv += '\n';
    }


    let data = {};
    data["airfoilName"] = $("#airfoilName").val();
    data["shortName"] = $("#shortName").val();
    data["details"] = $("#description").val();
    data["data"] = resultCSVList;
    data["viewCsv"] = viewCsv;
    console.log(data);
    $(document).ready(function () {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: rootUrl + "/rest/write/updateAirfoilStringCsv",
            data: JSON.stringify(data),
            dataType: 'json',
            timeout: 600000
        }).then(function (data) {
            console.log(data);
            alert(data.message);
        });
    });
}