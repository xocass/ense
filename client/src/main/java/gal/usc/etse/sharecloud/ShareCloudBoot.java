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

import javafx.application.Application;
import javafx.stage.Stage;

public class ShareCloudBoot extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage){
        FachadaGUI fgui= new FachadaGUI(stage, getHostServices());
        fgui.iniciarSesion(0);
    }

}



