package de.htw;

public class SparbuchFabrik implements Kontofabrik{
    @Override
    public Konto erzeugen(Kunde k, long kontonummer) {
        return new Sparbuch(k, kontonummer);
    }
}
