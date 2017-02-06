function updateDB() {
    $(document).ready(function () {
        $.ajax({
            url: rootUrl + "/rest/write/init"
        }).then(function (data) {
            console.log(data);
        });
    });
}
function stopUpdate() {
    console.log('stopUpdate');
    $(document).ready(function () {
        $.ajax({
            url: rootUrl + "/rest/write/stop"
        }).then(function (data) {
            console.log(data);
            alert(data.message);
        });
    });
}
