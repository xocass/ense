package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;


@SuppressWarnings("unused")
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String token;
    private String user;
    @Indexed(name = "expires_idx", expireAfter = "0s")
    private Date expiresAt;

    public RefreshToken() { }

    public RefreshToken(String token, String user, long ttlSeconds) {
        this.token = token;
        this.user = user;
        // se convierte a Date para que TTL funcione
        this.expiresAt = Date.from(Instant.now().plusSeconds(ttlSeconds));
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

}
