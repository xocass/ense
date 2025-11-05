package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.db.Connection;
import gal.usc.etse.sharecloud.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareCloudApplication {

    public static void main(String[] args) {
        Connection db= new Connection();
        SpringApplication.run(ShareCloudApplication.class, args);
        User prueba = new User("hola","hola","hola",13,"holaland","holalopolis");

    }

}
