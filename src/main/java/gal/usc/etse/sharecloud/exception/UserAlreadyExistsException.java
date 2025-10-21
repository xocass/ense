package gal.usc.etse.sharecloud.exception;

public class UserAlreadyExistsException extends Throwable {

    public UserAlreadyExistsException() {
        super("Ya existe un usuario con este correo electrónico");
    }
}
