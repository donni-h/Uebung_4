package de.htw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    @Test
    public void testIllegalArgumentEinzahlen(){
        Konto konto = getKonto(Waehrung.EUR, 10);
        try {
            konto.einzahlen(-1, konto.getAktuelleWaehrung());
            Assertions.fail();
        }catch (IllegalArgumentException ignored){}
    }
    @Test
    public void testEventListenerEinzahlen() {
       Konto konto = getKonto(Waehrung.EUR,1000);
       PropertyChangeListener mockListener = Mockito.mock(KontoListener.class);
       konto.anmelden(mockListener);
       konto.einzahlen(10);
       konto.abmelden(mockListener);
       konto.einzahlen(10);
        Mockito.verify(mockListener).propertyChange(ArgumentMatchers.any());
    }
    @Test
    public void testEventListenerAbheben() throws GesperrtException {
        Konto konto = getKonto(Waehrung.EUR,1000);
        PropertyChangeListener mockListener = Mockito.mock(KontoListener.class);
        konto.anmelden(mockListener);
        konto.abheben(10);
        konto.abmelden(mockListener);
        konto.abheben(19);
        Mockito.verify(mockListener).propertyChange(ArgumentMatchers.any());
    }
    @Test
    public void testEventListenerWaehrung(){
        Konto konto = getKonto(Waehrung.EUR,1000);
        PropertyChangeListener mockListener = Mockito.mock(KontoListener.class);
        konto.anmelden(mockListener);
        konto.waehrungswechsel(Waehrung.DKK);
        konto.abmelden(mockListener);
        konto.waehrungswechsel(Waehrung.EUR);
        Mockito.verify(mockListener).propertyChange(ArgumentMatchers.any());
    }
    @Test
    public void testEventListenerAktie() throws InterruptedException, ExecutionException {
        Konto konto = getKonto(Waehrung.EUR,1000);
        PropertyChangeListener mockListener = Mockito.mock(KontoListener.class);
        konto.anmelden(mockListener);
        Aktie aktie = new Aktie("Aktie", "1");
        Future<Double> f = konto.kaufauftrag(aktie, 1, 3);
        f.get();
        Mockito.verify(mockListener, Mockito.times(2)).propertyChange(ArgumentMatchers.any());
    }
}
