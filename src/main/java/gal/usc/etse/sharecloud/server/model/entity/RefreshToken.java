package gal.usc.etse.sharecloud.server.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@SuppressWarnings("unused")
@RedisHash()
public class RefreshToken {
    @Id
    private String token;
    private String user;
    @TimeToLive
    private long ttl;

    public RefreshToken() { }

    public RefreshToken(String token, String user, long ttl) {
        this.token = token;
        this.user = user;
        this.ttl = ttl;
    }

    public String getToken() {
        return token;
    }

    public RefreshToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUser() {
        return user;
    }

    public RefreshToken setUser(String user) {
        this.user = user;
        return this;
    }

    public long getTtl() {
        return ttl;
    }

    public RefreshToken setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

}
