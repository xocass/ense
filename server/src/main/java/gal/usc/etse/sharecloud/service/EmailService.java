package gal.usc.etse.sharecloud.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
public class EmailService {
    @Value("${resend.api-key}")
    private String apiKey;



    public void sendPasswordRecoveryEmail(String to, String code) {
        Resend resend = new Resend(apiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("ShareCloud <onboarding@resend.dev>")
                .to(to)
                .subject("ShareCloud – Recuperación de contraseña")
                .html("""
                        <p>Tu código de recuperación es:</p>
                        <h2>%s</h2>
                        <p>Este código caduca en 10 minutos.</p>
                        """.formatted(code))
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            // response.getId() si quieres guardarlo/loguearlo
        } catch (ResendException e) {
            throw new RuntimeException("Resend error sending email", e);
        }
    }
}
