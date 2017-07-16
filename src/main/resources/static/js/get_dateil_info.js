var id;
function getDetailInfo(forEdit) {
    id = document.getElementById("shortName").value;

    console.log('id = ' + id);
    $(document).ready(function () {
        $.ajax({
            url: "/rest/airfoilDto/" + id
        }).then(function (data) {
            console.log(data);
            if (data == '') {
                document.getElementById('airfoilDetailInfo').innerHTML = "Airfoil не найден";
            } else {
                fillEditableContentDetailInfoEditabvarable(data);
            }
        });
    });
}

var number = 0;
function fillEditableContentDetailInfoEditabvarable(data) {
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
    for (var i = 0; i < rows.length - 1; i++) {
        var item = rows[i].split(',');
        document.getElementsByName('view[0][' + (i + 1) + ']')[0].setAttribute('value', item[0]);
        document.getElementsByName('view[1][' + (i + 1) + ']')[0].setAttribute('value', item[1]);
    }
    var characteristics = data.characteristics;
    number = characteristics.length;
    for (var i = 0; i < number; i++) {
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
                btn2.setAttribute("value", 'Devare Last Row');
                btn2.setAttribute("onClick", 'javascript:$("#tabular' + i + '").tabularInput("devareRow")');
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

                for (var j = 11, l = 1; j < coordinatesStl.length - 1; j++, l++) {
                    var items = coordinatesStl[j].split(',');
                    for (var k = 0; k < items.length; k++) {
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
