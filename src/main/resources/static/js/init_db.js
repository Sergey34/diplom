function updateDB() {
    $(document).ready(function () {
        $.ajax({
            url: "/init"
        }).then(function (data) {
            console.log(data);
        });
    });
}
function stopUpdate() {
    console.log('stopUpdate');
    $(document).ready(function () {
        $.ajax({
            url: "/stop"
        }).then(function (data) {
            console.log(data);
            alert(data.message);
        });
    });
}
