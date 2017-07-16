var number = 0;
function saveWab() {
    var resultCSVList = [];
    for (var i = 0; i < number; i++) {
        var resultCSV = "Xfoil polar. Reynolds number fixed. Mach  number fixed\n";
        var airfoilName = document.getElementById('ShortName').value;
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


        var table = document.getElementById('tabular' + i).getElementsByTagName('*');
        for (var j = 1; j <= tableLength('tabular' + i); j++) {
            for (var k = 0; k < 7; k++) {
                var value = getTableItem(k, j, table);
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
        resultCsvObj["fileName"] = fileName;
        resultCsvObj["data"] = resultCSV;
        resultCsvObj["reynolds"] = Reynolds_number;
        resultCsvObj["nCrit"] = document.getElementById('input_Ncrit' + i).value;
        resultCsvObj["maxClCd"] = document.getElementById('input_MaxClCd' + i).value;


        console.log(resultCSV);
        resultCSVList[i] = resultCsvObj;
    }
    var tableView = document.getElementById('viewTab').getElementsByTagName('*');
    var viewCsv = "";
    for (var j = 1; j <= tableLength('viewTab'); j++) {
        for (var k = 0; k < 2; k++) {
            var value = getTableItem(k, j, tableView);
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
    data["shortName"] = $("#ShortName").val();
    data["details"] = $("#Details").val();
    data["data"] = resultCSVList;
    data["viewCsv"] = viewCsv;
    console.log(data);
    $(document).ready(function () {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/rest/write/addAirfoilForStringCsv",
            data: JSON.stringify(data),
            dataType: 'json',
            timeout: 600000
        }).then(function (data) {
            console.log(data);
            alert(data.message);
        });
    });
}
function getTableItem(i, j, table) {
    return table['[' + i + '][' + j + ']'].value;
}
function tableLength(i) {
    return document.getElementById(i).getElementsByTagName('tbody')[0].childElementCount
}


//создание таблиц

function createLabel(id, value) {
    var label = document.createElement('label');
    label.id = id + number;
    label.innerHTML = value;
    var input = document.createElement('input');
    input.id = 'input_' + id + number;
    input.setAttribute('type', 'text');
    label.appendChild(input);
    label.appendChild(document.createElement('Br'));
    return label;
}
function addTable() {

    var Reynolds_number = createLabel('Reynolds_number', "Reynolds number");
    var Ncrit = createLabel('Ncrit', "Ncrit");
    var Mach = createLabel('Mach', "Mach");
    var MaxClCd = createLabel('MaxClCd', "Max Cl/Cd");
    var MaxClCdalpha = createLabel('MaxClCdalpha', "Max Cl/Cd alpha");


    var tableDiv = document.createElement('div');
    tableDiv.setAttribute("class", 'example');
    var btn = document.createElement('input');
    btn.setAttribute("type", 'button');
    btn.setAttribute("class", 'btn btn-default');
    btn.setAttribute("value", 'Add New Row');
    btn.setAttribute("onClick", 'javascript:$("#tabular' + number + '").tabularInput("addRow")');
    var btn2 = document.createElement('input');
    btn2.setAttribute("type", 'button');
    btn2.setAttribute("class", 'btn btn-default');
    btn2.setAttribute("value", 'Devare Last Row');
    btn2.setAttribute("onClick", 'javascript:$("#tabular' + number + '").tabularInput("devareRow")');

    var table = document.createElement('div');
    table.id = 'tabular' + number;

    tableDiv.appendChild(Reynolds_number);
    tableDiv.appendChild(Ncrit);
    tableDiv.appendChild(Mach);
    tableDiv.appendChild(MaxClCd);
    tableDiv.appendChild(MaxClCdalpha);
    tableDiv.appendChild(btn);
    tableDiv.appendChild(btn2);
    tableDiv.appendChild(document.createElement('Br'));
    tableDiv.appendChild(table);

    var addAirfoilWeb = document.getElementById('addAirfoilWeb');
    addAirfoilWeb.appendChild(tableDiv);
    jQuery('#tabular' + number).tabularInput({
        'rows': 10,
        'columns': 7,
        'minRows': 10,
        'newRowOnTab': true,
        'columnHeads': ['alpha', 'CL', 'CD', 'CDp', 'CM', 'Top_Xtr', 'Bot_Xtr'],
        'name': '',
        'animate': true
    });
    number++;

}
