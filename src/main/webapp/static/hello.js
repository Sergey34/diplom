$(document).ready(function () {
    $.ajax({
        url: "/rest/geta",
        data: {'Authorization': 'Basic YWxleDoxMjM0NTY='},
    }).then(function (data) {
        $('.greeting-id').append(data.group);
        $('.greeting-content').append(data.tuesday);
    });
});
