package de.htw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KontoTests {

    public Konto getKonto(Waehrung w, double kontostand){
       Konto konto = new Girokonto();
       konto.waehrungswechsel(w);
       konto.setKontostand(kontostand);
       return konto;
    }
    @Test
    public void testAbhebenEuroEuro() throws GesperrtException {
        Konto konto = getKonto(Waehrung.EUR, 1000);
        assertTrue(konto.abheben(100, Waehrung.EUR));
        assertEquals(900, konto.getKontostand(), 0.1);
    }
    @Test
    public void testAbhebenDKKEuro() throws GesperrtException {
        Konto konto = getKonto(Waehrung.DKK, 1000);
        assertTrue(konto.abheben(100, Waehrung.EUR));
        assertEquals(1000-Waehrung.DKK.euroInWaehrungUmrechnen(100), konto.getKontostand(), 0.1);
    }
    @Test
    public void testGesperrt() throws GesperrtException {
        Konto konto = getKonto(Waehrung.EUR, 1000);
        konto.sperren();
        assertTrue(konto.isGesperrt());
        try {
            konto.abheben(1000, Waehrung.EUR);
            Assertions.fail();
        } catch (GesperrtException ignored){

        }
    }
    @Test
    public void testEinzahlenEuroEuro(){
        Konto konto = getKonto(Waehrung.EUR, 1000);
        konto.einzahlen(10, Waehrung.EUR);
        assertEquals(1010, konto.getKontostand(), 0);
    }
    @Test
    public void testEinzahlenDKKEuro(){
        Konto konto = getKonto(Waehrung.DKK, 1000);
        konto.einzahlen(10, Waehrung.EUR);
        assertEquals(1000 + Waehrung.DKK.euroInWaehrungUmrechnen(10), konto.getKontostand(), 0);
    }
    @Test
    public void testWaehrungsWechsel(){
        Konto konto = getKonto(Waehrung.EUR, 1000);
        konto.waehrungswechsel(Waehrung.DKK);
        assertEquals(Waehrung.DKK, konto.getAktuelleWaehrung());
        assertEquals(Waehrung.DKK.euroInWaehrungUmrechnen(1000), konto.getKontostand(), 0);
    }
}
