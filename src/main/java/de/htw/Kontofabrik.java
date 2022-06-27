package de.htw;

public interface Kontofabrik {
    /**
     * erzeugt ein neues Konto.
     * @param k Inhaber des Kontos
     * @return frisch erzeugtes Konto.
     */
    Konto erzeugen(Kunde k, long kontonummer);
}
