function edit(id) {

    var edit_btn = document.getElementById("edit_btn_" + id);
    var is_editable = edit_btn.value == "Редактировать";
    console.log(id);


    var name = document.getElementById("name_" + id);
    var descr = document.getElementById("airfoil_description_" + id);
    var link = document.getElementById("link_" + id);

    name.setAttribute('contentEditable', is_editable);
    descr.setAttribute('contentEditable', is_editable);
    link.setAttribute('contentEditable', is_editable);

    var div_input = document.getElementById("div_input_" + id);
    if (is_editable) {
        edit_btn.value = "Отменить";
        console.log(div_input);
        var but = document.createElement('input');
        but.type = 'button';
        but.setAttribute('onclick', 'save(' + id + ');');
        but.value = "Сохранить";
        but.id = "btn_save_" + id;
        div_input.appendChild(but); // отобразить кнопку сохранить
        name.style.outline = '2px solid #000';
        descr.style.outline = '2px solid #000';
        link.style.outline = '2px solid #000';
    } else {
        document.getElementById("contentid").removeChild(document.getElementById("airfoil_list"));
        getContent(currentPrefix);//todo оптимизировать - сходить на сервер только за измененным профилем
        /*edit_btn.value = "Редактировать";
         div_input.removeChild(document.getElementById("btn_save_" + id));
         name.style.outline = '';
         descr.style.outline = '';
         link.style.outline = '';*/
    }
}

function save(id) {
    console.log("save");
    console.log(id);
    var name = document.getElementById('name_' + id).innerHTML;
    var descr = document.getElementById('airfoil_description_' + id).innerHTML;
    var link = document.getElementById('link_' + id).innerHTML;
    var image = document.getElementById('airfoil_image_' + id).innerHTML;
    var prefix = document.getElementById('prefix_' + id).innerHTML;


    var data = {};
    data["id"] = id;
    data["name"] = name;
    data["description"] = descr;
    data["link"] = link;
    data["image"] = image;
    data["prefix"] = prefix;
    console.log(data);


    $(document).ready(function () {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/rest/write/editAirfoil",
            data: JSON.stringify(data),
            dataType: 'json',
            timeout: 600000,
            error: function (e) {
                console.log("ERROR: ", e);
                //document.getElementById('error').style.display = "";
            },
            success: function (data) {
                console.log("SUCCESS: ", data);
                //document.getElementById('success').style.display = "";
            }
        });
    });
}