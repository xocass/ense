package gal.usc.etse.sharecloud;

/*
     ¡OLLO! Antes de nada:
             - Ter instalado jdk-21
             - Declarar en terminales a emplear:
                        $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
                        $env:Path="$env:JAVA_HOME\bin;" + $env:Path
     Como runnear:
     1. Buildear desde root / do proxecto:          ./gradlew clean build
     2. Runnear en 1 terminal servidor primetro:    ./gradlew :server:bootRun
     3. Runnear en outra terminal cliente:          ./gradlew :client:run
 */

import gal.usc.etse.sharecloud.guiController.cLog;
import gal.usc.etse.sharecloud.guiController.cSession;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FachadaGUI extends Application {
    private Stage entrarStage;
    private static HostServices hostServices;


    public Stage getEntrarStage() {return entrarStage;}
    public void setEntrarStage(Stage entrarStage) {this.entrarStage = entrarStage;}

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
            Scene scene = new Scene(fxmlLoader.load(), 878, 422);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this);

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public void entrarSesion(String email){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    FachadaGUI.class.getResource("/gal/usc/etse/sharecloud/layouts/vPrincipal.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            cSession controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setFachadas(this);

            entrarStage.setTitle("Session");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }
}
