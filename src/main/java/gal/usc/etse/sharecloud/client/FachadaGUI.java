package gal.usc.etse.sharecloud.client;

import gal.usc.etse.sharecloud.client.gui_controller.cLog;
import gal.usc.etse.sharecloud.client.gui_controller.cSession;
import gal.usc.etse.sharecloud.client.clientModel.User;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FachadaGUI extends Application {
    private Stage entrarStage;

    private static HostServices hostServices;

    /*DESACOPLAMIENTO FGUI Y SPRING
    private static UserService userService;
    private static AuthService authService;
    private static UserController userController;


    public static void setUserService(UserService us) { userService = us; }
    public static void setAuthService(AuthService as) { authService = as; }
    public static void setUserController(UserController uc) { userController = uc; }
    public UserService getUserService() { return userService; }
    public AuthService getAuthService() { return authService; }
    public UserController getUserController() { return userController; }
     */

    //public static HostServices getHostServicesInstance() {return hostServices;}
    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage){
        entrarStage=stage;
        entrarStage.setResizable(false);
        hostServices = getHostServices();
        iniciarSesion();
    }

    /* Función que muestra la pantalla de inicio de sesión */
    public void iniciarSesion() {
        System.out.println("\n\n>>>>>>>> ARRANCADO <<<<<<<<<\n\n");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Thread.currentThread().getContextClassLoader().getResource(
                            "gal/usc/etse/sharecloud/layouts/vLog.fxml"
                    )
            );
            Scene scene = new Scene(fxmlLoader.load(), 878, 415);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this);
            controller.setHostServices(getHostServices());

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public void entrarSesion(User loggedUser){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Thread.currentThread().getContextClassLoader().getResource(
                            "gal/usc/etse/sharecloud/layouts/vPrincipal.fxml"
                    )
            );
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            cSession controller = fxmlLoader.getController();
            controller.setFachadas(this, loggedUser);
            controller.setHostServices(getHostServices());

            entrarStage.setTitle("Session");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }
}
