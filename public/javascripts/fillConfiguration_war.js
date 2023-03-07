/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var globalProfiles;
var globalPermissions;
var globalGroups;
var globalBuscador;

function fillConfigModule(){
    $.ajax({
        url: "/admin-dashboards/loadConfigModule",
        success: function (data){
            var splitData = data.split("<;>");
            $("#pathFileModule").val(splitData[0]);
            $("#passwordCertificate").val(splitData[1]);
        }
    });
}

function fillConfigUrlApiModule(){
    $.ajax({
        url: "/admin-dashboards/loadUrlConfig",
        success: function (data){
            $("#urlApiSolr").val(data);
        }
    });
}

function fillPermissions(){
    $.ajax({
        url: "/admin-dashboards/loadPermission",
        method: "POST",
        success: function (data){
            var json = JSON.parse(data);
            //console.log(json);
            $('#tablePermissions').append('<thead><tr><td><div class=\"dvCheckbox\">Permisos de Usuarios</div></td><td>Permitir</td></tr></thead>');
            jQuery.each(json, function(i, val) {
                if(i>0){
                    $('#tablePermissions').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenPermission_'+i+'\" value=\"'+val.id_permission+'\"/></td><td><div class=\"dvCheckbox\">'+val.desc_permission+'</div></td><td><input type="checkbox" id=\"chPermission_'+i+'\"/></td></tr>');
                }
            });
            globalPermissions = $('#tablePermissions tr').length;
        }
    });
}

function fillProfiles(){
    $.ajax({
        url: "/admin-dashboards/loadProfiles",
        method: "POST",
        success: function (data){
            var json = JSON.parse(data);
            //console.log(json);
            $('#tableProfiles').append('<thead><tr><td><div class=\"dvCheckbox\">Perfiles</div></td><td>Agregar</td></tr></thead>');
            jQuery.each(json, function(i, val) {
                if(i>0){
                    $('#tableProfiles').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenProfile_'+i+'\" value=\"'+val.id_profile+'\"/></td><td><div class=\"dvCheckbox\">'+val.name_profile+'</div></td><td><input type="checkbox" id=\"chProfile_'+i+'\"/></td></tr>');
                }
            });
            globalProfiles = $('#tableProfiles tr').length;
        }
    });
}

function fillGroups(){
    $.ajax({
        url: "/admin-dashboards/loadGroups",
        method: "POST",
        success: function (data){
            var json = JSON.parse(data);
            //console.log(json);
            $('#tableGroups').append('<thead><tr><td><div class=\"dvCheckbox\">Grupos</div></td><td>Seleccionar</td></tr></thead>');
            jQuery.each(json, function(i, val) {
                if(i>0){
                    $('#tableGroups').append('<tr><td style=\"display:none;\"><input type=\"hidden\" id=\"hiddenGroup_'+i+'\" value=\"'+val.id_group+'\"/></td><td><div class=\"dvCheckbox\">'+val.name_group+'</div></td><td><input type="radio" name="radioGroups" id=\"radGroup_'+i+'\"/></td></tr>');
                }
            });
            globalGroups = $('#tableGroups tr').length;
        }
    });
}

function fillOtherConfigCleanData(){
    $.ajax({
        url: "/admin-dashboards/loadCleanDataService",
        success: function (data){
            var splitData = data.split("<;>");
            $("#ipServiceCleanData").val(splitData[0]);
            $("#portServiceCleanData").val(splitData[1]);
            $("#portBootleCleanData").val(splitData[2]);
        }
    });
}

function fillIframeCleanDataDB(){
    $.ajax({
        url: "/admin-dashboards/loadPathCleanDataService",
        success: function (data){  
           var jupyterBD = data+"/tree/work/admin_dashboards/admin_serviceDash/databases/filesViews";
            $('#iframeCleanDataServiceDB').attr('src', jupyterBD);
        }
    });
}

function fillIframeCleanDataFS(){
    $.ajax({
        url: "/admin-dashboards/loadPathCleanDataService",
        success: function (data){  
           var jupyterBD = data+"/tree/work/admin_dashboards/admin_serviceDash/fileServers/filesViews";
            $('#iframeCleanDataServiceFS').attr('src', jupyterBD);
        }
    });
}

function fillAdminUsers(){
    $.ajax({
        url: "/admin-dashboards/loadAdminUsers",
        success: function (data){
            $.ajax({
                type: 'GET',
                url: data,
                success: function(){
            $("#iframeAdminUsersDashboards").attr('src',data);
                },
                error: function(xhr, error, code){
                    $("#dvAdminUsers").html('<br><div class="alert alert-warning" role="alert">No existe un administrador de dashboards</div>');
                }
            });
        }
    });
}
function campoFechaADDWord(idtag){
    var word = $('#campoFecha_'+idtag).val();
    if(word!=""){
        $("#search_results_campoFecha_"+idtag).append("<option value=\""+word+"\">"+word+" </option>");
        savecampoFechaConfig();
    }
}
function savecampoFechaConfig(){
    SconcatcampoFecha = "";
    var optionsWords = $('#search_results_campoFecha_0 option');
    SconcatcampoFecha += $.map(optionsWords ,function(option) {return option.value;});     // disparador(exp regular o palabra)
    while (SconcatcampoFecha.includes(",")){
    SconcatcampoFecha=SconcatcampoFecha.replace(",","\|\|");
    }
    document.getElementById("fieldDate").value = SconcatcampoFecha ;
}
function deletecampoFecha(idtag){
    var selectedOpts = $('#search_results_campoFecha_'+idtag+' option:selected');
    $(selectedOpts).remove();
    savecampoFechaConfig();
}

function fillBackupCurrent() {
    $.ajax({
        url: "/admin-dashboards/loadSolrLastBackUp",
        method: "POST",
        contentType: "text/plain",
        data: $("#backup_solr").val() + ";" + $("#backup_core").val(),
        success: function (data) {
            //console.log(data);
            $("#spanLastBackup").text(data);
            $("#backup_current_div").show();
            $("#backup_buttonDo_div").show();
        },
        error: function () {
            $("#spanLastBackup").text(" - ");
            $("#backup_current_div").show();
            $("#backup_buttonDo_div").show();
            alert("No se encontraron copias");
        }
    });

}


function fillBackUpSolrInsts() {
    $.ajax({
        url: "/admin-dashboards/loadSolrInsts",
        method: "POST",
        async: false,
        success: function (data) {
            //console.log(data);
            var SplitSchemaSolrInsts = data.split(";");
            for (var j = 0; j < SplitSchemaSolrInsts.length; j++) {
                if (SplitSchemaSolrInsts[j] !== null && SplitSchemaSolrInsts[j] !== "") {
                    var o = new Option(SplitSchemaSolrInsts[j], SplitSchemaSolrInsts[j]);
                    $(o).html(SplitSchemaSolrInsts[j]);
                    $("#backup_solr").append(o);
                }
            }
            
        },
        error: function () {
            alert("No se encontraron instancias de Solr instaladas");
        }
    });
}

function fillRestoreBackUpSolrInsts() {
    $.ajax({
        url: "/admin-dashboards/loadSolrBackUpInsts",
        method: "POST",
        success: function (data) {
            //console.log(data);
            
            var SplitSchemaSolrInsts = data.split(";");
            for (var j = 0; j < SplitSchemaSolrInsts.length; j++) {
                if (SplitSchemaSolrInsts[j] !== null && SplitSchemaSolrInsts[j] !== "" && SplitSchemaSolrInsts[j] !== "null") {
                    var o = new Option(SplitSchemaSolrInsts[j], SplitSchemaSolrInsts[j]);
                    $(o).html(SplitSchemaSolrInsts[j]);
                    $("#restore_solr").append(o);
                }
            }
           
        },
        error: function () {
            alert("No se encontraron instancias de Solr con copia de seguridad");
        }
    });
}

function fillRestoreBackupInstCores() {
    $.ajax({
        url: "/admin-dashboards/loadSolrBackUpCores",
        method: "POST",
        contentType: "text/plain",
        data: $("#restore_solr").val(),
        success: function (data) {
            $("#tableColecciones").find('tbody').empty()
            console.log("1"+data);
            var SplitSchemaCores = data.split(";");
            for (var j = 0; j < SplitSchemaCores.length; j++) {
                if (SplitSchemaCores[j] !== null && SplitSchemaCores[j] !== "null" && SplitSchemaCores[j] !== "") {
                    $("#tableColecciones").find('tbody')
                            .append($('<tr> <td><input name="optnsBackUp" onclick="getCheckedProgBackUp();" type="checkbox" id="' + SplitSchemaCores[j] + 'cbox" value="' + SplitSchemaCores[j] + '"></td>' + '<td>' + SplitSchemaCores[j] + '</td>'
                                    + '<td><select id ="opBack' + SplitSchemaCores[j] + '"></select></td>' + '</tr>'));
                    callBackUps($("#restore_solr").val(), SplitSchemaCores[j]);
                    
                }
            }
            $("#backupSolrTable").show();
        },
        error: function () {
            $("#tableColecciones").find('tbody').empty()
            alert("No se encontraron colecciones");
        }
    });
}

function fillBackupInstCores() {
    $.ajax({
        url: "/admin-dashboards/loadSolrCores",
        method: "POST",
        contentType: "text/plain",
        data: $("#backup_solr").val(),
        success: function (data) {
            $("#tableColecciones").find('tbody').empty()
            //console.log(data);
            var SplitSchemaCores = data.split(";");
            for (var j = 0; j < SplitSchemaCores.length; j++) {
                if (SplitSchemaCores[j] !== null && SplitSchemaCores[j] !== "") {
                    $("#tableColecciones").find('tbody')
                            .append($('<tr> <td><input name="optnsBackUp" onclick="getCheckedProgBackUp();" type="checkbox" id="' + SplitSchemaCores[j] + 'cbox" value="' + SplitSchemaCores[j] + '"></td>' + '<td>' + SplitSchemaCores[j] + '</td>'
                                    + '<td id ="tdLast' + SplitSchemaCores[j] + '"></td' + '</tr>'));
                    callLastBackUp($("#backup_solr").val(), SplitSchemaCores[j]);
                    
                }
            }
            $("#backupSolrTable").show();

        },
        error: function () {
            $("#tableColecciones").find('tbody').empty()
            alert("No se encontraron colecciones");
        }
    });
}