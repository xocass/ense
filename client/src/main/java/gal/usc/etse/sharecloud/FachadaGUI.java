package gal.usc.etse.sharecloud;

import gal.usc.etse.sharecloud.guiController.*;
import gal.usc.etse.sharecloud.http.SpotifyApi;
import gal.usc.etse.sharecloud.http.TokenManager;
import gal.usc.etse.sharecloud.http.UserApi;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyArtist;
import gal.usc.etse.sharecloud.model.dto.SpotifyItems.SpotifyTrack;
import gal.usc.etse.sharecloud.model.dto.SpotifyRecentlyPlayedResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopArtistsResponse;
import gal.usc.etse.sharecloud.model.dto.SpotifyTopTracksResponse;
import gal.usc.etse.sharecloud.model.entity.SpotifyResponseCompact;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class FachadaGUI {
    private static FachadaGUI instance;
    private Stage entrarStage;
    private HostServices hostServices;

    public FachadaGUI(Stage stage, HostServices hostServices) {
        instance = this;
        this.entrarStage = stage;
        this.entrarStage.setResizable(false);
        this.hostServices = hostServices;
    }

    public static FachadaGUI getInstance() {return instance;}
    public Stage getEntrarStage() {return entrarStage;}
    public void setEntrarStage(Stage entrarStage) {this.entrarStage = entrarStage;}




    public void iniciarSesion(int status) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vLog.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cLog controller = fxmlLoader.getController();
            if(status != 200 && status != 0){
                controller.updateStatus("Error "+ status +": El email o contraseña introducidos son incorrectos");
            }

            entrarStage.setTitle("Iniciar sesión");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void mostrarPantallaCarga(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vLoading.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            entrarStage.setTitle("Cargando");
            entrarStage.setScene(scene);
            entrarStage.show();

        } catch (IOException e) {e.printStackTrace();}
    }

    public void registroCrearCuenta(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vRegisterAccount.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cRegisterAccount controller = fxmlLoader.getController();

            entrarStage.setTitle("Registro");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void registroVincularSpotify(String email){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vRegisterLinkSpotify.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cRegisterLinkSpotify controller = fxmlLoader.getController();
            controller.setEmail(email);

            entrarStage.setTitle("Registro");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void registroCompletado(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vRegisterCompleted.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cRegisterCompleted controller = fxmlLoader.getController();

            entrarStage.setTitle("Registro");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void recuperarContrasenhaEmail(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vForgotPasswordEmail.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cForgotPasswordEmail controller = fxmlLoader.getController();

            entrarStage.setTitle("Recuperar contraseña");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void recuperarContrasenhaCodigo(String email){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vForgotPasswordCode.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cForgotPasswordCode controller = fxmlLoader.getController();
            controller.setEmail(email);

            entrarStage.setTitle("Recuperar contraseña");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}

    }

    public void recuperarContrasenhaActualizar(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vForgotPasswordUpdate.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cForgotPasswordUpdate controller = fxmlLoader.getController();
            controller.setEmail(email);

            entrarStage.setTitle("Recuperar contraseña");
            entrarStage.setScene(scene);
            entrarStage.show();

        } catch (Exception e) {e.printStackTrace();}
    }

    public void recuperarContrasenhaCompletado() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vForgotPasswordCompleted.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cForgotPasswordCompleted controller = fxmlLoader.getController();

            entrarStage.setTitle("Recuperar contraseña");
            entrarStage.setScene(scene);
            entrarStage.show();

        } catch (Exception e) {e.printStackTrace();}
    }


    public void irFeed(String email) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vFeed.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1200, 760);
            cFeed controller = fxmlLoader.getController();
            controller.setEmail(email);
            controller.setMenuUsername(TokenManager.getUsername());
            if(TokenManager.getImage() != null){
                Image pic = new Image(TokenManager.getImage());
                controller.getMenuUserPicture().setImage(pic);
            }

            entrarStage.setTitle("Sesión: Feed");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){e.printStackTrace();}
    }

    public void verCurrPerfil(SpotifyResponseCompact data, String userEmail){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vProfileUser.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(),1200,760);
            cProfile controller = fxmlLoader.getController();
            controller.setUserEmail(userEmail);
            controller.setMenuUsername(TokenManager.getUsername());
            controller.setLoggedUser(data.getProfileView());
            if(data.getProfileView().getProfileURL() == null){
                controller.getBtnGoSpotify().setDisable(true);
            }else{
                controller.getBtnGoSpotify().setDisable(false);
                controller.setSpotifyURL(data.getProfileView().getProfileURL());
            }
            if(TokenManager.getImage() != null){
                Image pic = new Image(TokenManager.getImage());
                controller.getMenuUserPicture().setImage(pic);
            }

            if(data.getProfileView().getImage()!=null) {
                Image pfp = new Image(data.getProfileView().getImage());
                controller.setPfp(pfp);
            }
            controller.setUsername(data.getProfileView().getDisplayName());
            controller.setCountry(data.getProfileView().getCountry());
            controller.setFollowers(data.getProfileView().getNFollowers());


            _cargarRecentlyPlayed(controller, data.getRecentlyPlayed());
            _cargarTopTracks(controller, data.getTopTracks());
            _cargarTopArtistas(controller, data.getTopArtists());

            entrarStage.setTitle("Sesión: Perfil de usuario");
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){System.err.println("IOException: "+e.getMessage());
        }catch(Exception e){System.err.println("Exception(getUser): "+e.getMessage());}
    }

    public void verOtroPerfil(SpotifyResponseCompact data, String userEmail){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ShareCloudBoot.class.getResource("/gal/usc/etse/sharecloud/layouts/vProfileOther.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(),1200,760);
            cProfile controller = fxmlLoader.getController();
            System.out.println("pre menu pic");
            controller.setUserEmail(userEmail);
            controller.setMenuUsername(TokenManager.getUsername());
            controller.setLoggedUser(data.getProfileView());
            controller.setSpotifyURL(data.getProfileView().getProfileURL());
            if(TokenManager.getImage() != null){
                Image pic = new Image(TokenManager.getImage());
                controller.getMenuUserPicture().setImage(pic);
            }
            controller.setIsFriend(data.getUserBooleans().isFriend());
            controller.updateFriendButton();
            System.out.println("prfId: "+ data.getUserBooleans().userId());
            controller.setProfileUserId(data.getUserBooleans().userId());

            if(data.getProfileView().getImage()!=null) {
                Image pfp = new Image(data.getProfileView().getImage());
                controller.setPfp(pfp);
            }
            controller.setUsername(data.getProfileView().getDisplayName());
            controller.setCountry(data.getProfileView().getCountry());
            controller.setFollowers(data.getProfileView().getNFollowers());
            controller.setSeguido(data.getUserBooleans().isFollowing());

            _cargarRecentlyPlayed(controller, data.getRecentlyPlayed());
            _cargarTopTracks(controller, data.getTopTracks());
            _cargarTopArtistas(controller, data.getTopArtists());

            entrarStage.setTitle("Sesión: Perfil de " + data.getProfileView().getDisplayName());
            entrarStage.setScene(scene);
            entrarStage.show();

        }catch(IOException e){System.err.println("IOException: "+e.getMessage());
        }catch(Exception e){System.err.println("Exception(getUser): "+e.getMessage());}
    }

    private void _cargarRecentlyPlayed(cProfile controller, SpotifyRecentlyPlayedResponse response){
        controller.clearRecentlyPlayed();
        for (SpotifyRecentlyPlayedResponse.Item item : response.items()) {
                //cargar el template
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/gal/usc/etse/sharecloud/layouts/vTemplateRecentTrack.fxml")
                );
                Parent template;
                try {
                    template = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                gal.usc.etse.sharecloud.guiController.cTemplateRecentTrack cTemplate = loader.getController();

                //recuperar datos del response
                String trackName = item.track().name();

                String artists = item.track().artists()
                        .stream()
                        .map(SpotifyArtist::name)
                        .collect(Collectors.joining(", "));

                String imageUrl = null;
                if (item.track().album() != null
                        && item.track().album().images() != null
                        && !item.track().album().images().isEmpty()) {
                    imageUrl = item.track().album().images().getFirst().url();
                }

                Integer duration = item.track().duration_ms();
                //settear datos del template y añadir al hbox
                cTemplate.setName(trackName);
                cTemplate.setArtist(artists);
                if (imageUrl != null) cTemplate.setImage(new Image(imageUrl));
                cTemplate.setDuration(duration);

                controller.addRecentlyPlayed(template);
        }
    }
    public void _cargarTopTracks(cProfile controller, SpotifyTopTracksResponse response) {
        controller.clearTopTracks();
        Integer counter = 0;
        for (SpotifyTrack track : response.items()) {
            counter++;
            //cargar el template
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gal/usc/etse/sharecloud/layouts/vItemTemplate.fxml")
            );
            Parent template;
            try {
                template = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            gal.usc.etse.sharecloud.guiController.cItemTemplate cTemplate=loader.getController();

            //recuperar datos de cada
            String trackName = track.name();
            String artists = track.artists()
                    .stream()
                    .map(SpotifyArtist::name)
                    .collect(Collectors.joining(", "));

            String imageUrl = null;
            if (track.album() != null
                    && track.album().images() != null
                    && !track.album().images().isEmpty()) {
                imageUrl = track.album().images().getFirst().url();
            }

            //settear los datos al template y añadirlo al hbox
            cTemplate.setName(trackName);
            cTemplate.setArtist(artists);
            cTemplate.setRank(counter.toString());
            if (imageUrl != null) {
                cTemplate.setImage(new Image(imageUrl, true));
            }
            controller.addTopTrack(template);
        }
    }
    public void _cargarTopArtistas(cProfile controller, SpotifyTopArtistsResponse response) {
        controller.clearTopArtist();
        Integer counter=0;
        for (SpotifyTopArtistsResponse.Item artist : response.items()) {
            counter++;
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gal/usc/etse/sharecloud/layouts/vItemTemplate.fxml")
            );
            Parent template;
            try {
                template = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            gal.usc.etse.sharecloud.guiController.cItemTemplate cTemplate = loader.getController();

            //recuperar datos del response
            String artistName = artist.name();

            String imageUrl = null;
            if (artist.images() != null && !artist.images().isEmpty()) {
                imageUrl = artist.images().getFirst().url();
            }

            //settear los datos del template y añadirlo al hbox
            cTemplate.setName(artistName);
            cTemplate.invLabel();
            cTemplate.setRank(counter.toString());
            if (imageUrl != null) {
                cTemplate.setImage(new Image(imageUrl, true));
            }
            controller.addTopArtist(template);
        }
    }

    public void doFollow(String otherSpotifyID){
        try {
            SpotifyApi.doFollow(otherSpotifyID,TokenManager.getUserID());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void doUnfollow(String otherSpotifyID){
        try {
            SpotifyApi.doUnfollow(otherSpotifyID,TokenManager.getUserID());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
