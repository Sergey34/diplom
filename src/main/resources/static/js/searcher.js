function search() {
    var searchTemplate = $("#searchTemplate").val();
    window.location.replace("/airfoilList.html?st=" + searchTemplate);
}
function searchFull() {
    var renolds = $("#value0").val();
    var maxClCd = $("#value1").val();
    var ncrit = $("#value2").val();
    var n = 0;
    if (renolds != "" && renolds != undefined) {
        n++;
    }
    if (ncrit != "" && ncrit != undefined) {
        n++;
    }
    if (maxClCd != "" && maxClCd != undefined) {
        n++;
    }
    var short_name = $("#short_name").val();
    // window.location.replace("/airfoilList.html?st="+searchTemplate);

    console.log("n= " + n);
    var array = new Array(n);
    for (var i = 0; i < 3 && n != 0; i++) {
        let val = $("#value" + i).val();
        console.log("val" + val);
        if (val != undefined && val != "") {
            console.log("add");
            var item = {};
            item["action"] = $("#select" + i).val();
            item["attrName"] = document.getElementById("label" + i).getAttribute("attrName");
            item["value"] = val;
            array[n - 1] = item;
            n--;
        }
    }


    console.log(array);
    console.log(short_name);
    $(document).ready(function () {

        var data2 = JSON.stringify(array);
        console.log(data2);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/rest/searchAirfoil/" + short_name,
            data: data2,
            dataType: 'json',
            timeout: 600000,
            error: function (e) {
                console.log("ERROR: ", e);
            },
            success: function (data) {
                console.log("SUCCESS: ", data);

            }
        });
    });
}


