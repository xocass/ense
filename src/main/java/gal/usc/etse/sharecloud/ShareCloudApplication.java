package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.db.Connection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareCloudApplication {

    public static void main(String[] args) {
        Connection db= new Connection();
        SpringApplication.run(ShareCloudApplication.class, args);
    }

}
