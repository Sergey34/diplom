function refreshiframe() {
    console.log("refreshiframe");
    var id = document.getElementById('id').innerHTML;
    console.log();
    var actions = document.getElementsByName("activ");
    var checkeds = new Array(10);
    for (var i = 0; i < actions.length; i++) {
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
            url: "/rest/updateGraph/" + id,
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
                for (var i = 0; i < data.length; i++) {
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
