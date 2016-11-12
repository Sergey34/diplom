function getDetailInfo() {

    var id = $.getUrlVar('airfoilId');
    if (id == undefined || id == '') {
        self.close()
    } else {
        console.log('id = ' + id);
        $(document).ready(function () {
            $.ajax({
                url: "/rest/getDetailInfo/" + id
            }).then(function (data) {
                console.log(data);
                if (data.error != true) {
                    console.log(data.id);
                    console.log("картинки");
                    console.log("/resources/chartTemp/" + id + "ClCd.png");
                    console.log("/resources/chartTemp/" + id + "ClAlpha.png");
                    console.log("/resources/chartTemp/" + id + "CdAlpha.png");
                    console.log("/resources/chartTemp/" + id + "CmAlpha.png");
                    console.log("/resources/chartTemp/" + id + "ClCdAlpha.png");
                }
            });
        });
    }
}