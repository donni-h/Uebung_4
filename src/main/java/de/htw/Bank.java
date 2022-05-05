package de.htw;

import java.util.*;

public class Bank {
    private Map<Long, Konto> kontoliste = new HashMap<>();
    private long bankleitzahl;
    public Bank(long bankleitzahl){
        this.bankleitzahl = bankleitzahl;
    }

    public long getBankleitzahl() {
        return bankleitzahl;
    }

    public long girokontoErstellen(Kunde inhaber){
        long kontonummer = kontoliste.size();
        Konto konto = new Girokonto(inhaber, kontonummer, 100);
        kontoliste.put(kontonummer, konto);
        return kontonummer;
    }
    public long sparbuchErstellen(Kunde inhaber){
        long kontonummer = kontoliste.size();
        Konto konto = new Sparbuch(inhaber, kontonummer);
        kontoliste.put(kontonummer, konto);
        return kontonummer;
    }
    public String getAlleKonten(){
        StringBuilder builder = new StringBuilder();
        for (Konto k: kontoliste.values()) {
            builder.append("Kontonummer: ").append(k.getKontonummer()).append(" Inhaber: ").append(k.getInhaber()).append("\n"); // IntelliJ wollte die chained append() calls haben
        }
        return builder.toString();
    }
    public List<Long> getAlleKontonummern(){
        List<Long> nummernListe = new LinkedList<>(kontoliste.keySet()); // die IDE hat das so schön gekürzt :) Ich wünschte das wäre meine Idee...
        Collections.sort(nummernListe); // Sortierung war nicht gefordert, ich mach es aber trotzdem...
        return nummernListe;
    }
    public boolean geldAbheben(long von, double betrag) throws GesperrtException {
        if (!kontoliste.containsKey(von))
            return false;
        Konto k = kontoliste.get(von);
        return k.abheben(betrag);

    }
    public void geldEinzahlen(long auf, double betrag){
        if (kontoliste.containsKey(auf))
            kontoliste.get(auf).einzahlen(betrag);
    }
    public boolean kontoLoeschen(long nummer){
        return kontoliste.remove(nummer) != null;
    }
    public double getKontostand(long nummer) throws KontoDoesntExistException {
        if (kontoliste.containsKey(nummer))
            return kontoliste.get(nummer).getKontostand();
        throw new KontoDoesntExistException(nummer);
    }
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck){

    }
}
