function refreshFrame() {
    console.log("refreshFrame");
    var id = document.getElementById('id').innerHTML;
    var actions = document.getElementsByName("activ");
    var checkeds = new Array(actions.length);
    for (var i = 0; i < actions.length; i++) {
        if (actions[i].checked) {
            checkeds[i] = actions[i].getAttribute('filename');
        }
    }
    console.log(checkeds);

    function cleanImgFrame() {
        var images = document.getElementById("images");
        images.removeChild(document.getElementById("carousel-inner"));

        var div = document.createElement('div');
        div.id = "carousel-inner";
        div.setAttribute('class', 'carousel-inner');
        images.appendChild(div);
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
                var carousel_inner = document.getElementById('carousel-inner');
                for (var i = 0; i < data.length; i++) {
                    var item = document.createElement('div');
                    if (i === 0) {
                        item.setAttribute('class', "item active")
                    } else {
                        item.setAttribute('class', "item");
                    }
                    var img = document.createElement('img');
                    img.setAttribute('src', data[i] + "?" + Math.random());
                    img.setAttribute('class', "slide-image");
                    img.setAttribute('alt', "");
                    item.appendChild(img);
                    carousel_inner.appendChild(item);
                }
            }
        });
    });
}
