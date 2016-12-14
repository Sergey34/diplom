function updateDB() {
    $(document).ready(function () {
        $.ajax({
            url: "/rest/write/init"
        }).then(function (data) {
            console.log(data);
        });
    });
}
