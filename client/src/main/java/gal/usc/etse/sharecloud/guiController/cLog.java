package gal.usc.etse.sharecloud.guiController;

import gal.usc.etse.sharecloud.FachadaGUI;
import gal.usc.etse.sharecloud.http.AuthApi;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class cLog {
    @FXML private TextField fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label statusLabel;
    @FXML private Hyperlink forgotPasswordLink;

    private String email;
    private String password;


    @FXML
    private void initialize(){
        statusLabel.setText("");
        fieldEmail.setOnAction(e -> callLogin());
        fieldPassword.setOnAction(e -> callLogin());
    }

    @FXML
    private void clickOnLogin() {
        callLogin();
    }

    private void callLogin() {
        statusLabel.setText("");
        this.email = fieldEmail.getText();
        this.password = fieldPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            updateStatus("Email y contraseña requeridos.");
            return;
        }

        FachadaGUI.getInstance().mostrarPantallaCarga();

        Task<Integer> loginTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return AuthApi.login(email, password);
            }
        };
        loginTask.setOnSucceeded(e -> {
            int status = loginTask.getValue();

            if (status == 200) {
                FachadaGUI.getInstance().irFeed(email);
            } else {
                FachadaGUI.getInstance().iniciarSesion(status);
            }
        });

        loginTask.setOnFailed(e -> {
            int status = loginTask.getValue();
            updateStatus("Error "+ status +": " + loginTask.getException());
            FachadaGUI.getInstance().iniciarSesion(status);
        });
        new Thread(loginTask).start();
    }

    @FXML
    private void clickOnRegister() {
        FachadaGUI.getInstance().registroCrearCuenta();
    }

    @FXML
    private void clickOnForgotPassword() {
        FachadaGUI.getInstance().recuperarContrasenhaEmail();
    }

    public void updateStatus(String msg) {
        javafx.application.Platform.runLater(() -> statusLabel.setText(msg));
    }
}
