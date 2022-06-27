package de.htw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/*
 Testet die Funktionalitäten fürs Überweisen der Bankklasse
 */
public class BankUeberweisungTests {
    /*
    Initialisiert eine Bank mit Konten vor jedem Test.
     */
    private Bank bank;
    private long girokonto1;
    private long girokonto2;
    private long sparbuch1;
    private long sparbuch2;
    @BeforeEach
    public void init(){
         bank = new Bank(123);
         Kontofabrik girokontofabrik = new GirokontoFabrik();
         Kontofabrik sparbuchfabrik = new SparbuchFabrik();
        girokonto1 = bank.kontoErstellen(girokontofabrik, new Kunde());
        girokonto2 = bank.kontoErstellen(girokontofabrik, new Kunde());
        sparbuch1 = bank.kontoErstellen(sparbuchfabrik, new Kunde());
        sparbuch2 = bank.kontoErstellen(sparbuchfabrik, new Kunde());
    }
    /*
    guter Test, sollte normal verlaufen
     */
    @Test
    public void testGirozuGiro() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1,1000);
        bank.geldEinzahlen(girokonto2,1000);
        assertTrue(bank.geldUeberweisen(girokonto1, girokonto2, 100, "Überweisungstext"));
        assertEquals(1100, bank.getKontostand(girokonto2));
        assertEquals(900, bank.getKontostand(girokonto1));
    }
    /*
    Überweisungsbetrag überschreitet Kontostand + Dispo
     */
    @Test
    public void testGirozuGiroZuViel() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1000);
        bank.geldEinzahlen(girokonto2, 1000);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto2, 11000, "Überweisungstext"));
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));

    }
    /*
    ein negativer Wert soll überwiesen werden
     */
    @Test
    public void testGirozuGiroNegativ() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1000);
        bank.geldEinzahlen(girokonto2, 1000);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto2, -1000, "Überweisungstext"));
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));
    }
    /*
    der Wert 0 soll überwiesen werden, Mindestbetrag sollte 0.01 sein.
     */
    @Test
    public void testGirozuGiroNull() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1,1000);
        bank.geldEinzahlen(girokonto2,1000);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto2, 0, "Überweisungstext"));
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));
    }
    /*
    Man kann von einem Sparbuch aus keine Überweisung tätigen
     */
    @Test
    public void sparbuchZuGiro() throws KontoDoesntExistException {
         bank.geldEinzahlen(sparbuch1, 10);
         try {
             bank.geldUeberweisen(sparbuch1, girokonto1, 1,"Überweisungstext");
             Assertions.fail();
         }catch (NichtUeberweisungsfaehigException ignored){}
         assertEquals(10,bank.getKontostand(sparbuch1));
    }
    /*
    Ein Sparbuch soll keine Überweisungen empfangen können.
     */
    @Test
    public void giroZuSparbuch() throws KontoDoesntExistException {
        bank.geldEinzahlen(girokonto1, 10);
        try {
            bank.geldUeberweisen(girokonto1,sparbuch1, 1,"Überweisungstext");
            Assertions.fail();
        }catch (NichtUeberweisungsfaehigException ignored){}
        assertEquals(10,bank.getKontostand(girokonto1));
    }
    @Test
    public void sparbuchZuSparbuch() throws KontoDoesntExistException{
        bank.geldEinzahlen(sparbuch1, 10);
        try {
            bank.geldUeberweisen(sparbuch1,sparbuch2, 1,"Überweisungstext");
            Assertions.fail();
        }catch (NichtUeberweisungsfaehigException ignored){}
        assertEquals(10,bank.getKontostand(sparbuch1));
    }
    /*
    Man kann nicht auf das gleiche Konto überweisen
     */
    @Test
    public void testUeberweisungAufGleichesKonto() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto1, 1, "Überweisungstext"));
        assertEquals(1,bank.getKontostand(girokonto1));
    }
    /*
    Ein Konto das nicht existiert kann nicht überweisen
     */
    @Test
    public void konto1DoesntExist() throws NichtUeberweisungsfaehigException {
        try {
            bank.geldUeberweisen(-1, girokonto1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){}
    }
    /*
    Man kann nicht an ein Konto überweisen, welches nicht existiert
     */
    @Test
    public void konto2DoesntExist() throws NichtUeberweisungsfaehigException, KontoDoesntExistException {
        bank.geldEinzahlen(girokonto1, 1);
        try {
            bank.geldUeberweisen(girokonto1, -1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){
            assertEquals(1,bank.getKontostand(girokonto1));
        }
    }
    @Test
    public void konto1And2DoesntExist() throws NichtUeberweisungsfaehigException {
        try {
            bank.geldUeberweisen(-2, -1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){}
    }
    /*
    Eine Überweisung muss einen Verwendungszweck beinhalten
     */
    @Test
    public void testGirozuGiroNoText() throws KontoDoesntExistException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1,1000);
        bank.geldEinzahlen(girokonto2,1000);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto2, 100, null));
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));
    }
    // Wie kann ich gesperrt testen, wenn ich von der Bank aus die Konten nicht ändern kann und keine externen Konten der Bank hinzufügen kann?


}
