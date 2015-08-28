/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package request;

/**
 *
 * @author Diogo
 */
public class EnumerationRequestType {
    
    public enum requestType{
        
        EXIT("Exit", 1, "Fechar programa"),
        CREATE("Create", 1, "Enviar arquivo"),
        DELETE("Delete", 2, "Deletar arquivo"),
        OPEN("Open", 3, "Abrir arquivo"),
        APPEND("Append", 4, "Anexar a um arquivo"),
        CLOSE("Close", 5, "Fechar arquivo"),
        CREATEDIR("CreateDir", 101, "Criar diretório"),
        DELETEDIR("DeleteDir", 102, "Deletar diretório"),
        OPENDIR("OpenDir", 103, "Abrir diretório"),
        JOIN("Join", 1001, "DataServer join");

        private final int operationID;
        private final String operationName;
        private final String operationDescription;
        
        private requestType(String operationName, int operationID, String operationDescription){
            this.operationName = operationName;
            this.operationID = operationID;
            this.operationDescription = operationDescription;
        }
        
        public String getOperationName(){
            return this.operationName;
        }
        
        public int getOperationID() {
            return operationID;
        }

        public String getOperationDescription() {
            return operationDescription;
        }
        
        public String getOpertion(){
            return operationID + ": " + operationDescription;
        }
    }
    
}
