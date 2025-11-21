package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.gui_controller.cLog;
import gal.usc.etse.sharecloud.gui_controller.cSession;
import gal.usc.etse.sharecloud.clientModel.User;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FachadaGUI extends Application {
    private Stage entrarStage;

    private static HostServices hostServices;

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
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vLog.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 878, 415);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this);

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public void entrarSesion(User loggedUser){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vPrincipal.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            cSession controller = fxmlLoader.getController();
            controller.setFachadas(this, loggedUser);

            entrarStage.setTitle("Session");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }
}
