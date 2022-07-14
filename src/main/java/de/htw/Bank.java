package de.htw;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Stellt eine Bank dar (Finanzinstitut)
 */
public class Bank implements Cloneable, Serializable {
    // Liste aller Konten
    private TreeMap<Long, Konto> kontoliste = new TreeMap<>();
    private long nummern = 1;
    private long bankleitzahl;

    /**
     * @param bankleitzahl Bankleitzahl der Bank
     */
    public Bank(long bankleitzahl) {
        this.bankleitzahl = bankleitzahl;
    }

    /**
     * Sperrt alle Konten mit negativem Kontostand
     */
    public void pleitegeierSperren() {
        kontoliste.values().stream().filter(v -> v.getKontostand() < 0).forEach(Konto::sperren);
    }

    /**
     * Gibt alle Kunden wieder, die ein Konto haben, was einen Kontostand über einem gewissen Wert hat.
     *
     * @param minimum minimaler Wert der auf dem Konto sein muss.
     * @return Liste von Kunden auf die es zutrifft
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum) {
        Set<Kunde> setReich = new HashSet<>(); // Sorgt dafür, dass keine Doppelungen kommen.
        kontoliste.values().stream().filter(v -> v.getKontostand() >= minimum).forEach(k -> setReich.add(k.getInhaber()));
        return new LinkedList<>(setReich);
    }

    /**
     * Gibt einen String mit einer Liste von allen Kunden wieder sortiert nach ihrem Geburtstag
     *
     * @return String von Kunden
     */
    public String getKundenGeburtstage() {
        Comparator<Kunde> bDayComparator = Comparator.comparing(Kunde::getGeburtstag);
        TreeSet<Kunde> kundenset = kontoliste.values().stream().map(Konto::getInhaber).collect(Collectors.toCollection(() -> new TreeSet<>(bDayComparator)));
        return kundenset.stream().map(kunde -> kunde.getName() + ": " + kunde.getGeburtstag()).collect(Collectors.joining(System.lineSeparator(), "{", "}"));
    }

    /**
     * Gibt alle Kontonummern zurück die nicht belegt sind, bis zur momentan höchsten.
     *
     * @return Liste von Kontonummern.
     */
    public List<Long> getKontonummernLuecken() {
        return LongStream.range(1, nummern).filter(i -> !kontoliste.containsKey(i)).boxed().toList(); // Wenn das funktioniert, ist das ein cleaner Oneliner
    }

    /**
     * @return Bankleitzahl des Kontos
     */
    public long getBankleitzahl() {
        return bankleitzahl;
    }

    /**
     * Erstellt ein neues Girokonto
     *
     * @param inhaber inhaber des neuen Kontos
     * @return Kontonummer
     */

    public long kontoErstellen(Kontofabrik fabrik, Kunde inhaber){
        long kontonummer = nummern++;
        Konto k = fabrik.erzeugen(inhaber, kontonummer);
        kontoliste.put(kontonummer, k);
        return kontonummer;
    }
    /**
     * Erstellt ein neues Sparbuch
     *
     * @param inhaber Inhaber des Kontos
     * @return Kontonummer
     */

    /**
     * Gibt alle Konten der Bank in Textform zurück "Kontonummer: $kontonummer Inhaber: $inhaber"
     *
     * @return Stringliste aller Konten
     */
    public String getAlleKonten() {
        StringBuilder builder = new StringBuilder();
        for (Konto k : kontoliste.values()) {
            builder.append("Kontonummer: ").append(k.getKontonummer()).append(" Inhaber: ").append(k.getInhaber()).append("\n"); // IntelliJ wollte die chained append() calls haben
        }
        return builder.toString();
    }

    /**
     * Gibt eine Liste aller Kontonummern zurück
     *
     * @return Liste von Kontonummern
     */
    public List<Long> getAlleKontonummern() {
        List<Long> nummernListe = new LinkedList<>(kontoliste.keySet());
        Collections.sort(nummernListe); // Sortierung war nicht gefordert, ich mach es aber trotzdem...
        return nummernListe;
    }

    /**
     * Hebt Geld von einem Konto ab
     *
     * @param von    Kontonummer
     * @param betrag abzuhebener Betrag
     * @return true - hat geklappt, false - hat nicht geklappt
     * @throws GesperrtException         Wenn das Konto gesperrt ist
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
     *
     * @param auf    Kontonummer
     * @param betrag einzuzahlender Betrag
     * @throws KontoDoesntExistException Zur Kontonummer ist kein Konto zugehörig
     */
    public void geldEinzahlen(long auf, double betrag) throws KontoDoesntExistException {
        if (!kontoliste.containsKey(auf))
            throw new KontoDoesntExistException(auf);
        kontoliste.get(auf).einzahlen(betrag);

    }

    /**
     * Löscht ein Konto
     *
     * @param nummer Kontonummer
     * @return true - Konto wurde gelöscht, false - Konto existiert nicht
     */
    public boolean kontoLoeschen(long nummer) {
        return kontoliste.remove(nummer) != null;
    }

    /**
     * Gibt Kontostand zurück
     *
     * @param nummer Kontonummer
     * @return Kontostand
     * @throws KontoDoesntExistException Zur Kontonummer ist kein Konto zugehörig
     */
    public double getKontostand(long nummer) throws KontoDoesntExistException {
        if (kontoliste.containsKey(nummer))
            return kontoliste.get(nummer).getKontostand();
        throw new KontoDoesntExistException(nummer);
    }

    /**
     * Diese Methode überweist Geld von einem überweisungsfähigem Konto auf ein anderes.
     *
     * @param vonKontonr       Kontonummer von der aus das Geld geschickt wird
     * @param nachKontonr      Kontonummer an die das Geld überwiesen wird
     * @param betrag           Betrag der überwiesen werden soll
     * @param verwendungszweck Verwendungszweck der Überweisung
     * @return true, wenn Überweisung funktioniert hat. false, wenn Überweisung abgewiesen wurde.
     * @throws NichtUeberweisungsfaehigException
     * @throws KontoDoesntExistException
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) throws NichtUeberweisungsfaehigException, KontoDoesntExistException { //Ich habe mich dazu entschieden diese beiden Exception weiterzugeben und die anderen in der Methode selbst zu handlen.
        /*
        Falls es eine bessere Alternative zu dem else if-Block gibt, bitte melden :)
         */
        if (!kontoliste.containsKey(vonKontonr))
            throw new KontoDoesntExistException(vonKontonr);
        else if (!kontoliste.containsKey(nachKontonr))
            throw new KontoDoesntExistException(nachKontonr);
        else if (!(kontoliste.get(vonKontonr) instanceof Ueberweisungsfaehig) || !(kontoliste.get(nachKontonr) instanceof Ueberweisungsfaehig))
            throw new NichtUeberweisungsfaehigException("Eines der Konten ist nicht überweisungsfähig.");
        else if (vonKontonr == nachKontonr)
            return false;
        Konto von = kontoliste.get(vonKontonr);
        Konto nach = kontoliste.get(nachKontonr);
        double vonKontostand = von.getKontostand(); // Zwischenspeichern vom Kontostand, falls es fehlschlägt
        try {
            if (!((Ueberweisungsfaehig) von).ueberweisungAbsenden(betrag, nach.getInhaber().getName(), nachKontonr, this.bankleitzahl, verwendungszweck)) // wenn Überweisung fehlschlägt, abbrechen...
                return false;
            ((Ueberweisungsfaehig) nach).ueberweisungEmpfangen(betrag, von.getInhaber().getName(), vonKontonr, this.bankleitzahl, verwendungszweck);
            return true;
        } catch (IllegalArgumentException | GesperrtException e) {
            von.setKontostand(vonKontostand);
            return false;
        }
    }

    /**
     * Gibt eine tiefe Kopie der Bank zurück
     * @return Kopie der Bank
     */
    @Override
    public Bank clone() {
        byte[] bankBytes = new byte[0];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            bankBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bankBytes); ObjectInputStream in = new ObjectInputStream(bis)) {
            return (Bank) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}