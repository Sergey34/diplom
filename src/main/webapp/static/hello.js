$(document).ready(function () {
    $.ajax({
        url: "/rest/geta"
    }).then(function (data) {
        $('.greeting-id').append(data.group);
        $('.greeting-content').append(data.tuesday);
    });
});
