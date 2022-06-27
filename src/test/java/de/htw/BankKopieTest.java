package de.htw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankKopieTest {
    private Bank bank;
    @BeforeEach
    public void initBank(){
        bank = new Bank(1);
    }
    @Test
    public void bankKopie() throws CloneNotSupportedException {
        Bank kopie = bank.clone();
        assertEquals(bank.getBankleitzahl(), kopie.getBankleitzahl());
    }
    @Test
    public void bankMitKonto() throws CloneNotSupportedException {
        Kontofabrik kontofabrik = new GirokontoFabrik();
        bank.kontoErstellen(kontofabrik, new Kunde());
        Bank kopie = bank.clone();
        assertEquals(bank.getAlleKontonummern(), kopie.getAlleKontonummern());
    }
    @Test
    public void bankMitEinzahlen() throws CloneNotSupportedException, KontoDoesntExistException {
        Kontofabrik kontofabrik = new GirokontoFabrik();
        long nr = bank.kontoErstellen(kontofabrik, new Kunde());
        Bank kopie = bank.clone();
        bank.geldEinzahlen(nr, 1000);
        assertNotEquals(bank.getKontostand(nr), kopie.getKontostand(nr));
    }
    @Test
    public void bankKontoHinzufuegen() throws CloneNotSupportedException {
        Kontofabrik kontofabrik = new GirokontoFabrik();
        bank.kontoErstellen(kontofabrik, new Kunde());
        Bank kopie = bank.clone();
        bank.kontoErstellen(kontofabrik, new Kunde());
        assertNotEquals(bank.getAlleKontonummern(), kopie.getAlleKontonummern());

    }
}
