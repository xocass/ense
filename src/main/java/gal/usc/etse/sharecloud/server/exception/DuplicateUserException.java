package gal.usc.etse.sharecloud.server.exception;

import gal.usc.etse.sharecloud.server.model.entity.User;

public class DuplicateUserException extends RuntimeException {
    private final User user;

    public DuplicateUserException(User user) {
        super("User already exists!");
        this.user = user;

    }

    public User getUser() {
        return user;
    }
}

