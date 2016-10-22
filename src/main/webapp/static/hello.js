$(document).ready(function () {
    $.ajax({
        url: "/rest/geta",
        /*headers: {
         "Authorization": "Basic " + btoa(username + ":" + password)
         }*/
    }).then(function (data) {
        $('.greeting-id').append(data.group);
        $('.greeting-content').append(data.tuesday);
    });
});
