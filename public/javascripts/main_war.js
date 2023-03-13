/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$('#hiddenDownloadAllFile').on('click', function () {
    if (!$(this).is(':checked')) {
        $("#tableCampoFecha").css("display", "table-cell");
        $("#diascampoFecha").removeAttr("disabled");
        $("#fieldDate").val("")
        if (!$('#hiddenCheckedFracView').is(':checked')) {
            $("#fieldOrderBy").removeAttr("disabled");
        }
    } else {
        $("#diascampoFecha").attr("disabled", "disabled");
        if (!$('#hiddenCheckedFracView').is(':checked'))
        {
            $("#fieldOrderBy").val("");
            $("#fieldOrderBy").attr("disabled", "disabled");
        }
        $("#tableCampoFecha").css("display", "none");
    }
});
$('#hiddenCheckedDate').on('click', function () {
    if ($(this).is(':checked')) {
        $("#date1").attr("disabled", "disabled");
        $("#date1").val("");
        $("#date2").attr("disabled", "disabled");
        $("#date2").val("");
        $("#hiddenChecked").val("true");
        $("#tableCampoFecha").css("display", "none");
        $("#nametableCampoFecha").css("display", "none");
        $("#fieldDate").val("");
        $("#hiddenDownloadAllFile").removeAttr("disabled");
        $("#hiddenDownloadAllFile").val("true");
        $("#fieldDate").val("");
    } else {
        $("#date1").removeAttr("disabled");
        $("#date2").removeAttr("disabled");
//        $("#fieldDate").removeAttr("disabled");
        $("#diascampoFecha").attr("disabled", "disabled");
        $("#hiddenDownloadAllFile").attr("disabled", "disabled");
        $("#tableCampoFecha").css("display", "table-cell");
        $("#nametableCampoFecha").css("display", "block");
        $("#fieldDate").val("");
        $("#hiddenChecked").val("false");
    }
});

$('#CheckedEditDate').on('click', function () {
    if ($(this).is(':checked')) {
        $("#tableCampoFecha").css("display", "none");
        $("#date1Edit").attr("disabled", "disabled");
        $("#date1Edit").val("");
        $("#date2Edit").attr("disabled", "disabled");
        $("#date2Edit").val("");
        $("#fieldDate").val("");
        $("#fieldDateEdit").val("");
        $("#fieldDateEdit").attr("disabled", "disabled");
    } else {
        $("#fieldDateEdit").removeAttr("disabled");
        $("#tableCampoFecha").css("display", "table-cell");
        $("#fieldDate").val("");
        $("#fieldDate").removeAttr("disabled");

    }
});
$('#hiddenCheckedEditDate').on('click', function () {
    if ($(this).is(':checked')) {

        $("#CheckedEditDate").removeAttr("disabled");
        $("#CheckedEditDate").val("true")
//        $("#CheckedEditDate").click()
        $("#date1Edit").attr("disabled", "disabled");
        $("#date1Edit").val("");
        $("#date2Edit").attr("disabled", "disabled");
        $("#date2Edit").val("");
        $("#tableCampoFecha").css("display", "none");
        $("#hiddenCheckedEdit").val("true");
        $("#fieldDateEdit").attr("disabled", "disabled");
        $("#fieldDateEdit").val("")

    } else {
        $("#CheckedEditDate").attr("disabled", "disabled");
        $("#CheckedEditDate").val("false")
//        $("#CheckedEditDate").click()
        $("#date1Edit").removeAttr("disabled");
        $("#date2Edit").removeAttr("disabled");
        $("#hiddenCheckedEdit").val("false");
        $("#fieldDateEdit").attr("disabled", "disabled");
        $("#fieldDateEdit").val("")
        $("#tableCampoFecha").css("display", "table-cell");
        $("#tableCampoFecha").css("width", "80%");

    }
});

$('#checkedDays').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDays").val("true");
        $("#checkedDayWeek").prop("checked", false);
        $("#dayWeekTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayWeek").val("false");
        $("#checkedDayMonth").prop("checked", false);
        $("#dayMonthTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonth").val("false");

        document.getElementById("divProgHoursDB").style.display = "block";
        $("#hourTask").prop('selectedIndex', 0);
        $("#minuteTask").prop('selectedIndex', 0);
        $("#checkedHours").prop("checked", false);
        $("#perHoursTask").val("");
        $("#hiddenCheckedHours").val("false");
        $("#checkedMinutes").prop("checked", false);
        $("#perMinutesTask").val("");
        $("#hiddenCheckedMinutes").val("false");
    } else {
        $("#hiddenCheckedDays").val("false");
    }
});

$('#checkedDayWeek').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDayWeek").val("true");
        $("#checkedDays").prop("checked", false);
        $("#dayTask").val("");
        $("#hiddenCheckedDays").val("false");
        $("#checkedDayMonth").prop("checked", false);
        $("#dayMonthTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonth").val("false");

        document.getElementById("divProgHoursDB").style.display = "block";
        $("#hourTask").prop('selectedIndex', 0);
        $("#minuteTask").prop('selectedIndex', 0);
        $("#checkedHours").prop("checked", false);
        $("#perHoursTask").val("");
        $("#hiddenCheckedHours").val("false");
        $("#checkedMinutes").prop("checked", false);
        $("#perMinutesTask").val("");
        $("#hiddenCheckedMinutes").val("false");
    } else {
        $("#hiddenCheckedDayWeek").val("false");
    }
});

$('#checkedDayMonth').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDayMonth").val("true");
        $("#checkedDays").prop("checked", false);
        $("#dayTask").val("");
        $("#hiddenCheckedDays").val("false");
        $("#checkedDayWeek").prop("checked", false);
        $("#dayWeekTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayWeek").val("false");

        document.getElementById("divProgHoursDB").style.display = "block";
        $("#hourTask").prop('selectedIndex', 0);
        $("#minuteTask").prop('selectedIndex', 0);
        $("#checkedHours").prop("checked", false);
        $("#perHoursTask").val("");
        $("#hiddenCheckedHours").val("false");
        $("#checkedMinutes").prop("checked", false);
        $("#perMinutesTask").val("");
        $("#hiddenCheckedMinutes").val("false");
    } else {
        $("#hiddenCheckedDayWeek").val("false");
    }
});

$('#checkedMinutes').on('click', function () {
    if ($(this).is(':checked')) {
        document.getElementById("divProgHoursDB").style.display = "none";
        $("#hourTask").prop('selectedIndex', 0);
        $("#minuteTask").prop('selectedIndex', 0);

        $("#hiddenCheckedMinutes").val("true");
        $("#checkedHours").prop("checked", false);
        $("#perHoursTask").val("");
        $("#hiddenCheckedHours").val("false");
        $("#checkedDays").prop("checked", false);
        $("#dayTask").val("");
        $("#hiddenCheckedDays").val("false");
        $("#checkedDayWeek").prop("checked", false);
        $("#dayWeekTask").prop('selectedIndex', 0);
        $("#hiddenCheckeDayWeek").val("false");
        $("#checkedDayMonth").prop("checked", false);
        $("#dayMonthTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonth").val("false");
    }
});

$('#checkedHours').on('click', function () {
    if ($(this).is(':checked')) {
        document.getElementById("divProgHoursDB").style.display = "none";
        $("#hourTask").prop('selectedIndex', 0);
        $("#minuteTask").prop('selectedIndex', 0);

        $("#hiddenCheckedHours").val("true");
        $("#checkedMinutes").prop("checked", false);
        $("#perMinutesTask").val("");
        $("#hiddenCheckedMinutes").val("false");
        $("#checkedDays").prop("checked", false);
        $("#dayTask").val("");
        $("#hiddenCheckedDays").val("false");
        $("#checkedDayWeek").prop("checked", false);
        $("#dayWeekTask").prop('selectedIndex', 0);
        $("#hiddenCheckeDayWeek").val("false");
        $("#checkedDayMonth").prop("checked", false);
        $("#dayMonthTask").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonth").val("false");
    }
});

$('#checkedDaysEdit').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDayEdit").val("true");
        $("#checkedDayWeekEdit").prop("checked", false);
        $("#dayWeekEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayWeekEdit").val("false");
        $("#checkedDayMonthEdit").prop("checked", false);
        $("#dayMonthEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonthEdit").val("false");

        document.getElementById("divProgHoursDBEdit").style.display = "block";
        $("#checkedHoursEdit").prop("checked", false);
        $("#perHoursTaskEdit").val("");
        $("#hiddenCheckedHoursEdit").val("false");
        $("#checkedMinutesEdit").prop("checked", false);
        $("#perMinutesTaskEdit").val("");
        $("#hiddenCheckedMinutesEdit").val("false");
    } else {
        $("#hiddenCheckedDayEdit").val("false");
    }
});

$('#checkedDayWeekEdit').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDayWeekEdit").val("true");
        $("#checkedDaysEdit").prop("checked", false);
        $("#dayTaskEdit").val("");
        $("#hiddenCheckedDayEdit").val("false");
        $("#checkedDayMonthEdit").prop("checked", false);
        $("#dayMonthEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonthEdit").val("false");

        document.getElementById("divProgHoursDBEdit").style.display = "block";
        $("#checkedHoursEdit").prop("checked", false);
        $("#perHoursTaskEdit").val("");
        $("#hiddenCheckedHoursEdit").val("false");
        $("#checkedMinutesEdit").prop("checked", false);
        $("#perMinutesTaskEdit").val("");
        $("#hiddenCheckedMinutesEdit").val("false");
    } else {
        $("#hiddenCheckedDayWeekEdit").val("false");
    }
});

$('#checkedDayMonthEdit').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedDayMonthEdit").val("true");
        $("#checkedDaysEdit").prop("checked", false);
        $("#dayTaskEdit").val("");
        $("#hiddenCheckedDayEdit").val("false");
        $("#checkedDayWeekEdit").prop("checked", false);
        $("#dayWeekEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayWeekEdit").val("false");

        document.getElementById("divProgHoursDBEdit").style.display = "block";
        $("#checkedHoursEdit").prop("checked", false);
        $("#perHoursTaskEdit").val("");
        $("#hiddenCheckedHoursEdit").val("false");
        $("#checkedMinutesEdit").prop("checked", false);
        $("#perMinutesTaskEdit").val("");
        $("#hiddenCheckedMinutesEdit").val("false");
    } else {
        $("#hiddenCheckedDayWeekEdit").val("false");
    }
});


$('#checkedMinutesEdit').on('click', function () {
    if ($(this).is(':checked')) {
        document.getElementById("divProgHoursDBEdit").style.display = "none";
        $("#hourTaskEdit").prop('selectedIndex', 0);
        $("#minuteTaskEdit").prop('selectedIndex', 0);

        $("#hiddenCheckedMinutesEdit").val("true");
        $("#checkedHoursEdit").prop("checked", false);
        $("#perHoursTaskEdit").val("");
        $("#hiddenCheckedHoursEdit").val("false");
        $("#checkedDaysEdit").prop("checked", false);
        $("#dayTaskEdit").val("");
        $("#hiddenCheckedDayEdit").val("false");
        $("#checkedDayWeekEdit").prop("checked", false);
        $("#dayWeekEdit").prop('selectedIndex', 0);
        $("#hiddenCheckeDayWeekEdit").val("false");
        $("#checkedDayMonthEdit").prop("checked", false);
        $("#dayMonthEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonthEdit").val("false");
    }
});

$('#checkedHoursEdit').on('click', function () {
    if ($(this).is(':checked')) {
        document.getElementById("divProgHoursDBEdit").style.display = "none";
        $("#hourTaskEdit").prop('selectedIndex', 0);
        $("#minuteTaskEdit").prop('selectedIndex', 0);

        $("#hiddenCheckedHoursEdit").val("true");
        $("#checkedMinutesEdit").prop("checked", false);
        $("#perMinutesTaskEdit").val("");
        $("#hiddenCheckedMinutesEdit").val("false");
        $("#checkedDaysEdit").prop("checked", false);
        $("#dayTaskEdit").val("");
        $("#hiddenCheckedDayEdit").val("false");
        $("#checkedDayWeekEdit").prop("checked", false);
        $("#dayWeekEdit").prop('selectedIndex', 0);
        $("#hiddenCheckeDayWeekEdit").val("false");
        $("#checkedDayMonthEdit").prop("checked", false);
        $("#dayMonthEdit").prop('selectedIndex', 0);
        $("#hiddenCheckedDayMonthEdit").val("false");
    }
});

$('#hiddenCheckedFracView').on('click', function () {
    if ($(this).is(':checked')) {
        $("#numFracView").val("");
        $("#numFracView").removeAttr("disabled");
        $("#fieldOrderBy").val("");
        $("#fieldOrderBy").removeAttr("disabled");
        $("#disFracView").val("true");
    } else {
        $("#numFracView").val("");
        $("#numFracView").attr("disabled", "disabled");
        if ($('#hiddenDownloadAllFile').is(':checked'))
        {
            $("#fieldOrderBy").val("");
            $("#fieldOrderBy").attr("disabled", "disabled");
        }
        $("#disFracView").val("false");
    }
});

$('#checkedCleanCore').on('click', function () {
    if ($(this).is(':checked')) {
        $("#hiddenCheckedCleanCore").val("true");
    } else {
        $("#hiddenCheckedCleanCore").val("false");
    }
});

function clearConections() {

    $("#idEdit").val("");
    $("#nomConnectionEdit").val("");
    $("#separatorBDEdit").val("");
    $("#queryEdit").val("");
    $("#fieldURLEdit").val("");
    $("#date1Edit").val("");
    $("#date2Edit").val("");
    $("#fieldDateEdit").val("");
    $("#fieldDate").val("");
    $("#search_results_campoFecha_0").empty();
    $("#hiddenCheckedEdit").val("false");
    $("#CheckedEditDate").prop("checked", false);
    $("#hiddenDownloadAllFile").val("false");
    $("#hiddenCheckedEditDate").prop("checked", true);
    $("#date1Edit").attr("disabled", "disabled");
    $("#date2Edit").attr("disabled", "disabled");
    $("#databasesEdit").val("");
    $("#serverBDEdit").val("");
    $("#portBDEdit").val("");
    $("#nameBDEdit").val("");
    $("#instanceBDEdit").val("");
    $("#userBDEdit").val("");
    $("#passBDEdit").val("");
    $("#EditFracView").val("");
    $("#hiddenCheckedEditFracView").prop("checked", false);
    $("#numFracViewEdit").val("");
    $("#fieldOrderByEdit").val("");
    $("#pathServiceEdit").val("");
    $("#EditHiddenCleanCore").val("false");
    $("#EditDisPreProc").val("");
    $("#hiddenCheckedEditDisPreProc").prop("checked", false);
}

function saveProfile() {
    var concatProfile = "";
    for (var i = 0; i < globalPermissions; i++) {
        if ($("#chPermission_" + i).is(':checked')) {
            concatProfile += $("#hiddenPermission_" + i).val() + "<;>";
        }
    }

    $("#hiddenPermissions").val(concatProfile);

    $.ajax({
        url: "/admin-dashboards/saveProfile",
        type: 'POST',
        data: {nomPerfil: $("#nomPerfil").val(), hiddenPermissions: $("#hiddenPermissions").val()},
        success: function (res) {
            if (res) {
                location.href = "/admin-dashboards/profile";
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores al guardar el perfil... ' + xhr.responseText}, {type: 'danger'});
        }
    });
}

$('#chPermitirTodosProfile').on('click', function () {
    if ($(this).is(':checked')) {
        for (var i = 0; i < globalPermissions; i++) {
            $("#chPermission_" + i).prop("checked", true);
        }
    } else {
        for (var i = 0; i < globalPermissions; i++) {
            $("#chPermission_" + i).prop("checked", false);
        }
    }
});

$('#chPermitirTodosEditProfile').on('click', function () {
    if ($(this).is(':checked')) {
        for (var i = 0; i < globalPermissions; i++) {
            $("#chPermissionEdit_" + i).prop("checked", true);
        }
    } else {
        for (var i = 0; i < globalPermissions; i++) {
            $("#chPermissionEdit_" + i).prop("checked", false);
        }
    }
});

function saveGroup() {
    var concatGroup = "";
    for (var i = 0; i < globalProfiles; i++) {
        if ($("#chProfile_" + i).is(':checked')) {
            concatGroup += $("#hiddenProfile_" + i).val() + "<;>";
        }
    }

    $("#hiddenProfileGroup").val(concatGroup);

    $.ajax({
        url: "/admin-dashboards/saveGroup",
        type: 'POST',
        data: {nomGrupo: $("#nomGrupo").val(), hiddenProfileGroup: $("#hiddenProfileGroup").val()},
        success: function (res) {
            if (res) {
                location.href = "/admin-dashboards/profile";
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores al guardar el grupo... ' + xhr.responseText}, {type: 'danger'});
        }
    });
}

$('#chAgregarTodosGrupos').on('click', function () {
    if ($(this).is(':checked')) {
        for (var i = 0; i < globalProfiles; i++) {
            $("#chProfile_" + i).prop("checked", true);
        }
    } else {
        for (var i = 0; i < globalProfiles; i++) {
            $("#chProfile_" + i).prop("checked", false);
        }
    }
});

$('#chAgregarTodosGruposEdit').on('click', function () {
    if ($(this).is(':checked')) {
        for (var i = 0; i < globalProfiles; i++) {
            $("#chProfileEdit_" + i).prop("checked", true);
        }
    } else {
        for (var i = 0; i < globalProfiles; i++) {
            $("#chProfileEdit_" + i).prop("checked", false);
        }
    }
});

function saveUser() {
    for (var i = 0; i < globalGroups; i++) {
        if ($("#radGroup_" + i).is(':checked')) {
            $("#hiddenGroup").val($("#hiddenGroup_" + i).val());
        }
    }

    $.ajax({
        url: "/admin-dashboards/saveUser",
        type: 'POST',
        data: {nomUser: $("#nomUser").val(), email: $("#email").val(), password: $("#password").val(), hiddenGroup: $("#hiddenGroup").val()},
        success: function (res) {
            if (res) {
                location.href = "/admin-dashboards/profile";
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores al guardar el usuario... ' + xhr.responseText}, {type: 'danger'});
        }
    });
}

function current_tab(id_tab) {
    $("#" + id_tab).click();
    var tab_links = $("#" + $("#" + id_tab).attr("aria-controls")).find(".linkMenu");
    var curr_url = window.location.pathname;
    for (var l_i = 0; l_i < tab_links.length; l_i++) {
        if (tab_links[l_i].getAttribute("onclick").indexOf('location.href=') != -1) {
            if (tab_links[l_i].getAttribute("onclick").split("=")[1].replace(/[';]/g, "") === curr_url.substr(curr_url.lastIndexOf('/') + 1)) {
                /*if ($(tab_links[i]).attr("onclick").split("=")[1].replace(/[';]/g,"") === url.substr(url.lastIndexOf('/') + 1)){*/
                $(tab_links[l_i]).css("font-weight", "bold");
            }
        }
    }
}

$('#btnVolverProfile').on('click', function () {
    location.href = "/admin-dashboards/profile";
});

function loadConfigurationsDB() {
    $.ajax({
        url: "/admin-dashboards/loadConfigurationsDB",
        type: 'GET',
        success: function (data) {
            var array = data.substring(0, data.length - 1).split(",");
            var option = '';
            $('#nameConfiguration').html("");
            for (var i = 0; i < array.length; i++) {
                option += '<option value="' + array[i] + '">' + array[i] + '</option>';
            }
            $('#nameConfiguration').append(option);
            checkPythonDB();
        }
    });
}

function checkPythonDB() {
    $.ajax({
        url: "/admin-dashboards/checkPython",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>databases",
        contentType: "text/plain",
        success: function (data) {
            if (data === "existe") {
                $('#btnRunScriptDB').prop('disabled', false);
            } else if (data === "no_existe") {
                $('#btnRunScriptDB').prop('disabled', true);
                $('#btnIndexerDB').prop('disabled', true);
            }
            checkOutputDB();
        }
    });
}

function checkOutputDB() {
    $.ajax({
        url: "/admin-dashboards/checkOutput",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>databases",
        contentType: "text/plain",
        success: function (data) {
            if (data === "existe") {
                $('#btnIndexerDB').prop('disabled', false);
            } else if (data === "no_existe") {
                $('#btnIndexerDB').prop('disabled', true);
            }
        }
    });
}

$('#btnGenerateScriptDB').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/generateScript",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "databases",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se genero el Script correctamente...'}, {type: 'success'});
                $this.button('reset');
                checkPythonDB();
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores en la creacion del Script, verifique las configuraciones...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

$('#btnRunScriptDB').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/runPython",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "databases",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se ejecuto el python correctamente, verifique la salida en outFiles...'}, {type: 'success'});
                $this.button('reset');
                checkOutputDB();
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se genero errores al ejecutar el python el archivo...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

$('#btnIndexerDB').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/indexer",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "databases",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se indexo el archivo correctamente...'}, {type: 'success'});
                $this.button('reset');
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se genero errores al indexar el archivo...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

function loadConfigurationsFS() {
    $.ajax({
        url: "/admin-dashboards/loadConfigurationsFS",
        type: 'GET',
        success: function (data) {
            var array = data.substring(0, data.length - 1).split(",");
            var option = '';
            $('#nameConfiguration').html("");
            for (var i = 0; i < array.length; i++) {
                option += '<option value="' + array[i] + '">' + array[i] + '</option>';
            }
            $('#nameConfiguration').append(option);
            checkPythonFS();
        }
    });
}

function checkPythonFS() {
    $.ajax({
        url: "/admin-dashboards/checkPython",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>fileServers",
        contentType: "text/plain",
        success: function (data) {
            if (data === "existe") {
                $('#btnRunScriptFS').prop('disabled', false);
            } else if (data === "no_existe") {
                $('#btnRunScriptFS').prop('disabled', true);
            }
            checkOutputFS();
        }
    });
}

function checkOutputFS() {
    $.ajax({
        url: "/admin-dashboards/checkOutput",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>fileServers",
        contentType: "text/plain",
        success: function (data) {
            if (data === "existe") {
                $('#btnIndexerFS').prop('disabled', false);
            } else if (data === "no_existe") {
                $('#btnIndexerFS').prop('disabled', true);
            }
        }
    });
}

$('#btnGenerateScriptFS').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/generateScript",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "fileServers",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se genero el Script correctamente...'}, {type: 'success'});
                $this.button('reset');
                checkPythonFS();
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores en la creacion del Script, verifique las configuraciones...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

$('#btnRunScriptFS').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/runPython",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "fileServers",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se ejecuto el python correctamente, verifique la salida en outFiles...'}, {type: 'success'});
                $this.button('reset');
                checkOutputFS();
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se genero errores al ejecutar el python el archivo...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

$('#btnIndexerFS').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/indexer",
        method: "POST",
        data: $("#nameConfiguration").val() + "<;>" + "fileServers",
        contentType: "text/plain",
        success: function (res) {
            if (res === "Success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se indexo el archivo correctamente...'}, {type: 'success'});
                $this.button('reset');
            } else {
                $.notify({title: '<strong>Error!</strong>', message: 'Se genero errores al indexar el archivo...'}, {type: 'danger'});
                $this.button('reset');
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
        }
    });
});

function syncNow() {
    $.ajax({
        url: "/admin-dashboards/loadConfigsDBReady",
        type: 'GET',
        success: function (data) {
            var array = data.substring(0, data.length - 1).split(",");
            var option = '';
            $('#nameConfigurationSyncNow').html("");
            for (var i = 0; i < array.length; i++) {
                option += '<option value="' + array[i] + '">' + array[i] + '</option>';
            }
            $('#nameConfigurationSyncNow').append(option);
        }
    });
    $("#modal-SyncNow").modal('show');
}

$('#modal-btnSyncNow-si').on('click', function () {
    var $this = $(this);
    $this.button('loading');
    $.ajax({
        url: "/admin-dashboards/syncNow",
        method: "POST",
        data: $("#nameConfigurationSyncNow").val() + "<;>" + "databases",
        contentType: "text/plain",
        success: function (res) {
            if (res === "success") {
                $.notify({title: '<strong>Realizado!</strong>', message: 'Se sincroniz&oacute; la configuraci&oacute;n correctamente...'}, {type: 'success'});
            }
            $this.button('reset');
            $("#modal-SyncNow").modal('hide');
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>', message: 'Se generaron errores... ' + xhr.responseText}, {type: 'danger'});
            $("#modal-SyncNow").modal('hide');
        }
    });

});

$('#modal-btnSyncNow-no').on('click', function () {
    $("#modal-SyncNow").modal('hide');
});

$("#ipInSolrToSolr").on("keyup", function () {
    if ($("#ipInSolrToSolr").val() === "") {
        $("#dnsInSolrToSolr").prop("disabled", false);
    } else {
        $("#dnsInSolrToSolr").prop("disabled", true);
    }
});
$("#portInSolrToSolr").on("keyup", function () {
    if ($("#portInSolrToSolr").val() === "") {
        $("#dnsInSolrToSolr").prop("disabled", false);
    } else {
        $("#dnsInSolrToSolr").prop("disabled", true);
    }
});

$("#dnsInSolrToSolr").on("keyup", function () {
    if ($("#dnsInSolrToSolr").val() === "") {
        $("#ipInSolrToSolr").prop("disabled", false);
        $("#portInSolrToSolr").prop("disabled", false);
    } else {
        $("#ipInSolrToSolr").prop("disabled", true);
        $("#portInSolrToSolr").prop("disabled", true);
    }
});

$("#ipOutSolrToSolr").on("keyup", function () {
    if ($("#ipOutSolrToSolr").val() === "") {
        $("#dnsOutSolrToSolr").prop("disabled", false);
    } else {
        $("#dnsOutSolrToSolr").prop("disabled", true);
    }
});
$("#portOutSolrToSolr").on("keyup", function () {
    if ($("#portOutSolrToSolr").val() === "") {
        $("#dnsOutSolrToSolr").prop("disabled", false);
    } else {
        $("#dnsOutSolrToSolr").prop("disabled", true);
    }
});
$("#dnsOutSolrToSolr").on("keyup", function () {
    if ($("#dnsOutSolrToSolr").val() === "") {
        $("#ipOutSolrToSolr").prop("disabled", false);
        $("#portOutSolrToSolr").prop("disabled", false);
    } else {
        $("#ipOutSolrToSolr").prop("disabled", true);
        $("#portOutSolrToSolr").prop("disabled", true);
    }
});

function GetFieldsSolrToSolr() {
    $.ajax({
        url: "/admin-dashboards/getFieldsSolrToSolr",
        type: 'POST',
        contentType: "text/plain",
        data: getParamsSolrToSolr(),
        dataType: 'json',
        success: function (data1) {

            $("#rowsCountSolrToSolr").text(data1[0].numRegsSolrToSolr);
            $.each(data1[0].fieldsSolrToSolr, function (i, v) {
                $('#availableFieldsSolrToSolr').append(`<option value="${v}" id="${v + "Copy"}">
                                       ${v}
                                  </option>`);
            });
            $("#ipInSolrToSolr").prop("readonly", true);
            $("#portInSolrToSolr").prop("readonly", true);
            $("#dnsInSolrToSolr").prop("readonly", true);
            $("#collectionNameInSolrToSolr").prop("readonly", true);
            $("#btnCargarSolrToSolr").prop("disabled", true);

        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log("no");
        }
    });

}
function getParamsSolrToSolr() {
    var res;
    if ($("#ipInSolrToSolr").val() === "" && $("#portInSolrToSolr").val() === "") {
        res = $("#dnsInSolrToSolr").val() + '/' + $("#collectionNameInSolrToSolr").val();
    } else if ($("#dnsInSolrToSolr").val() === "") {
        res = 'http://' + $("#ipInSolrToSolr").val() + ":" + $("#portInSolrToSolr").val() + '/solr/' + $("#collectionNameInSolrToSolr").val();
    }
    return res;
}

$('#btnCargarSolrToSolr').on('click', function () {
    GetFieldsSolrToSolr();
});

$('#btnAddFieldSolrToSolr').on('click', function () {
    if ($("#availableFieldsSolrToSolr").val() !== "" && $("#availableFieldsSolrToSolr").val() !== null) {
        $('#copyFieldsSolrToSolr').append(`<option value="${$("#availableFieldsSolrToSolr").val()}" id="${$("#availableFieldsSolrToSolr").val() + "Copied"}">
                                            ${$("#availableFieldsSolrToSolr").val()}
                                       </option>`);
        $('#fieldsdHiddenSolrToSolr').val($('#fieldsdHiddenSolrToSolr').val() + ',' + $("#availableFieldsSolrToSolr").val());
        $('option:selected', $("#availableFieldsSolrToSolr")).remove();
    }


});

$('#btnRemoveFieldSolrToSolr').on('click', function () {
    if ($("#copyFieldsSolrToSolr").val() !== "" && $("#copyFieldsSolrToSolr").val() !== null) {
        $('#availableFieldsSolrToSolr').append(`<option value="${$("#copyFieldsSolrToSolr").val()}" id="${$("#copyFieldsSolrToSolr").val() + "Copy"}">
                                                 ${$("#copyFieldsSolrToSolr").val()}
                                            </option>`);
        $('option:selected', $("#copyFieldsSolrToSolr")).remove();
    }


});

$('#btnAddAllFieldsSolrToSolr').on('click', function () {
    $("#availableFieldsSolrToSolr > option").each(function () {
        $('#copyFieldsSolrToSolr').append(`<option value="${$(this).val()}" id="${$(this).val() + "Copied"}">
                                                ${$(this).val()}
                                           </option>`);
        $('#fieldsdHiddenSolrToSolr').val($('#fieldsdHiddenSolrToSolr').val() + ',' + $(this).val());
    });
    $('option', $("#availableFieldsSolrToSolr")).remove();

});

$('#btnRemoveAllFieldsSolrToSolr').on('click', function () {
    $("#copyFieldsSolrToSolr > option").each(function () {
        $('#availableFieldsSolrToSolr').append(`<option value="${$(this).val()}" id="${$(this).val() + "Copied"}">
                                                    ${$(this).val()}
                                                </option>`);
    });
    $('option', $("#copyFieldsSolrToSolr")).remove();

});

function callLastBackUp(solr, core) {
    $.ajax({
        url: "/admin-dashboards/loadSolrLastBackUp",
        method: "POST",
        contentType: "text/plain",
        data: solr + ";" + core,
        success: function (data) {
            $("#tdLast" + core).text(data);

        },
        error: function () {
            $("#tdLast" + core).text(" - ");
        }
    });

}
function callBackUps(solr, core) {
    $.ajax({
        url: "/admin-dashboards/loadSolrBackups",
        method: "POST",
        contentType: "text/plain",
        data: solr + ";" + core,
        success: function (data) {
            console.log("call" + data)
            var SplitSchemaSolrInsts = data.split(";");
            for (var j = 0; j < SplitSchemaSolrInsts.length; j++) {
                if (SplitSchemaSolrInsts[j] !== null && SplitSchemaSolrInsts[j] !== "" && SplitSchemaSolrInsts[j] !== "null") {
                    var o = new Option(SplitSchemaSolrInsts[j], SplitSchemaSolrInsts[j]);
                    $(o).html(SplitSchemaSolrInsts[j]);
                    $("#opBack" + core).append(o);
                }
            }


        },
        error: function () {
            $("#tdLast" + core).text(" - ");
        }
    });

}

function doBackUp() {
    var cores = getCheckedBackUp();
    $.ajax({
        url: "/admin-dashboards/doBackUp",
        method: "POST",
        contentType: "text/plain",
        data: cores,
        success: function (data) {
            //console.log(data);

            $.notify({title: '<strong>Realizado!</strong>', message: 'Se hicieron las copias de seguridad'}, {type: 'success'});

            console.log(data);
        },
        error: function () {
            $("#spanLastBackup").text(" no ");

            alert("No se hizo la copia");
        }
    });

}

function getSplitted(varIn) {
    var toSplit = varIn.split(";");
    var result = "";
    for (var j = 0; j < toSplit.length; j++) {
        result
    }


}

function getCheckedBackUp() {
    var cores = $("#backup_solr").val() + "-";
    $.notify({title: '<strong>Realizando la copia de seguridad</strong>', message: 'Espere el mensaje de confirmacion'}, {type: 'info'}, {clickToHide: true}, {autoHide: false});
    $("input[type=checkbox]:checked").each(function () {
        cores = cores + $(this).val() + ";";
    });
    return cores;
}
function getCheckedRestore() {
    var cores = $("#backup_solr").val() + "+" + $("#restore_solr").val() + "+";
    $("input[type=checkbox]:checked").each(function () {
        cores = cores + $(this).val() + "," + $("#opBack" + $(this).val()).val() + ";";
    });
    return cores;
}

function restoreBackUp() {
    var cores = getCheckedRestore();
    $.ajax({
        url: "/admin-dashboards/restoreBackUp",
        method: "POST",
        contentType: "text/plain",
        data: cores,
        success: function (data) {
            //console.log(data);
            $.notify({title: '<strong>Realizado!</strong>', message: 'Se restauraron las copias de seguridad'}, {type: 'success'});
            console.log(data);
        },
        error: function () {

            alert("No se pudo reestablecer la copia");
        }
    });

}

$('#btnDoBackup').on('click', function () {
    doBackUp();
});

$('#btnRestoreBackup').on('click', function () {
    restoreBackUp();
});

$("#cboxBackUpAll").click(function () {
    $("input[name='optnsBackUp']").prop('checked', $(this).prop('checked'));
    var cores1 = "";
    $('input[name="optnsBackUp"]:checked').each(function () {
        cores1 = cores1 + $(this).val() + ";";
    });
    $("#coresHiddenBackUp").val(cores1);

});


$("#backup_solr").on('change', function () {
    $("#instHiddenBackUp").val($("#backup_solr").val());
});

function getCheckedProgBackUp() {
    var cores = "";
    $('input[name="optnsBackUp"]:checked').each(function () {
        cores = cores + $(this).val() + ";";
    });
    $("#coresHiddenBackUp").val(cores);
}
function saveCleanType() {
    $("#cleanType").val($("#selectCleanType").val());
}

$('#chHiddenCheckedBatches').on('click', function () {
    if ($(this).is(':checked')) {
        $("#tableNumBatches").css("display", "block");
        $("#numBatches").val("");
        $("#hiddenCheckedBatches").val("true");
    } else {
        $("#tableNumBatches").css("display", "none");
        $("#numBatches").val("");
        $("#hiddenCheckedBatches").val("false");
    }
});

//    selectFile.addEventListener('change', function () {
//        hiddenFileName.value = selectFile.value;
//    });

$('#fileSelector').change(function () {
    $("#jsonSelected").val($(this).val());
});


$('#operationSelector').change(function () {

    if ($(this).val() === "Indexar") {
        $("#origin_data").show();
        $("#destination_data").show();
        $("#jsonFile_data").hide();
    } else if ($(this).val() === "Guardar") {
        $("#origin_data").show();
        $("#destination_data").hide();
        $("#jsonFile_data").hide();
    } else if ($(this).val() === "Guardar e Indexar") {
        $("#origin_data").show();
        $("#destination_data").show();
        $("#jsonFile_data").hide();
    } else if ($(this).val() === "Indexar Archivo JSON") {
        $("#origin_data").hide();
        $("#destination_data").show();
        $("#jsonFile_data").show();
    }

});

$("#directorySelector").change(function () {
    
    $("#fileSelector option:gt(0)").remove();

    $(document).ready(function () {
        fillFileSelector();
    });

});

function selectedFields() {

    if ($('#operationSelector').val() === "Indexar") {
        $("#origin_data").show();
        $("#destination_data").show();
        $("#jsonFile_data").hide();
    } else if ($('#operationSelector').val() === "Guardar") {
        $("#origin_data").show();
        $("#destination_data").hide();
        $("#jsonFile_data").hide();
    } else if ($('#operationSelector').val() === "Guardar e Indexar") {
        $("#origin_data").show();
        $("#destination_data").show();
        $("#jsonFile_data").hide();
    } else if ($('#operationSelector').val() === "Indexar Archivo JSON") {
        $("#origin_data").hide();
        $("#destination_data").show();
        $("#jsonFile_data").show();
    }

    $(document).ready(function () {
        fillFileSelector();
        $("#jsonSelected").val($("#fileSelector").val());
    });

}






