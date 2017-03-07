let id;
function getDetailInfo(forEdit) {
    id = $.getUrlVar('airfoilId');
    if (id == undefined || id == '') {
        self.close()
    } else {
        console.log('id = ' + id);
        $(document).ready(function () {
            $.ajax({
                url: "/rest/airfoilDto/" + id
            }).then(function (data) {
                console.log(data);
                if (data == '') {
                    document.getElementById('airfoilDetailInfo').innerHTML = "Airfoil не найден";
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

    var edit = document.getElementById('edit_link');
    edit.setAttribute("href", "adminka/edit_airfoil.html?airfoilId=" + id);

    var name = document.getElementById('airfoil_name');
    name.innerHTML = data.name;

    //<p class="pull-left"><a id="downloadStl" class="list-group-item" href="">Скачать STL</a></p>
    var files = data.stlFilePath;
    for (let i = 0; i < files.length; i++) {
        var p = document.createElement('p');
        p.setAttribute("class", "pull-left");
        var a = document.createElement('a');
        a.setAttribute("class", "list-group-item");
        a.setAttribute("href", files[i]);
        a.innerHTML = "Скачать STL";
        p.appendChild(a);
        document.getElementById("bottoms").appendChild(p);
    }

    // var downloadStl = document.getElementById('downloadStl');
    // downloadStl.setAttribute("href", data.stlFilePath);

    var image = document.getElementById('imgDetail');
    image.setAttribute("src", data.image);

    var description = document.getElementById('descr_detail');
    description.innerHTML = data.description;

    var Polars_for = document.getElementById('Polars_for');
    Polars_for.innerHTML = Polars_for.textContent + data.name;

    var characteristics = data.characteristics;


    var table = document.getElementById('table1');
    for (let i = 0, j = 1; i < characteristics.length; i++, j++) {

        var tr = document.createElement('tr');
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
        var td = document.createElement('td');
        var checkbox = document.createElement('input');
        checkbox.setAttribute('type', 'checkbox');
        checkbox.setAttribute('name', 'activ');
        checkbox.setAttribute('checked', 'checked');
        checkbox.setAttribute('filename', characteristics[i].fileName);
        checkbox.id = 'checkbox' + j;
        td.appendChild(checkbox);
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "Renolds" + j;
        td.innerHTML = characteristics[i].renolgs;
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "NCrit" + j;
        td.innerHTML = characteristics[i].nCrit;
        tr.appendChild(td);

        td = document.createElement('td');
        td.id = "MaxClCd" + j;
        td.innerHTML = characteristics[i].maxClCd;
        tr.appendChild(td);

        td = document.createElement('td');
        var link_file = document.createElement('a');
        link_file.id = "link_file" + j;
        link_file.innerHTML = characteristics[i].fileName;
        link_file.setAttribute('href', characteristics[i].filePath);
        td.appendChild(link_file);
        tr.appendChild(td);

        table.appendChild(tr);
    }

    var imgCsvName = data.imgCsvName;
    var carousel_indicators = document.getElementById('carousel-indicators');
    var carousel_inner = document.getElementById('carousel-inner');
    for (let i = 0; i < imgCsvName.length; i++) {
        // <li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>

        var li = document.createElement('li');
        li.setAttribute('data-target', "#carousel-example-generic");
        li.setAttribute('data-slide-to', i + '');
        if (i == 0) {
            li.setAttribute('class', 'active');
        }
        carousel_indicators.appendChild(li);

        var item = document.createElement('div');
        if (i == 0) {
            item.setAttribute('class', "item active")
        } else {
            item.setAttribute('class', "item");
        }

        var img = document.createElement('img');
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
    var img_detail = document.getElementById("img_detail");
    var img = document.createElement('img');
    img.setAttribute("src", data.image);
    img_detail.appendChild(img);

    fillEditableContentDetailInfoEditableTable(data);

    var characteristics = data.characteristics;
    var links = document.getElementById('graf');
    for (let i = 0; i < characteristics.length; i++) {
        var linkItem = document.createElement('a');
        linkItem.setAttribute("href", characteristics[i].filePath);
        linkItem.innerHTML = characteristics[i].fileName;
        links.appendChild(linkItem);
        links.appendChild(document.createElement('br'))
    }
}

function refreshiframe() {
    console.log("refreshiframe");
    var actions = document.getElementsByName("activ");
    var checkeds = new Array(10);
    for (let i = 0; i < actions.length; i++) {
        if (actions[i].checked) {
            checkeds[i] = actions[i].getAttribute('filename');
        }
    }
    console.log(checkeds);

    function cleanImgFrame() {
        var carousel_example_generic = document.getElementById("carousel-example-generic");
        carousel_example_generic.removeChild(document.getElementById("carousel-indicators"));
        carousel_example_generic.removeChild(document.getElementById("carousel-inner"));
        carousel_example_generic.removeChild(document.getElementById("a1"));
        carousel_example_generic.removeChild(document.getElementById("a2"));

        var ol = document.createElement('ol');
        ol.id = 'carousel-indicators';
        ol.setAttribute('class', 'carousel-indicators');
        carousel_example_generic.appendChild(ol);

        var div = document.createElement('div');
        div.id = "carousel-inner";
        div.setAttribute('class', 'carousel-inner');
        carousel_example_generic.appendChild(div);


        var a1 = document.createElement("a");
        a1.innerHTML = "<span class='glyphicon glyphicon-chevron-left'></span>";
        a1.setAttribute("href", "#carousel-example-generic");
        a1.setAttribute("class", "left carousel-control");
        a1.setAttribute("data-slide", "prev");
        a1.setAttribute("style", "background:none !important");
        a1.id = "a1";


        var a2 = document.createElement("a");
        a2.innerHTML = "<span class='glyphicon glyphicon-chevron-right'></span>";
        a2.setAttribute("href", "#carousel-example-generic");
        a2.setAttribute("class", "right carousel-control");
        a2.setAttribute("data-slide", "next");
        a2.setAttribute("style", "background:none !important");
        a2.id = "a2";

        carousel_example_generic.appendChild(a1);
        carousel_example_generic.appendChild(a2);
    }

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
                cleanImgFrame();


                var carousel_indicators = document.getElementById('carousel-indicators');
                var carousel_inner = document.getElementById('carousel-inner');
                for (let i = 0; i < data.length; i++) {
                    // <li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>

                    var li = document.createElement('li');
                    li.setAttribute('data-target', "#carousel-example-generic");
                    li.setAttribute('data-slide-to', i + '');
                    if (i == 0) {
                        li.setAttribute('class', 'active');
                    }
                    carousel_indicators.appendChild(li);

                    var item = document.createElement('div');
                    if (i == 0) {
                        item.setAttribute('class', "item active")
                    } else {
                        item.setAttribute('class', "item");
                    }

                    var img = document.createElement('img');
                    img.setAttribute('src', data[i] + "?" + Math.random());
                    img.setAttribute('class', "slide-image");
                    img.setAttribute('alt', "");
                    item.appendChild(img);
                    // console.log(item);
                    carousel_inner.appendChild(item);
                }
            }
        });
    });
}

let number = 0;
function fillEditableContentDetailInfoEditableTable(data) {
    var tablesView = document.createElement("div");
    tablesView.id = 'tablesView';
    var viewCsv = data.coordView;
    var rows = viewCsv.split('\n');


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
        var item = rows[i].split(',');
        document.getElementsByName('view[0][' + (i + 1) + ']')[0].setAttribute('value', item[0]);
        document.getElementsByName('view[1][' + (i + 1) + ']')[0].setAttribute('value', item[1]);
    }
    var characteristics = data.characteristics;
    number = characteristics.length;
    for (let i = 0; i < number; i++) {
        (function (i) {
            setTimeout(function () {
                var tabular = document.createElement('div');
                tabular.id = 'tabular' + i;
                var tableDiv = document.createElement('div');
                tableDiv.setAttribute("class", 'example');
                var btn = document.createElement('input');
                btn.setAttribute("type", 'button');
                btn.setAttribute("class", 'btn btn-default');
                btn.setAttribute("value", 'Add New Row');
                btn.setAttribute("onClick", 'javascript:$("#tabular' + i + '").tabularInput("addRow")');
                var btn2 = document.createElement('input');
                btn2.setAttribute("type", 'button');
                btn2.setAttribute("class", 'btn btn-default');
                btn2.setAttribute("value", 'Delete Last Row');
                btn2.setAttribute("onClick", 'javascript:$("#tabular' + i + '").tabularInput("deleteRow")');
                document.getElementById('editableTables').appendChild(btn);
                document.getElementById('editableTables').appendChild(btn2);
                var characteristic = characteristics[i];


                var tableCsv = document.createElement('div');
                tableCsv.id = 'tableCsv' + i;


                var Reynolds_number = createLabel('Reynolds_number', "Reynolds number", i, characteristic.renolgs);
                var Ncrit = createLabel('Ncrit', "Ncrit", i, characteristic.nCrit);
                var Mach = createLabel('Mach', "Mach", i, "Mach");
                var MaxClCd = createLabel('MaxClCd', "Max Cl/Cd", i, characteristic.maxClCd);
                var MaxClCdalpha = createLabel('MaxClCdalpha', "Max Cl/Cd alpha", i, "MaxClCdalpha");

                document.getElementById('editableTables').appendChild(Reynolds_number);
                document.getElementById('editableTables').appendChild(Ncrit);
                document.getElementById('editableTables').appendChild(Mach);
                document.getElementById('editableTables').appendChild(MaxClCd);
                document.getElementById('editableTables').appendChild(MaxClCdalpha);

                var coordinatesStl = characteristic.coordinatesStl.split('\n');


                document.getElementById('editableTables').appendChild(tabular);
                jQuery('#tabular' + i).tabularInput({
                    'rows': coordinatesStl.length - 12,
                    'columns': 7,
                    'minRows': 10,
                    'newRowOnTab': true,
                    'columnHeads': ['alpha', 'CL', 'CD', 'CDp', 'CM', 'Top_Xtr', 'Bot_Xtr'],
                    'name': 'tabular' + i,
                    'animate': true
                });
                document.getElementById('input_Mach' + i).setAttribute('value', coordinatesStl[5].split(',')[1]);
                document.getElementById('input_MaxClCdalpha' + i).setAttribute('value', coordinatesStl[7].split(',')[1]);

                for (let j = 11, l = 1; j < coordinatesStl.length - 1; j++, l++) {
                    var items = coordinatesStl[j].split(',');
                    for (let k = 0; k < items.length; k++) {
                        document.getElementsByName('tabular' + i + '[' + k + '][' + l + ']')[0].setAttribute('value', items[k]);
                    }
                }


            }, 0);
        })(i);

    }
}


function createLabel(id, value, number, valueInput) {
    var label = document.createElement('label');
    label.id = id + number;
    label.innerHTML = value;
    var input = document.createElement('input');
    input.id = 'input_' + id + number;
    input.setAttribute('type', 'text');
    input.setAttribute('value', valueInput);
    label.appendChild(input);
    label.appendChild(document.createElement('Br'));
    return label;
}


function tableLength(i) {
    return document.getElementById(i).getElementsByTagName('tbody')[0].childElementCount
}
function getTableItem(i, j, number, name) {
    return document.getElementsByName(name + number + "[" + i + "][" + j + "]")[0].value;
}

function updateWab() {
    var resultCSVList = [];
    for (let i = 0; i < number; i++) {
        var resultCSV = "Xfoil polar. Reynolds number fixed. Mach  number fixed\n";
        var airfoilName = document.getElementById('shortName').value;
        var Reynolds_number = document.getElementById('input_Reynolds_number' + i).value;
        var fileName = "xf-" + airfoilName + "-" + Reynolds_number;
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


        for (let j = 1; j <= tableLength('tabular' + i); j++) {
            for (let k = 0; k < 7; k++) {
                var value = getTableItem(k, j, i, "tabular");
                if (value == '') {
                    alert("Ошибка! таблица заполнена не корректно");
                    return;
                }
                resultCSV += value;
                if (k != 6) {
                    resultCSV += ',';
                }
            }
            resultCSV += '\n';
        }
        var resultCsvObj = {};
        resultCsvObj["fileName"] = fileName + document.getElementById('input_Ncrit' + i).value;
        resultCsvObj["data"] = resultCSV;
        resultCsvObj["reynolds"] = Reynolds_number;
        resultCsvObj["nCrit"] = document.getElementById('input_Ncrit' + i).value;
        resultCsvObj["maxClCd"] = document.getElementById('input_MaxClCd' + i).value;


        //console.log(resultCSV);
        resultCSVList[i] = resultCsvObj;
    }

    var viewCsv = "";
    for (let j = 1; j <= tableLength('viewTab'); j++) {
        for (let k = 0; k < 2; k++) {
            var value = getTableItem(k, j, '', "view");
            if (value == '') {
                alert("Ошибка! таблица заполнена не корректно");
                return;
            }
            viewCsv += value;
            if (k != 1) {
                viewCsv += ',';
            }
        }
        viewCsv += '\n';
    }


    var data = {};
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
            url: "/rest/write/updateAirfoilStringCsv",
            data: JSON.stringify(data),
            dataType: 'json',
            timeout: 600000
        }).then(function (data) {
            console.log(data);
            alert(data.message);
        });
    });
}