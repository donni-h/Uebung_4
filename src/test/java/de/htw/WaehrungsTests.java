package de.htw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testet die Funktionalität der Währungsmethoden.
 */
public class WaehrungsTests {
    @Test
    public void euroInEuroTest(){
        Waehrung w = Waehrung.EUR;
        assertEquals(w.euroInWaehrungUmrechnen(100), 100, 0.01);
    }
    @Test
    public void euroInBGNTest(){
        Waehrung w = Waehrung.BGN;
        assertEquals(w.euroInWaehrungUmrechnen(1), 1.9558, 0.01);
    }
    @Test
    public void euroInMKDTest(){
        Waehrung w = Waehrung.MKD;
        assertEquals(w.euroInWaehrungUmrechnen(1), 61.62, 0.01);
    }
    @Test
    public void euroInDKKTest(){
        Waehrung w = Waehrung.DKK;
        assertEquals(w.euroInWaehrungUmrechnen(1), 7.4604, 0.01);
    }
    @Test
    public void bgnInEuroTest(){
        Waehrung w = Waehrung.BGN;
        assertEquals(w.waehrungInEuroUmrechnen(1), 1/1.9558, 0.01);
    }
    @Test
    public void mkdInEuroTest(){
        Waehrung w = Waehrung.MKD;
        assertEquals(w.waehrungInEuroUmrechnen(1), 1/61.62, 0.01);

    }
    @Test
    public void dkkInEuroTest(){
        Waehrung w = Waehrung.DKK;
        assertEquals(w.waehrungInEuroUmrechnen(1), 1/7.4604, 0.01);
    }
}
