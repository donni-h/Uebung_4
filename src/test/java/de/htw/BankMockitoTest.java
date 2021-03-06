package de.htw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class BankMockitoTest {
    Bank bank;
    Kunde kunde1, kunde2, kunde3, kunde4;
    Girokonto giroMock1, giroMock2;
    Sparbuch sparMock1, sparMock2;
    SparbuchFabrik sparbuchFabrik;

    GirokontoFabrik girokontoFabrik;
    long giro1, giro2, spar1, spar2;

    private void fillBank(){
        giro1 = bank.kontoErstellen(girokontoFabrik, kunde1);
        giro2 = bank.kontoErstellen(girokontoFabrik, kunde2);
        spar1 = bank.kontoErstellen(sparbuchFabrik, kunde3);
        spar2 = bank.kontoErstellen(sparbuchFabrik, kunde4);
    }

    /*
    Bei der init Methode habe ich etwas bei Ihrer Musterlösung nachgeschaut.
     */
    @BeforeEach
    public void init() throws Exception {
        sparbuchFabrik = Mockito.mock(SparbuchFabrik.class);
        girokontoFabrik = Mockito.mock(GirokontoFabrik.class);
        Mockito.when(girokontoFabrik.erzeugen(ArgumentMatchers.same(kunde1), ArgumentMatchers.anyLong())).thenReturn(giroMock1);
        Mockito.when(girokontoFabrik.erzeugen(ArgumentMatchers.same(kunde2), ArgumentMatchers.anyLong())).thenReturn(giroMock2);
        Mockito.when(sparbuchFabrik.erzeugen(ArgumentMatchers.same(kunde3), ArgumentMatchers.anyLong())).thenReturn(sparMock1);
        Mockito.when(sparbuchFabrik.erzeugen(ArgumentMatchers.same(kunde4), ArgumentMatchers.anyLong())).thenReturn(sparMock2);
        bank = new Bank(17122000); //Bank die getestet wird
        kunde1 = new Kunde();
        kunde2 = new Kunde();
        kunde3 = new Kunde();
        kunde4 = new Kunde();
        giroMock1 = Mockito.mock(Girokonto.class);
        Mockito.when(giroMock1.abhebenSpecific(ArgumentMatchers.anyDouble())).thenReturn(true); // Passt das so?
        Mockito.when(giroMock1.getInhaber()).thenReturn(kunde1);
        Mockito.when(giroMock1.getKontostand()).thenReturn(4761D);
        Mockito.when(giroMock1.getKontonummer()).thenReturn(1L);
        Mockito.when(giroMock1.isGesperrt()).thenReturn(false);
        Mockito.when(giroMock1.getDispo()).thenReturn(500D);
        Mockito.when(giroMock1.ueberweisungAbsenden(ArgumentMatchers.anyDouble(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),ArgumentMatchers.anyString())).thenReturn(true);
        giroMock2 = Mockito.mock(Girokonto.class);
        Mockito.when(giroMock2.abhebenSpecific(ArgumentMatchers.anyDouble())).thenReturn(true);
        Mockito.when(giroMock2.getInhaber()).thenReturn(kunde2);
        Mockito.when(giroMock2.getKontostand()).thenReturn(360D);
        Mockito.when(giroMock2.getKontonummer()).thenReturn(2L);
        Mockito.when(giroMock2.isGesperrt()).thenReturn(false);
        Mockito.when(giroMock2.getDispo()).thenReturn(1000D);
        Mockito.when(giroMock2.ueberweisungAbsenden(ArgumentMatchers.anyDouble(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),ArgumentMatchers.anyString())).thenReturn(true);
        sparMock1 = Mockito.mock(Sparbuch.class);
        Mockito.when(sparMock1.abhebenSpecific(ArgumentMatchers.anyDouble())).thenReturn(true);
        Mockito.when(sparMock1.getInhaber()).thenReturn(kunde3);
        Mockito.when(sparMock1.getKontostand()).thenReturn(100D);
        Mockito.when(sparMock1.getKontonummer()).thenReturn(3L);
        Mockito.when(sparMock1.isGesperrt()).thenReturn(false);
        sparMock2 = Mockito.mock(Sparbuch.class);
        Mockito.when(sparMock2.abhebenSpecific(ArgumentMatchers.anyDouble())).thenReturn(true);
        Mockito.when(sparMock2.getInhaber()).thenReturn(kunde4);
        Mockito.when(sparMock2.getKontostand()).thenReturn(300D);
        Mockito.when(sparMock2.getKontonummer()).thenReturn(4L);
        Mockito.when(sparMock2.isGesperrt()).thenReturn(false);
    }

    @Test
    public void testBank(){
        long blz = bank.getBankleitzahl();
        assertEquals(17122000, blz);
        String konten = bank.getAlleKonten();
        assertEquals("", konten);
        try {
            bank.geldEinzahlen(123456, 1);
            fail("Konto existiert nicht.");
        } catch (KontoDoesntExistException ignored){}

    }

    @Test
    public void testKontenEinfuegen(){
        fillBank();
        List<Long> kontenliste = bank.getAlleKontonummern();
        assertTrue(kontenliste.containsAll(Arrays.asList(1L,2L,3L,4L)));
        assertEquals(4, kontenliste.size());
        assertTrue(giroMock1 != giroMock2 && sparMock1 != sparMock2);
    }
    @Test
    public void testEinzahlen() throws KontoDoesntExistException {
        fillBank();
        bank.geldEinzahlen(1,1);
        bank.geldEinzahlen(2,2);
        bank.geldEinzahlen(3,3);
        bank.geldEinzahlen(4,4);
        Mockito.verify(giroMock1).einzahlen(1);
        Mockito.verify(giroMock2).einzahlen(2);
        Mockito.verify(sparMock1).einzahlen(3);
        Mockito.verify(sparMock2).einzahlen(4);
    }
    @Test
    public void testEinzahlenKeinKonto(){
        //kein fillBank();
        try {
            bank.geldEinzahlen(giro1,1);
            Assertions.fail();
        } catch (KontoDoesntExistException ignored) {
        }
    }
    @Test
    public void testEinzahlenMinus() throws KontoDoesntExistException {
        fillBank();
        Mockito.doThrow(new IllegalArgumentException("negativer Wert beim Einzahlen.")).when(giroMock1).einzahlen(ArgumentMatchers.anyDouble()); // Müsste eigtl <0 sein, aber hier egal
        try {
            bank.geldEinzahlen(giro1,-1);
            Assertions.fail();
        } catch (IllegalArgumentException ignored){
        }
    }
    @Test
    public void testAbheben() throws GesperrtException, KontoDoesntExistException {
        fillBank();
        assertTrue(bank.geldAbheben(giro1, 1));
        Mockito.verify(giroMock1).abheben(1);
    }
    @Test
    public void testAbhebenKeinKonto() throws GesperrtException {
        //kein fillBank();
        try {
            bank.geldAbheben(giro1,1);
            Assertions.fail();
        } catch (KontoDoesntExistException ignored) {
        }
    }
}
