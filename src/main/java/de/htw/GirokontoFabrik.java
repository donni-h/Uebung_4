package de.htw;

public class GirokontoFabrik implements Kontofabrik{

    @Override
    public Konto erzeugen(Kunde k, long kontonummer) {
        return new Girokonto(k, kontonummer, 100);
    }
}
