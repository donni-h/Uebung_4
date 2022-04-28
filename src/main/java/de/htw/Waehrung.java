package de.htw;

import org.decimal4j.util.DoubleRounder;

/**
 * stellt Fiatwährungen dar.
 */
public enum Waehrung {
    EUR(1), BGN(1/1.9558), DKK(1/7.4604), MKD(1/61.62);


    public final double valueInEuro;
    Waehrung(double valueInEuro) {
        this.valueInEuro = valueInEuro;
    }

    /**
     * Gibt in Euro angegebenen Betrag in der Wahrung wieder.
     * @param betrag Betrag in Euro
     * @return Betrag in jeweiliger Wahrung
     */
    public double euroInWaehrungUmrechnen(double betrag){
        return DoubleRounder.round(betrag/this.valueInEuro, 2) ;
    }

    /**
     * Gibt in Wahrung angegebenen Betrag in Euro wieder.
     * @param betrag Betrag in der jeweiligen Wahrung
     * @return Betrag in Euro
     */
    public double waehrungInEuroUmrechnen(double betrag){
        return DoubleRounder.round(betrag*this.valueInEuro, 2);
    }
}
