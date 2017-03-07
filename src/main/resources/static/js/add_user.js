function someFunc() {
    document.getElementById('error').style.display = "none";
    document.getElementById('success').style.display = "none";
    if ($("#name").val() != "" && $("#password").val().length > 4 && $("#password").val() == $("#password2").val()) {
        var data = {};
        data["name"] = $("#name").val();
        data["password"] = $("#password").val();
        data["role"] = $("#role").val();
        console.log(data);
        $(document).ready(function () {
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/rest/write/user",
                data: JSON.stringify(data),
                dataType: 'json',
                timeout: 600000,
                error: function (e) {
                    console.log("ERROR: ", e);
                },
                success: function (data) {
                    console.log("SUCCESS: ", data);
                    alert(data.message);

                    document.getElementById('name').style.backgroundColor = '';
                    document.getElementById('password').style.backgroundColor = '';
                    document.getElementById('password2').style.backgroundColor = '';
                }
            });
        });
    } else {
        console.log("некорректные данные");
        alert("некорректные данные! пароль должен быть > 4 символов")
    }
}
