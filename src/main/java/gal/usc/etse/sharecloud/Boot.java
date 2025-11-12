package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.db.Connection;
import gal.usc.etse.sharecloud.gui_controller.cLog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Boot extends Application {
    private Stage entrarStage;

    @Override
    public void start(Stage stage){
        entrarStage=stage;
        entrarStage.setResizable(false);

        iniciarSesion();
    }

    /* Función que muestra la pantalla de inicio de sesión */
    public void iniciarSesion() {
        try {
            System.out.println("CL Resource = " +
                    Thread.currentThread().getContextClassLoader().getResource(
                            "gal/usc/etse/sharecloud/layouts/vLog.fxml"));

            FXMLLoader fxmlLoader = new FXMLLoader(
                    Thread.currentThread().getContextClassLoader().getResource(
                            "gal/usc/etse/sharecloud/layouts/vLog.fxml"
                    )
            );
            Scene scene = new Scene(fxmlLoader.load(), 450, 550);
            cLog controller = fxmlLoader.getController();
            controller.setFachadas(this);
            controller.setHostServices(getHostServices());

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();
        }catch(IOException e){System.err.println("IOException: "+e.getMessage());}
    }

    public static void main(String[] args) {
        Connection db= new Connection();
        SpringApplication.run(Boot.class, args);
        Application.launch(args);
    }


}
