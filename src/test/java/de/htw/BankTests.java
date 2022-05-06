package de.htw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankTests {
    private Bank bank;
    private long girokonto1;
    private long girokonto2;
    private long sparbuch1;
    private long sparbuch2;
    @BeforeEach
    public void init(){
         bank = new Bank(123);
        girokonto1 = bank.girokontoErstellen(new Kunde());
        girokonto2 = bank.girokontoErstellen(new Kunde());
        sparbuch1 = bank.sparbuchErstellen(new Kunde());
        sparbuch2 = bank.sparbuchErstellen(new Kunde());
    }
    @Test
    public void testGirozuGiro() throws KontoDoesntExistException, GesperrtException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1,1000);
        bank.geldEinzahlen(girokonto2,1000);
        assertTrue(bank.geldUeberweisen(girokonto1, girokonto2, 100, "Überweisungstext"));
        assertEquals(1100, girokonto2);
        assertEquals(900, girokonto1);
    }
    @Test
    public void testGirozuGiroZuViel() throws KontoDoesntExistException, GesperrtException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1000);
        bank.geldEinzahlen(girokonto2, 1000);
        assertFalse(bank.geldUeberweisen(girokonto1, girokonto2, 11000, "Überweisungstext"));
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));

    }
    @Test
    public void testGirozuGiroNegativ() throws KontoDoesntExistException, GesperrtException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1000);
        bank.geldEinzahlen(girokonto2, 1000);
        try {
            bank.geldUeberweisen(girokonto1, girokonto2, 1100, "Überweisungstext");
            Assertions.fail();
        }catch (IllegalArgumentException ignored){}
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));
    }
    @Test
    public void testGirozuGiroNull() throws KontoDoesntExistException, GesperrtException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1,1000);
        bank.geldEinzahlen(girokonto2,1000);
        try {
            bank.geldUeberweisen(girokonto1, girokonto2, 0, "Überweisungstext");
            Assertions.fail();
        }catch (IllegalArgumentException ignored){}
        assertEquals(1000, bank.getKontostand(girokonto1));
        assertEquals(1000, bank.getKontostand(girokonto2));
    }
    @Test
    public void sparbuchZuGiro() throws KontoDoesntExistException, GesperrtException {
         bank.geldEinzahlen(sparbuch1, 10);
         try {
             bank.geldUeberweisen(sparbuch1, girokonto1, 1,"Überweisungstext");
             Assertions.fail();
         }catch (NichtUeberweisungsfaehigException ignored){}
         assertEquals(10,bank.getKontostand(sparbuch1));
    }
    @Test
    public void giroZuSparbuch() throws KontoDoesntExistException, GesperrtException {
        bank.geldEinzahlen(girokonto1, 10);
        try {
            bank.geldUeberweisen(girokonto1,sparbuch1, 1,"Überweisungstext");
            Assertions.fail();
        }catch (NichtUeberweisungsfaehigException ignored){}
        assertEquals(10,bank.getKontostand(girokonto1));
    }
    @Test
    public void sparbuchZuSparbuch() throws KontoDoesntExistException, GesperrtException {
        bank.geldEinzahlen(sparbuch1, 10);
        try {
            bank.geldUeberweisen(sparbuch1,sparbuch2, 1,"Überweisungstext");
            Assertions.fail();
        }catch (NichtUeberweisungsfaehigException ignored){}
        assertEquals(10,bank.getKontostand(sparbuch1));
    }
    @Test
    public void testUeberweisungAufGleichesKonto() throws KontoDoesntExistException, GesperrtException, NichtUeberweisungsfaehigException {
        bank.geldEinzahlen(girokonto1, 1);
        try {
            bank.geldUeberweisen(girokonto1, girokonto1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (NichtUeberweisungsfaehigException ignored){}
        assertEquals(1,bank.getKontostand(girokonto1));
    }
    @Test
    public void konto1DoesntExist() throws GesperrtException, NichtUeberweisungsfaehigException {
        try {
            bank.geldUeberweisen(-1, girokonto1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){}
    }
    @Test
    public void konto2DoesntExist() throws GesperrtException, NichtUeberweisungsfaehigException, KontoDoesntExistException {
        bank.geldEinzahlen(girokonto1, 1);
        try {
            bank.geldUeberweisen(girokonto1, -1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){}
    }
    @Test
    public void konto1And2DoesntExist() throws GesperrtException, NichtUeberweisungsfaehigException, KontoDoesntExistException {
        try {
            bank.geldUeberweisen(-2, -1, 1, "Überweisungstext");
            Assertions.fail();
        }catch (KontoDoesntExistException ignored){}
    }


    // Normalerweise würde ich noch nach gesperrt testen, jedoch sollten wir diese Funktionalität noch nicht implementieren...


}
