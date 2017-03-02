function search() {
    let searchTemplate = $("#searchTemplate").val();
    console.log(searchTemplate);
    $(document).ready(function () {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/rest/searchByShortNameLike/" + searchTemplate,
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
