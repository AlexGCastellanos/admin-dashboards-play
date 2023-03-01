/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class LoadSolrToSolrForm {
    public String collectionNameInSolrToSolr;
    public String ipInSolrToSolr;
    public String rowsOutSolrToSolr;
    public String portInSolrToSolr;
    public String dnsInSolrToSolr;
    
     /**
     * Validates Form<uploadFileForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
               
        if( (dnsInSolrToSolr==null||dnsInSolrToSolr.length()==0) ){
            if((ipInSolrToSolr==null||ipInSolrToSolr.length()==0)){
            errors.add(new ValidationError("ipInSolrToSolr", "Debe ingresar la direccion IP de origen"));
            errors.add(new ValidationError("portInSolrToSolr", "Debe ingresar el puerto"));
            errors.add(new ValidationError("dnsInSolrToSolr", "Debe ingresar un DNS"));
        }
            
            
        }
        if((ipInSolrToSolr!=null||ipInSolrToSolr.length()!=0) && (portInSolrToSolr==null||portInSolrToSolr.length()==0) ){
            errors.add(new ValidationError("portInSolrToSolr", "Debe ingresar el puerto"));
        }
        if((ipInSolrToSolr==null||ipInSolrToSolr.length()==0) && (portInSolrToSolr!=null||portInSolrToSolr.length()!=0) ){
            errors.add(new ValidationError("ipInSolrToSolr", "Debe ingresar la IP de origen"));
        }
        if(collectionNameInSolrToSolr==null||collectionNameInSolrToSolr.length()==0){
            errors.add(new ValidationError("collectionNameInSolrToSolr", "Debe ingresar el nombre de la coleccion de origen"));
        }
        if(rowsOutSolrToSolr==null||rowsOutSolrToSolr.length()==0||Integer.parseInt(rowsOutSolrToSolr)<1){
            errors.add(new ValidationError("collectionNameInSolrToSolr", "Debe ingresar un numero de filas"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
    
}
