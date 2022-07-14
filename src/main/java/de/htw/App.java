package de.htw;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class App extends Application {
    private Girokonto giro;
    public App(){
        giro = new Girokonto(new Kunde(), 1, 10000);
    }
    public void einzahlen(double betrag){
        giro.einzahlen(betrag);
    }
    public void abheben(double betrag){
        try {
            giro.abheben(betrag);
        } catch (GesperrtException e) {
            throw new RuntimeException(e);
        }
    }
    private Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        Parent kontoOberflaeche = new KontoOberflaeche(giro, this);
        Scene scene = new Scene(kontoOberflaeche);
        stage.setScene(scene);
        stage.setTitle("Kontooberfläche");
        stage.show();
    }
    public static void main(String[] args){
        launch();
    }
}
