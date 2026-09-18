<<<<<<<< HEAD:src/main/java/com/lacouf/rsbjwt/Exception/MotDePasseNonCorrespondantException.java
package com.lacouf.rsbjwt.Exception;
========
package com.lacouf.rsbjwt.Exceptions;
>>>>>>>> 5e17f52693d3e0246402190ab7fea2a1ed782244:src/main/java/com/lacouf/rsbjwt/Exceptions/MotDePasseNonCorrespondantException.java

public class MotDePasseNonCorrespondantException extends Exception {
    public MotDePasseNonCorrespondantException() {
        super("Les mots de passe ne correspondent pas");
    }
}
