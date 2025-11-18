package gal.usc.etse.sharecloud.exception;

import gal.usc.etse.sharecloud.model.entity.User;

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

