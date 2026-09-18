<<<<<<<< HEAD:src/main/java/com/lacouf/rsbjwt/Exception/EmailExistantException.java
package com.lacouf.rsbjwt.Exception;
========
package com.lacouf.rsbjwt.Exceptions;
>>>>>>>> 5e17f52693d3e0246402190ab7fea2a1ed782244:src/main/java/com/lacouf/rsbjwt/Exceptions/EmailExistantException.java

public class EmailExistantException extends Exception{
    public EmailExistantException(){
        super("Ce courriel existe déjà");
    }
}
