/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 * 
 */

var listConnections = function () {
    var tableConnections = $('#tableConnections').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadConnections",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data.conexiones.conexion;
            }
        },
        "scrollX": true,
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-bottom: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id'},
            {'data': 'nomConexion'},
            {'data': 'separadorBD'},
            {'data': 'query'},
            {'data': 'fechaIni'},
            {'data': 'fechaFin'},
            {'data': 'campoFecha'},
            {'data': 'numDays'},
            {'data': 'baseDatos'},
            {'data': 'servidorBD'},
            {'data': 'puertoBD'},
            {'data': 'nombreBD'},
            {'data': 'instanciaBD'},
            {'data': 'userBD'},/////////////////////////
            {'data': 'numFracView'},
            {'data': 'disFracView',
                render: function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="disFracView-active" disabled>';
                    }
                    return data;
                },
                className: "dt-body-center"
            },
            {'data': 'campoOrderBy'},
            {'data': 'pathService'},
            {'data': 'hiddenCleanCore',
                render: function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="hiddenCleanCore-active" disabled>';
                    }
                    return data;
                },
                className: "dt-body-center"
            },
            {'data': 'tipoIndexacion'}
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        rowCallback: function ( row, data ) {
            // Set the checked state of the checkbox in the table
            $('input.disPreProc-active', row).prop( 'checked', data.disPreProc === true );
            $('input.disFracView-active', row).prop( 'checked', data.disFracView === true );
            $('input.hiddenCleanCore-active', row).prop( 'checked', data.hiddenCleanCore === true );
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditConnections("#tableConnections tbody", tableConnections);
    getDataSelectConnections("#tableConnections tbody", tableConnections);
    getDataDeleteConnections("#tableConnections tbody", tableConnections);
}

var getDataEditConnections = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        clearConections();
        
        var data = table.row($(this).parents("tr")).data();
        $("#idEdit").val(data.id);
        $("#nomConnectionEdit").val(data.nomConexion);
        $("#cleanType").val(data.tipoIndexacion);
        $("#selectCleanType").val(data.tipoIndexacion);
        $("#separatorBDEdit").val(data.separadorBD);
        $("#queryEdit").val(data.query);
//        $("#fieldURLEdit").val(data.campoURL);
        $("#fieldDateEdit").val(data.numDays);
        $('#fieldDate').val(data.campoFecha);
        $("#date1Edit").val(data.fechaIni.split(" ")[0]);
        $("#date2Edit").val(data.fechaFin.split(" ")[0]);
        
        if( data.numDays == "" && data.campoFecha == "" && data.fechaIni == "" && data.fechaFin == "" ){
            $("#hiddenDownloadAllFile").val("true");
            $("#CheckedEditDate").prop("checked", true);
//            $("#CheckedEditDate").click(); 
        }
        if( data.numDays !== "" && data.campoFecha !== "" && data.fechaIni == "" && data.fechaFin == "" ){
            $("#hiddenCheckedEdit").val("true");
            $("#hiddenCheckedEditDate").prop("checked", true);
//            $("#hiddenCheckedEditDate").click();
            fillTable ();
        }
        if(data.fechaIni !== "" && data.fechaFin !== "" && data.campoFecha !==  ""){
            $("#hiddenCheckedEdit").val("false");
            $("#hiddenCheckedEditDate").prop("checked", false);
            $("#date1Edit").removeAttr("disabled");
            $("#date2Edit").removeAttr("disabled");
            fillTable ();
        }else{
//            $('#fieldDate').val("")
            $("#hiddenCheckedEdit").val("true");
            $("#hiddenCheckedEditDate").prop("checked", true);
            $("#date1Edit").attr("disabled","disabled");
            $("#date2Edit").attr("disabled","disabled");
        }
        function fillTable (){
            $('#search_results_campoFecha_0').empty();
            $('#fieldDate').val(data.campoFecha);
            var SplitCampoFecha =data.campoFecha.split("\|\|");
            for (var j=0;j<SplitCampoFecha.length;j++){
               $('#search_results_campoFecha_0').append("<option value=\""+ SplitCampoFecha[j] +"\">"+ SplitCampoFecha[j] +"</option>")
            }
        }
        
        $("#databasesEdit").val(data.baseDatos);
        $("#serverBDEdit").val(data.servidorBD);
        $("#portBDEdit").val(data.puertoBD);
        $("#nameBDEdit").val(data.nombreBD);
        $("#instanceBDEdit").val(data.instanciaBD);
        $("#userBDEdit").val(data.userBD);
        $("#passBDEdit").val(data.passBD);

        $("#EditFracView").val(data.disFracView);
        $("#hiddenCheckedEditFracView").prop("checked", data.disFracView);
        if(data.disFracView===true){            
            $("#numFracViewEdit").val(data.numFracView);
        }
        
        $("#fieldOrderByEdit").val(data.campoOrderBy);                    
        $("#pathServiceEdit").val(data.pathService);  
        
        $("#EditHiddenCleanCore").val(data.hiddenCleanCore);
        $("#hiddenCheckedEditCleanCore").prop("checked", data.hiddenCleanCore);
        
        $("#modal-connections").modal('show');
    });
}

$( '#hiddenCheckedEditFracView' ).on( 'click', function() {
    if( $(this).is(':checked') ){ 
        $("#EditFracView").val("true");
        $("#numFracViewEdit").val("");
        $("#numFracViewEdit").removeAttr("disabled");
        $("#fieldOrderByEdit").val("");
        $("#fieldOrderByEdit").removeAttr("disabled");
    }else{
        $("#numFracViewEdit").val("");
        $("#numFracViewEdit").attr("disabled","disabled");
        $("#fieldOrderByEdit").val("");
        $("#fieldOrderByEdit").attr("disabled","disabled");
        $("#EditFracView").val("false");
    }
});

$( '#hiddenCheckedEditCleanCore' ).on( 'click', function() {
    if( $(this).is(':checked') ){ 
        $("#EditHiddenCleanCore").val("true");
    }else{
        $("#EditHiddenCleanCore").val("false");
    }
});

$("#modal-btn-SaveConnection").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/saveEditConnection",
        type: 'POST',
        data: $("#idEdit").val() + "<;!;>" + $("#separatorBDEdit").val() + "<;!;>" + $("#queryEdit").val() +  "<;!;>" + $("#date1Edit").val() + "<;!;>" + $("#date2Edit").val() + "<;!;>" + $("#fieldDate").val()+ "<;!;>" +$("#cleanType").val() + "<;!;>" + $("#fieldDateEdit").val() + "<;!;>" + $("#databasesEdit").val() + "<;!;>" + $("#serverBDEdit").val() + "<;!;>" + $("#portBDEdit").val() + "<;!;>" + $("#nameBDEdit").val() + "<;!;>" + $("#instanceBDEdit").val() + "<;!;>" + $("#userBDEdit").val() + "<;!;>" + $("#passBDEdit").val() + "<;!;>" + $("#EditFracView").val() + "<;!;>" + $("#numFracViewEdit").val() + "<;!;>" + $("#fieldOrderByEdit").val() + "<;!;>" + $("#pathServiceEdit").val() + "<;!;>" + $("#EditHiddenCleanCore").val()+ "<;!;>" +"hoola2",
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listConnections();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico la conexi\u00F3n correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar la conexi\u00F3n...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-connections").modal('hide');
});

var getDataDeleteConnections = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDeleteCon").val(data.id);
        $("#nomConnectionEdit").val(data.nomConexion);
        $("#modal-DeleteConnection").modal('show');
    });
}

$("#modal-btn-si-DeleteConnection").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteConnection",
        type: 'POST',
        data: $("#idDeleteCon").val()+"<;>"+$("#nomConnectionEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listConnections();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro la conexi\u00F3n correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar la conexi\u00F3n...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteConnection").modal('hide');
});

$("#modal-btn-no-DeleteConnection").on("click", function () {
    $("#modal-DeleteConnection").modal('hide');
});

var getDataSelectConnections = function (tbody, table) {
    $(tbody).on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });
}

var listTasks = function () {
    var tableTasks = $('#tableTasks').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadProgTaskBD",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data.tareas.tarea;
            }
        },
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary'><i class='fa fa-pencil-square-o'></i></button>	<button type='button' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id'},
            {'data': 'porMinutos'},
            {'data': 'porHoras'},
            {'data': 'dia'},
            {'data': 'diaSemana'},
            {'data': 'diaMes'},
            {'data': 'hora'},
            {'data': 'minuto'},
            {'data': 'conexion'}
            
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditTasks("#tableTasks tbody", tableTasks);
    getDataDeleteTasks("#tableTasks tbody", tableTasks);
}

var getDataEditTasks = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        cleanTaskIndex();
        var data = table.row($(this).parents("tr")).data();
        //console.log(data);
        $("#idEdit").val(data.id);
        if(data.porMinutos!==""){
            $("#hiddenCheckedMinutesEdit").val("true");
            $("#checkedMinutesEdit").prop("checked", true);
            $("#perMinutesTaskEdit").val(data.porMinutos);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.porHoras!==""){
            $("#hiddenCheckedHoursEdit").val("true");
            $("#checkedHoursEdit").prop("checked", true);
            $("#porHoursTaskEdit").val(data.porHoras);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.dia!==""){
            $("#hiddenCheckedDayEdit").val("true");
            $("#checkedDaysEdit").prop("checked", true);
            $("#dayTaskEdit").val(data.dia);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaSemana !== ""){
            $("#hiddenCheckedDayWeekEdit").val("true");
            $("#checkedDayWeekEdit").prop("checked", true);
            $("#dayWeekEdit").val(data.diaSemana);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaMes !== ""){
            $("#hiddenCheckedDayMonthEdit").val("true");
            $("#checkedDayMonthEdit").prop("checked", true);
            $("#dayMonthEdit").val(data.diaMes);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }
        
        $("#hourTaskEdit").val(data.hora);
        $("#minuteTaskEdit").val(data.minuto);
        $("#selConnectionEdit").val(data.conexion);
        $("#modal-tasks").modal('show');
    });
}

function cleanTaskIndex(){
    $("#idEdit").val("");
    $("#hiddenCheckedMinutesEdit").val("false");
    $("#checkedMinutesEdit").prop("checked", false);
    $("#perMinutesTaskEdit").val("");
    $("#hiddenCheckedHoursEdit").val("false");
    $("#checkedHoursEdit").prop("checked", false);
    $("#perHoursTaskEdit").val("");
    
    $("#hiddenCheckedDayEdit").val("false");
    $("#checkedDaysEdit").prop("checked", false);
    $("#dayTaskEdit").val("");

    $("#hiddenCheckedDayWeekEdit").val("false");
    $("#checkedDayWeekEdit").prop("checked", false);
    $("#dayWeekEdit").val("");

    $("#hiddenCheckedDayMonthEdit").val("false");
    $("#checkedDayMonthEdit").prop("checked", false);
    $("#dayMonthEdit").val("");    
    $("#hourTaskEdit").val("");
    $("#minuteTaskEdit").val("");
    $("#selConnectionEdit").val("");
}

$("#modal-btn-SaveTasksBD").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/updateProgTaskBD",
        type: 'POST',
        data: $("#idEdit").val() + "<;>" + $("#perMinutesTaskEdit").val() + "<;>" + $("#perHoursTaskEdit").val() + "<;>" + $("#dayTaskEdit").val() + "<;>" + $("#dayWeekEdit").val() + "<;>" + $("#dayMonthEdit").val() + "<;>" + $("#hourTaskEdit").val() + "<;>" + $("#minuteTaskEdit").val() + "<;>" + $("#selConnectionEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasks();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-tasks").modal('hide');
});

var getDataDeleteTasks = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDeletetaskBD").val(data.id);
        $("#modal-DeleteTaskBD").modal('show');
    });
}

$("#modal-btn-si-DelTaskBD").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteProgTaskBD",
        type: 'POST',
        data: $("#idDeletetaskBD").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasks();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteTaskBD").modal('hide');
});

$("#modal-btn-no-DelTaskBD").on("click", function () {
    $("#modal-DeleteTaskBD").modal('hide');
});

var listEditPermissions = function () {
    var tableEditPermissions = $('#tableEditPermissions').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadPermission",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data;
            }
        },
        "scrollX": true,
        'paging': true,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-right: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id_permission'},
            {'data': 'desc_permission'}
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando...",
            "paginate": {
                "previous": "Anterior",
                "next": "Siguiente"
            },
            "lengthMenu":"Mostrar _MENU_ entradas",
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditPermissions("#tableEditPermissions tbody", tableEditPermissions);
    getDataDeletePermissions("#tableEditPermissions tbody", tableEditPermissions);
}

var getDataEditPermissions = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idEdit").val(data.id_permission);
        $("#descPermissionEdit").val(data.desc_permission);
        $("#modal-editPermissions").modal('show');
    });
}

$("#modal-btn-SaveEditPermission").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/saveEditPermission",
        type: 'POST',
        data: $("#idEdit").val() + "<;>" + $("#descPermissionEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditPermissions();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico el permiso correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar el permiso...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-editPermissions").modal('hide');
});

var getDataDeletePermissions = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDelete").val(data.id_permission);
        $("#modal-DeletePermission").modal('show');
    });
}

$("#modal-btn-si-DelPermission").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deletePermission",
        type: 'POST',
        data: $("#idDelete").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditPermissions();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro el permiso correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar el permiso...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeletePermission").modal('hide');
});

$("#modal-btn-no-DelPermission").on("click", function () {
    $("#modal-DeletePermission").modal('hide');
});

var listEditProfiles = function () {
    var tableEditProfiles = $('#tableEditProfiles').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadProfiles",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data;
            }
        },
        "scrollX": true,
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-right: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id_profile'},
            {'data': 'name_profile'}
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditProfiles("#tableEditProfiles tbody", tableEditProfiles);
    getDataDeleteProfiles("#tableEditProfiles tbody", tableEditProfiles);
}

var getDataEditProfiles = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#id_profileEdit").val(data.id_profile);
        $("#nomPerfilEdit").val(data.name_profile);
                
        $.ajax({
            url: "/admin-dashboards/loadPermission",
            method: "POST",
            success: function (data){
                var json = JSON.parse(data);
                $('#tablePermissionsEdit').html('');
                $('#tablePermissionsEdit').append('<thead><tr><td><div class=\"dvCheckbox\">Permisos de Usuarios</div></td><td>Permitir</td></tr></thead>');
                jQuery.each(json, function(i, val) {
                    if(i>0){
                        $('#tablePermissionsEdit').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenPermissionEdit_'+i+'\" value=\"'+val.id_permission+'\"/></td><td><div class=\"dvCheckbox\">'+val.desc_permission+'</div></td><td><input type="checkbox" id=\"chPermissionEdit_'+i+'\"/></td></tr>');
                    }
                });
                globalPermissions = $('#tablePermissionsEdit tr').length;
                //console.log(globalPermissions);
                $.ajax({
                    url: "/admin-dashboards/loadPermissionsProfiles",
                    type: 'POST',
                    data: ""+$("#id_profileEdit").val()+"",
                    contentType: "text/plain",
                    dataType: "text",
                    success: function (resp){
                        var json = JSON.parse(resp);
                        jQuery.each(json, function(i, val) {
                            for(var j=0; j<=globalPermissions; j++){
                                if($("#hiddenPermissionEdit_"+j).val()===""+val.id_permission+""){
                                    $("#chPermissionEdit_"+j).prop("checked",true);
                                }
                            }
                        });
                    }
                });
                $("#modal-editProfiles").modal('show');
            }
        });
    });
}

$("#modal-btn-SaveEditProfile").on("click", function () {
    var concatProfile="";
    for(var i=0; i<globalPermissions; i++){
        if($("#chPermissionEdit_"+i).is(':checked')){
            concatProfile+=$("#hiddenPermissionEdit_"+i).val()+"<;>";
        } 
    }
    
    $("#hiddenPermissionsEdit").val(concatProfile);
    
    $.ajax({
        url: "/admin-dashboards/saveEditProfile",
        type: 'POST',
        data: $("#id_profileEdit").val() + "<;;>" + $("#nomPerfilEdit").val() + "<;;>" + $("#hiddenPermissionsEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditProfiles();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico el perfil correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar el perfil...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-editProfiles").modal('hide');
});

var getDataDeleteProfiles = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDelete").val(data.id_profile);
        $("#modal-DeleteProfile").modal('show');
    });
}

$("#modal-btn-si-DeleteProfile").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteProfile",
        type: 'POST',
        data: $("#idDelete").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditProfiles();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro el perfil correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar el perfil...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteProfile").modal('hide');
});

$("#modal-btn-no-DeleteProfile").on("click", function () {
    $("#modal-DeleteProfile").modal('hide');
});

var listEditGroups = function () {
    var tableEditGroups = $('#tableEditGroups').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadGroups",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data;
            }
        },
        "scrollX": true,
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-right: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id_group'},
            {'data': 'name_group'}
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditGroups("#tableEditGroups tbody", tableEditGroups);
    getDataDeleteGroups("#tableEditGroups tbody", tableEditGroups);
}

var getDataEditGroups = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        var data = table.row($(this).parents("tr")).data();
        if(data.name_group!=="superusuarios" || $("#nameUser").val()==="superusuario"){
            $("#id_groupEdit").val(data.id_group);
            $("#nomGrupoEdit").val(data.name_group);

            $.ajax({
                url: "/admin-dashboards/loadProfiles",
                method: "POST",
                success: function (data){
                    var json = JSON.parse(data);
                    $('#tableProfilesEdit').html('');
                    $('#tableProfilesEdit').append('<thead><tr><td><div class=\"dvCheckbox\">Perfiles</div></td><td>Agregar</td></tr></thead>');
                    jQuery.each(json, function(i, val) {
                        if(i>0){
                            if(val.name_profile!=="superusuario" || $("#nameUser").val()==="superusuario"){
                                $('#tableProfilesEdit').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenProfileEdit_'+i+'\" value=\"'+val.id_profile+'\"/></td><td><div class=\"dvCheckbox\">'+val.name_profile+'</div></td><td><input type="checkbox" id=\"chProfileEdit_'+i+'\"/></td></tr>');
                            }
                        }
                    });
                    globalProfiles = $('#tableProfilesEdit tr').length;
                    //console.log(globalPermissions);
                    $.ajax({
                        url: "/admin-dashboards/loadProfilesGroups",
                        type: 'POST',
                        data: ""+$("#id_groupEdit").val()+"",
                        contentType: "text/plain",
                        dataType: "text",
                        success: function (resp){
                            var json = JSON.parse(resp);
                            jQuery.each(json, function(i, val) {
                                for(var j=0; j<=globalProfiles; j++){
                                    if($("#hiddenProfileEdit_"+j).val()===""+val.id_profile+""){
                                        $("#chProfileEdit_"+j).prop("checked",true);
                                    }
                                }
                            });
                        }
                    });
                    $("#modal-editGroups").modal('show');
                }
            });
        }
    });
}

$("#modal-btn-SaveEditGroup").on("click", function () {
    var concatGroup="";
    for(var i=0; i<globalProfiles; i++){
        if($("#chProfileEdit_"+i).is(':checked')){
            concatGroup+=$("#hiddenProfileEdit_"+i).val()+"<;>";
        } 
    }
    
    $("#hiddenProfileGroupEdit").val(concatGroup);
    
    $.ajax({
        url: "/admin-dashboards/saveEditGroup",
        type: 'POST',
        data: $("#id_groupEdit").val() + "<;;>" + $("#nomGrupoEdit").val() + "<;;>" + $("#hiddenProfileGroupEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditGroups();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico el grupo correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar el grupo...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-editGroups").modal('hide');
});

var getDataDeleteGroups = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        if(data.name_group!=="superusuarios" || $("#nameUser").val()==="superusuario"){
            $("#idDelete").val(data.id_group);
            $("#modal-DeleteGroup").modal('show');
        }
    });
}

$("#modal-btn-si-DeleteGroup").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteGroup",
        type: 'POST',
        data: $("#idDelete").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditGroups();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro el grupo correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar el grupo...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteGroup").modal('hide');
});

$("#modal-btn-no-DeleteGroup").on("click", function () {
    $("#modal-DeleteGroup").modal('hide');
});

var listEditUsers = function () {
    var tableEditUsers = $('#tableEditUsers').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadUsers",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data;
            }
        },
        "scrollX": true,
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-right: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id_user'},
            {'data': 'name_user'},
            {'data': 'address'},
            {'data': 'name_group'}
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditUsers("#tableEditUsers tbody", tableEditUsers);
    getDataDeleteUsers("#tableEditUsers tbody", tableEditUsers);
}

var getDataEditUsers = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        var data = table.row($(this).parents("tr")).data();
        if(data.name_user!=="superusuario" || $("#nameUser").val()==="superusuario"){
            $("#id_UserEdit").val(data.id_user);
            $("#nomUserEdit").val(data.name_user);
            $("#emailEdit").val(data.address);
            $("#passwordEdit").val(data.password);

            $.ajax({
                url: "/admin-dashboards/loadGroups",
                method: "POST",
                success: function (resp){
                    var json = JSON.parse(resp);
                    $('#tableGroupsEdit').html('');
                    $('#tableGroupsEdit').append('<thead><tr><td><div class=\"dvCheckbox\">Grupos</div></td><td>Seleccionar</td></tr></thead>');
                    jQuery.each(json, function(i, val) {
                        if(i>0){
                            if(val.name_group!=="superusuarios" || $("#nameUser").val()==="superusuario"){
                                $('#tableGroupsEdit').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenGroupEdit_'+i+'\" value=\"'+val.id_group+'\"/></td><td><div class=\"dvCheckbox\">'+val.name_group+'</div></td><td><input type="radio" name="radioGroups" id=\"radGroupEdit_'+i+'\"/></td></tr>');
                            }
                            if($("#hiddenGroupEdit_"+i).val()===""+data.id_group+""){
                                $("#radGroupEdit_"+i).prop("checked",true);
                            }
                        }
                    });
                    globalGroups = $('#tableGroupsEdit tr').length;

                    $("#modal-editUsers").modal('show');
                }
            });
        }
    });
}

$("#modal-btn-SaveEditUser").on("click", function () {
    for(var i=0; i<globalGroups; i++){
        if($("#radGroupEdit_"+i).is(':checked')){
            $("#hiddenGroupEdit").val($("#hiddenGroupEdit_"+i).val());
        } 
    }
    
    $.ajax({
        url: "/admin-dashboards/saveEditUser",
        type: 'POST',
        data: $("#id_UserEdit").val() + "<;>" + $("#nomUserEdit").val() + "<;>" + $("#emailEdit").val() + "<;>" + $("#passwordEdit").val() + "<;>" + $("#hiddenGroupEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditUsers();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico el usuario correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar el usuario...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-editUsers").modal('hide');
});

var getDataDeleteUsers = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        if(data.name_user!=="superusuario" || $("#nameUser").val()==="superusuario"){
            $("#idDelete").val(data.id_user);
            $("#modal-DeleteUser").modal('show');
        }
    });
}

$("#modal-btn-si-DeleteUser").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteUser",
        type: 'POST',
        data: $("#idDelete").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listEditUsers();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro el usuario correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar el usuario...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteUser").modal('hide');
});

$("#modal-btn-no-DeleteUser").on("click", function () {
    $("#modal-DeleteUser").modal('hide');
});

var listConnectionsFS = function () {
    var tableConnectionsFS = $('#tableConnectionsFS').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadConnectionsFS",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data.conexiones.conexion;
            }
        },
        "scrollX": true,
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary' style='width: 35px;margin-bottom: 10px;' title='Editar'><i class='fa fa-pencil-square-o'></i></button><button type='button' title='Eliminar' style='width: 35px;' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id'},
            {'data': 'nomConexion'},
            {'data': 'ipServerFS'},
            {'data': 'portServerFS'},
            {'data': 'pathFileServer'},
            {'data': 'userFS'},
            {'data': 'domainFS'},
            {'data': 'pathService'},
            {'data': 'hiddenCleanCore',
                render: function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="hiddenCleanCore-active" disabled>';
                    }
                    return data;
                },
                className: "dt-body-center"
            }
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        rowCallback: function ( row, data ) {
            // Set the checked state of the checkbox in the table
            $('input.hiddenCleanCore-active', row).prop( 'checked', data.hiddenCleanCore === true );
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditConnectionsFS("#tableConnectionsFS tbody", tableConnectionsFS);
    getDataDeleteConnectionsFS("#tableConnectionsFS tbody", tableConnectionsFS);
}

var getDataEditConnectionsFS = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        clearConections();
        var data = table.row($(this).parents("tr")).data();
        $("#idEdit").val(data.id);
        $("#nomConnectionEdit").val(data.nomConexion);
        $("#ipServerFSEdit").val(data.ipServerFS);
        $("#portServerFSEdit").val(data.portServerFS);
        $("#pathFileServerEdit").val(data.pathFileServer);
        $("#userFSEdit").val(data.userFS);
        $("#domainFSEdit").val(data.domainFS);
        $("#passFSEdit").val(data.passFS);
        $("#pathServiceEdit").val(data.pathService);  
        
        $("#EditHiddenCleanCore").val(data.hiddenCleanCore);
        $("#hiddenCheckedEditCleanCore").prop("checked", data.hiddenCleanCore);
        
        $("#modal-connectionsFS").modal('show');
    });
}

$("#modal-btn-SaveConnectionFS").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/saveEditConnectionFS",
        type: 'POST',
        data: $("#idEdit").val() + "<;>" + $("#ipServerFSEdit").val() + "<;>" + $("#portServerFSEdit").val() + "<;>" + $("#pathFileServerEdit").val() + "<;>" + $("#userFSEdit").val() + "<;>" + $("#domainFSEdit").val() + "<;>" + $("#passFSEdit").val() + "<;>" + $("#pathServiceEdit").val() + "<;>" + $("#EditHiddenCleanCore").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listConnectionsFS();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico la configuracion correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar la configuracion...'+res},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-connectionsFS").modal('hide');
});

var getDataDeleteConnectionsFS = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDeleteCon").val(data.id);
        $("#modal-DeleteConnectionFS").modal('show');
    });
}

$("#modal-btn-si-DeleteConnectionFS").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteConnectionFS",
        type: 'POST',
        data: $("#idDeleteCon").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listConnectionsFS();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro la conexi\u00F3n correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar la conexi\u00F3n...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteConnectionFS").modal('hide');
});

$("#modal-btn-no-DeleteConnectionFS").on("click", function () {
    $("#modal-DeleteConnectionFS").modal('hide');
});

var listTasksFS = function () {
    var tableTasksFS = $('#tableTasksFS').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadProgTaskFS",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data.tareas.tarea;
            }
        },
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary'><i class='fa fa-pencil-square-o'></i></button>	<button type='button' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id'},
            {'data': 'porMinutos'},
            {'data': 'porHoras'},
            {'data': 'dia'},
            {'data': 'diaSemana'},
            {'data': 'diaMes'},
            {'data': 'hora'},
            {'data': 'minuto'},
            {'data': 'conexion'}
            
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditTasksFS("#tableTasksFS tbody", tableTasksFS);
    getDataDeleteTasksFS("#tableTasksFS tbody", tableTasksFS);
}

var getDataEditTasksFS = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        cleanTaskIndex();
        var data = table.row($(this).parents("tr")).data();
        //console.log(data);
        $("#idEdit").val(data.id);
        if(data.porMinutos!==""){
            $("#hiddenCheckedMinutesEdit").val("true");
            $("#checkedMinutesEdit").prop("checked", true);
            $("#perMinutesTaskEdit").val(data.porMinutos);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.porHoras!==""){
            $("#hiddenCheckedHoursEdit").val("true");
            $("#checkedHoursEdit").prop("checked", true);
            $("#porHoursTaskEdit").val(data.porHoras);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.dia!==""){
            $("#hiddenCheckedDayEdit").val("true");
            $("#checkedDaysEdit").prop("checked", true);
            $("#dayTaskEdit").val(data.dia);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaSemana !== ""){
            $("#hiddenCheckedDayWeekEdit").val("true");
            $("#checkedDayWeekEdit").prop("checked", true);
            $("#dayWeekEdit").val(data.diaSemana);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaMes !== ""){
            $("#hiddenCheckedDayMonthEdit").val("true");
            $("#checkedDayMonthEdit").prop("checked", true);
            $("#dayMonthEdit").val(data.diaMes);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }
        
        $("#hourTaskEdit").val(data.hora);
        $("#minuteTaskEdit").val(data.minuto);
        $("#selConnectionEdit").val(data.conexion);
        $("#modal-tasksFS").modal('show');
    });
}

function cleanTaskIndex(){
    $("#idEdit").val("");
    $("#hiddenCheckedMinutesEdit").val("false");
    $("#checkedMinutesEdit").prop("checked", false);
    $("#perMinutesTaskEdit").val("");
    $("#hiddenCheckedHoursEdit").val("false");
    $("#checkedHoursEdit").prop("checked", false);
    $("#perHoursTaskEdit").val("");
    
    $("#hiddenCheckedDayEdit").val("false");
    $("#checkedDaysEdit").prop("checked", false);
    $("#dayTaskEdit").val("");

    $("#hiddenCheckedDayWeekEdit").val("false");
    $("#checkedDayWeekEdit").prop("checked", false);
    $("#dayWeekEdit").val("");

    $("#hiddenCheckedDayMonthEdit").val("false");
    $("#checkedDayMonthEdit").prop("checked", false);
    $("#dayMonthEdit").val("");    
    $("#hourTaskEdit").val("");
    $("#minuteTaskEdit").val("");
    $("#selConnectionEdit").val("");
}

$("#modal-btn-SaveTasksFS").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/updateProgTaskFS",
        type: 'POST',
        data: $("#idEdit").val() + "<;>" + $("#perMinutesTaskEdit").val() + "<;>" + $("#perHoursTaskEdit").val() + "<;>" + $("#dayTaskEdit").val() + "<;>" + $("#dayWeekEdit").val() + "<;>" + $("#dayMonthEdit").val() + "<;>" + $("#hourTaskEdit").val() + "<;>" + $("#minuteTaskEdit").val() + "<;>" + $("#selConnectionEdit").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasksFS();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-tasksFS").modal('hide');
});

var getDataDeleteTasksFS = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDeletetaskFS").val(data.id);
        $("#modal-DeleteTaskFS").modal('show');
    });
}

$("#modal-btn-si-DelTaskFS").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteProgTaskFS",
        type: 'POST',
        data: $("#idDeletetaskFS").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasksFS();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteTaskFS").modal('hide');
});

$("#modal-btn-no-DelTaskFS").on("click", function () {
    $("#modal-DeleteTaskFS").modal('hide');
});

var listTasksBackUp = function () {
    var tableTasksBackUp = $('#tableTasksBackUp').DataTable({
        "destroy": true,
        "ajax": {
            "url": "/admin-dashboards/loadProgTaskBackUp",
            "method": "POST",
            "dataType": "json",
            "dataSrc": function (data) {
                //console.log(data);
                return data.tareas.tarea;
            }
        },
        'paging': false,
        'sort': false,
        'searching': false,
        columns: [
            {'defaultContent': "<button type='button' class='editar btn btn-primary'><i class='fa fa-pencil-square-o'></i></button>	<button type='button' class='eliminar btn btn-danger' data-toggle='modal' data-target='#modalEliminar' ><i class='fa fa-trash-o'></i></button>"},
            {'data': 'id'},
            {'data': 'porMinutos'},
            {'data': 'porHoras'},
            {'data': 'dia'},
            {'data': 'diaSemana'},
            {'data': 'diaMes'},
            {'data': 'hora'},
            {'data': 'minuto'},
            {'data': 'instancia'},
            {'data': 'core'}
            
        ],
        'language': {
            "info":       "Mostrando pagina _PAGE_ de _PAGES_",
            "infoEmpty":  "No hay registros para mostrar",
            "emptyTable": "No hay datos en la tabla",
            "loadingRecords": "Cargando...",
            "processing":     "Procesando..."
        },
        "initComplete": function(settings, json) {
            this.api().row(':eq(0)').remove().draw();
        }
    });
      
    getDataEditTasksBackUp("#tableTasksBackUp tbody", tableTasksBackUp);
    getDataDeleteTasksBackUp("#tableTasksBackUp tbody", tableTasksBackUp);
    
}


var getDataEditTasksBackUp = function (tbody, table) {
    $(tbody).on("click", "button.editar", function () {
        cleanTaskIndex();
        
        var data = table.row($(this).parents("tr")).data();
        //console.log(data);
        $("#idEdit").val(data.id);
        if(data.porMinutos!==""){
            $("#hiddenCheckedMinutesEdit").val("true");
            $("#checkedMinutesEdit").prop("checked", true);
            $("#perMinutesTaskEdit").val(data.porMinutos);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.porHoras!==""){
            $("#hiddenCheckedHoursEdit").val("true");
            $("#checkedHoursEdit").prop("checked", true);
            $("#porHoursTaskEdit").val(data.porHoras);
            document.getElementById("divProgHoursDBEdit").style.display = "none";
        }else if(data.dia!==""){
            $("#hiddenCheckedDayEdit").val("true");
            $("#checkedDaysEdit").prop("checked", true);
            $("#dayTaskEdit").val(data.dia);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaSemana !== ""){
            $("#hiddenCheckedDayWeekEdit").val("true");
            $("#checkedDayWeekEdit").prop("checked", true);
            $("#dayWeekEdit").val(data.diaSemana);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }else if(data.diaMes !== ""){
            $("#hiddenCheckedDayMonthEdit").val("true");
            $("#checkedDayMonthEdit").prop("checked", true);
            $("#dayMonthEdit").val(data.diaMes);
            document.getElementById("divProgHoursDBEdit").style.display = "block";
        }
        
        $("#hourTaskEdit").val(data.hora);
        $("#minuteTaskEdit").val(data.minuto);
        fillBackUpSolrInsts();
        $("#backup_solr").val(data.instancia);
        fillBackupEditInstCores(data.instancia, data.core);
        
        $("#modal-tasksFS").modal('show');
    });
}

function fillBackupEditInstCores(inst, core) {
    $.ajax({
        url: "/admin-dashboards/loadSolrCores",
        method: "POST",
        contentType: "text/plain",
        data: inst,
        success: function (data) {
            $("#core_solr").empty()
            //console.log(data);
            var SplitSchemaCores = data.split(";");
           for (var j = 0; j < SplitSchemaCores.length; j++) {
                if (SplitSchemaCores[j] !== null && SplitSchemaCores[j] !== "" && SplitSchemaCores[j] !== "null") {
                    var o = new Option(SplitSchemaCores[j], SplitSchemaCores[j]);
                    $(o).html(SplitSchemaCores[j]);
                    $("#core_solr").append(o);
                }
            }
            $("#core_solr").val(core);
            

        },
        error: function () {
            $("#core_solr").empty()
            alert("No se encontraron colecciones");
        }
    });
}

var getDataDeleteTasksBackUp = function (tbody, table) {
    $(tbody).on("click", "button.eliminar", function () {
        var data = table.row($(this).parents("tr")).data();
        $("#idDeletetaskFS").val(data.id);
        $("#modal-DeleteTaskFS").modal('show');
    });
}

$("#modal-btn-si-DelTaskBackUp").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/deleteScheduleTaskBackUp",
        type: 'POST',
        data: $("#idDeletetaskFS").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasksBackUp();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se borro la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al borrar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-DeleteTaskFS").modal('hide');
});

$("#modal-btn-no-DelTaskBackUp").on("click", function () {
    $("#modal-DeleteTaskFS").modal('hide');
});

$("#modal-btn-SaveTasksBackUp").on("click", function () {
    $.ajax({
        url: "/admin-dashboards/updateScheduleTaskBackUp",
        type: 'POST',
        data: $("#idEdit").val() + "<;>" + $("#perMinutesTaskEdit").val() + "<;>" + $("#perHoursTaskEdit").val() + "<;>" + $("#dayTaskEdit").val() + "<;>" + $("#dayWeekEdit").val() + "<;>" + $("#dayMonthEdit").val() + "<;>" + $("#hourTaskEdit").val() + "<;>" + $("#minuteTaskEdit").val() + "<;>" + $("#backup_solr").val()+ "<;>" + $("#core_solr").val(),
        contentType: "text/plain",
        dataType: "text",
        success: function (res) {
            if (res) {
                listTasksBackUp();
                $.notify({title: '<strong>Realizado!</strong>',message: 'Se modifico la tarea correctamente...'},{type: 'success'});
            } else {
                $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores al modificar la tarea...'},{type: 'danger'});
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            $.notify({title: '<strong>Error!</strong>',message: 'Se generaron errores... '+xhr.responseText},{type: 'danger'});
        }
    });
    $("#modal-tasksFS").modal('hide');
});