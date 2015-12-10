package co.edu.udea.cmovil.gr11.gcmdemogr11;

/**
 * Created by USER on 08/12/2015.
 */
public class ChatObject {
    String message;

    public String getType(){

        return type;
    }

    String type;

    public ChatObject(String message, String type){
        this.message = message;
        this.type = type;
    }

    String getMessage (){

        return message;
    }

    public void setMessage(String message){

        this.message = message;
    }
}
