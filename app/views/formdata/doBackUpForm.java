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
public class doBackUpForm {
    public String collectionNameOutSolrToSolr;
    public String rowsOutSolrToSolr;
    public String ipOutSolrToSolr;
    public String portOutSolrToSolr;
    public String dnsOutSolrToSolr;
    public String collectionNameInSolrToSolr;
    public String ipInSolrToSolr;
    public String portInSolrToSolr;
    public String dnsInSolrToSolr;
    public String fieldsdHiddenSolrToSolr;
    
     /**
     * Validates Form<uploadFileForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
               
        if( (dnsOutSolrToSolr==null||dnsOutSolrToSolr.length()==0) ){
            
            if((ipOutSolrToSolr==null||ipOutSolrToSolr.length()==0) && (portOutSolrToSolr==null||portOutSolrToSolr.length()==0) ){
            errors.add(new ValidationError("ipOutSolrToSolr", "Debe ingresar la direccion ip"));
            errors.add(new ValidationError("portOutSolrToSolr", "Debe ingresar el puerto"));
        }
            
            if((ipOutSolrToSolr==null||ipOutSolrToSolr.length()==0) && (portOutSolrToSolr!=null||portOutSolrToSolr.length()!=0) ){
            errors.add(new ValidationError("ipOutSolrToSolr", "Debe ingresar la direccion ip"));
            
        }
        
            if((ipOutSolrToSolr!=null||ipOutSolrToSolr.length()!=0) && (portOutSolrToSolr==null||portOutSolrToSolr.length()==0) ){
            errors.add(new ValidationError("portOutSolrToSolr", "Debe ingresar el puerto"));
            
        }    
            
        }
        if((ipOutSolrToSolr==null||ipOutSolrToSolr.length()==0) && (portOutSolrToSolr==null||portOutSolrToSolr.length()==0) ){
            if( (dnsOutSolrToSolr==null||dnsOutSolrToSolr.length()==0) ){
                errors.add(new ValidationError("dnsOutSolrToSolr", "Debe ingresar un DNS de destino "));
            }
        }
        
        if(rowsOutSolrToSolr==null||rowsOutSolrToSolr.length()==0||Integer.parseInt(rowsOutSolrToSolr)<1){
            errors.add(new ValidationError("rowsOutSolrToSolr", "Debe ingresar un numero de filas"));
        }
        if( (collectionNameOutSolrToSolr==null||collectionNameOutSolrToSolr.length()==0) ){
            errors.add(new ValidationError("collectionNameOutSolrToSolr", "Debe ingresar nombre de coleccion"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
    
}
