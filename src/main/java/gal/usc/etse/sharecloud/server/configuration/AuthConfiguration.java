package gal.usc.etse.sharecloud.server.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class AuthConfiguration {
    @Value("${keystore.location:classpath:keys.p12}")
    private String ksLocation;
    @Value("${keystore.password:123456789}")
    private String ksPassword;
    @Value("${keystore.private.password:123456789}")
    private String keyPassword;
    @Value("${keystore.private.name:jwt}")
    private String keyName;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public KeyPair jwtSignatureKeys() {
        try {
            ClassPathResource resource = new ClassPathResource(ksLocation);

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(resource.getInputStream(), ksPassword.toCharArray());

            PublicKey publicKey = ks.getCertificate(keyName).getPublicKey();
            PrivateKey privateKey = (PrivateKey) ks.getKey(keyName, keyPassword.toCharArray());

            return new KeyPair(publicKey, privateKey);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

