package de.htw;

import java.util.*;

/**
 * Stellt eine Bank dar (Finanzinstitut)
 */
public class Bank {
    // Liste aller Konten
    private Map<Long, Konto> kontoliste = new HashMap<>();
    private long nummern = 0;
    private long bankleitzahl;

    /**
     *
     * @param bankleitzahl Bankleitzahl der Bank
     */
    public Bank(long bankleitzahl){
        this.bankleitzahl = bankleitzahl;
    }

    /**
     *
     * @return Bankleitzahl des Kontos
     */
    public long getBankleitzahl() {
        return bankleitzahl;
    }

    /**
     *  Erstellt ein neues Girokonto
     * @param inhaber inhaber des neuen Kontos
     * @return Kontonummer
     */
    public long girokontoErstellen(Kunde inhaber){
        long kontonummer = nummern++;
        Konto konto = new Girokonto(inhaber, kontonummer, 100);
        kontoliste.put(kontonummer, konto);
        return kontonummer;
    }

    /**
     * Erstellt ein neues Sparbuch
     * @param inhaber Inhaber des Kontos
     * @return Kontonummer
     */
    public long sparbuchErstellen(Kunde inhaber){
        long kontonummer = nummern++;
        Konto konto = new Sparbuch(inhaber, kontonummer);
        kontoliste.put(kontonummer, konto);
        return kontonummer;
    }

    /**
     * Gibt alle Konten der Bank in Textform zurück "Kontonummer: $kontonummer Inhaber: $inhaber"
     * @return Stringliste aller Konten
     */
    public String getAlleKonten(){
        StringBuilder builder = new StringBuilder();
        for (Konto k: kontoliste.values()) {
            builder.append("Kontonummer: ").append(k.getKontonummer()).append(" Inhaber: ").append(k.getInhaber()).append("\n"); // IntelliJ wollte die chained append() calls haben
        }
        return builder.toString();
    }

    /**
     * Gibt eine Liste aller Kontonummern zurück
     * @return Liste von Kontonummern
     */
    public List<Long> getAlleKontonummern(){
        List<Long> nummernListe = new LinkedList<>(kontoliste.keySet());
        Collections.sort(nummernListe); // Sortierung war nicht gefordert, ich mach es aber trotzdem...
        return nummernListe;
    }

    /**
     * Hebt Geld von einem Konto ab
     * @param von Kontonummer
     * @param betrag abzuhebener Betrag
     * @return  true - hat geklappt, false - hat nicht geklappt
     * @throws GesperrtException Wenn das Konto gesperrt ist
     * @throws KontoDoesntExistException Zur Kontonummer ist kein Konto zugehörig
     */
    public boolean geldAbheben(long von, double betrag) throws GesperrtException, KontoDoesntExistException {
        if (!kontoliste.containsKey(von))
            throw new KontoDoesntExistException(von);
        Konto k = kontoliste.get(von);
        return k.abheben(betrag);

    }

    /**
     * Zahlt Geld auf ein Konto ein
     * @param auf Kontonummer
     * @param betrag einzuzahlender Betrag
     * @throws KontoDoesntExistException Zur Kontonummer ist kein Konto zugehörig
     */
    public void geldEinzahlen(long auf, double betrag) throws KontoDoesntExistException {
        if (!kontoliste.containsKey(auf))
            throw new KontoDoesntExistException(auf);
        kontoliste.get(auf).einzahlen(betrag);

    }

    /**
     *  Löscht ein Konto
     * @param nummer Kontonummer
     * @return true - Konto wurde gelöscht, false - Konto existiert nicht
     */
    public boolean kontoLoeschen(long nummer){
        return kontoliste.remove(nummer) != null;
    }

    /**
     * Gibt Kontostand zurück
     * @param nummer Kontonummer
     * @return Kontostand
     * @throws KontoDoesntExistException Zur Kontonummer ist kein Konto zugehörig
     */
    public double getKontostand(long nummer) throws KontoDoesntExistException {
        if (kontoliste.containsKey(nummer))
            return kontoliste.get(nummer).getKontostand();
        throw new KontoDoesntExistException(nummer);
    }
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) throws NichtUeberweisungsfaehigException, GesperrtException, KontoDoesntExistException{
    return false;
    }
}
