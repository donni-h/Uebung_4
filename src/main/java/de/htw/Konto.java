package de.htw;

import javafx.beans.property.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * stellt ein allgemeines Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable
{
	/** 
	 * der Kontoinhaber
	 */
	private Kunde inhaber;
	private Map<Aktie, Long> aktiendepot;
	/**
	 * Waehrung in der das Konto geführt wird.
	 */
	private Waehrung waehrung = Waehrung.EUR;
	/**
	 * die Kontonummer
	 */
	private final long nummer;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * meldet einen Beobachter am Konto an
	 * @param b Listener der das Konto beobachtet
	 */
	public void anmelden(PropertyChangeListener b){
		propertyChangeSupport.addPropertyChangeListener(b);
	}

	/**
	 * meldet einen Beobachter vom Konto ab
	 * @param b Listener der vom Konto entfernt werden soll
	 */
	public void abmelden(PropertyChangeListener b){
		propertyChangeSupport.removePropertyChangeListener(b);
	}
	/**
	 * der aktuelle Kontostand
	 */
	private ReadOnlyDoubleWrapper kontostand;

	/**
	 * Gibt die Währung in der das Konto geführt wird wieder.
	 * @return Währung in der das Konto geführt wird
	 */
	public Waehrung getAktuelleWaehrung() {
		return waehrung;
	}
	/**
	 * Wechselt die Währung des Kontos, in der das Konto geführt wird.
	 * @param w Währung zu der gewechselt werden soll.
	 */
	public void waehrungswechsel(Waehrung w) {
		setKontostand(w.euroInWaehrungUmrechnen(this.getAktuelleWaehrung().waehrungInEuroUmrechnen(this.getKontostand())));
		Waehrung tmp = this.waehrung;
		this.waehrung = w;
		propertyChangeSupport.firePropertyChange("Waehrung", tmp, w);
	}
	/**
	 * setzt den aktuellen Kontostand
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		this.kontostand.set(kontostand);
		this.negative.set(kontostand<0);
	}
	public ReadOnlyDoubleProperty kontostandProperty() {
		return this.kontostand.getReadOnlyProperty();
	}

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), können keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers wären (abheben, Inhaberwechsel)
	 */
	private BooleanProperty gesperrt;

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anfängliche Kontostand wird auf 0 gesetzt.
	 *
	 * @param inhaber der Inhaber
	 * @param kontonummer die gewünschte Kontonummer
	 * @throws IllegalArgumentException wenn der Inhaber null
	 */
	public Konto(Kunde inhaber, long kontonummer) {
		if(inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostand = new ReadOnlyDoubleWrapper();
		gesperrt = new SimpleBooleanProperty();
		negative = new SimpleBooleanProperty();
		this.setKontostand(0);
		this.gesperrt.set(false);
		this.aktiendepot = Collections.synchronizedMap(new HashMap<>());
	}
	
	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567);
	}

	/**
	 * liefert den Kontoinhaber zurück
	 * @return   der Inhaber
	 */
	public Kunde getInhaber() {
		return this.inhaber;
	}
	
	/**
	 * setzt den Kontoinhaber
	 * @param kinh   neuer Kontoinhaber
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException{
		if (kinh == null)
			throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
		if(this.gesperrt.get())
			throw new GesperrtException(this.nummer);
		Kunde tmp = this.inhaber;
		this.inhaber = kinh;
		propertyChangeSupport.firePropertyChange("Kunde", tmp, inhaber);
	}
	
	/**
	 * liefert den aktuellen Kontostand
	 * @return   double
	 */
	public double getKontostand() {
		return kontostand.get();
	}

	/**
	 * liefert die Kontonummer zurück
	 * @return   long
	 */
	public long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zurück, ob das Konto gesperrt ist oder nicht
	 * @return true, wenn das Konto gesperrt ist
	 */
	public boolean isGesperrt() {
		return gesperrt.get();
	}

	/**
	 * Zahlt den einen Betrag in der angegebenen Währung ein.
	 * @param betrag double
	 * @param w Währung
	 * @throws IllegalArgumentException wenn der Betrag negativ ist
	 */
	public void einzahlen(double betrag, Waehrung w){
		einzahlen(getAktuelleWaehrung().euroInWaehrungUmrechnen(w.waehrungInEuroUmrechnen(betrag)));
	}
	/**
	 * Erhöht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist 
	 */
	public void einzahlen(double betrag) {
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		double tmp = getKontostand();
		setKontostand(getKontostand() + betrag);
		propertyChangeSupport.firePropertyChange("Kontostand", tmp, getKontostand());
	}
	
	/**
	 * Gibt eine Zeichenkettendarstellung der Kontodaten zurück.
	 */
	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
				+ System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}

	public Map<Aktie, Long> getAktiendepot() {
		return aktiendepot;
	}
	
	/*
	public void ausgeben()
	{
		System.out.println(this.toString());
	}
	*/

	/**
	 * Hebt einen Betrag einer gewissen Währung vom Konto ab.
	 * @param betrag Betrag der abgehoben werden soll
	 * @param w Währung die abgehoben werden soll
	 * @return true wenn Abhebung erfolgreich, false wenn Abhebung nicht erfolgreich
	 * @throws GesperrtException wird geworfen wenn Konto gesperrt ist
	 */
	public boolean abheben(double betrag, Waehrung w) throws GesperrtException{
		 return abheben(getAktuelleWaehrung().euroInWaehrungUmrechnen(w.waehrungInEuroUmrechnen(betrag))); // rechnet abzuhebende Währung in Euro um und dann Euro in Währung des Kontos.

	}
	/**
	 * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist.
	 *
	 * @param betrag double
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ ist 
	 * @return true, wenn die Abhebung geklappt hat, 
	 * 		   false, wenn sie abgelehnt wurde
	 */
	public final boolean abheben(double betrag) throws GesperrtException{
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Betrag ungültig");
		}
		if(this.isGesperrt())
			throw new GesperrtException(this.getKontonummer());
		double tmp = getKontostand();
		boolean b = abhebenSpecific(betrag);
		if (b)
			propertyChangeSupport.firePropertyChange("Kontostand", tmp, getKontostand());
		return b;
	}
	public BooleanProperty gesperrtProperty(){
		return gesperrt;
	}

	/**
	 * Gibt an, ob Kontostand negativ ist.
	 */
	private BooleanProperty negative;
	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr möglich.
	 */
	public final void sperren() {
		propertyChangeSupport.firePropertyChange("Sperrung", this.gesperrt, true);
		this.gesperrt.set(true);

	}
	protected abstract boolean abhebenSpecific(double betrag);
	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder möglich.
	 */
	public final void entsperren() {
		propertyChangeSupport.firePropertyChange("Sperrung", this.gesperrt, false);
		this.gesperrt.set(false);
	}
	
	
	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public BooleanProperty negativeProperty(){
		return this.negative;
	}
	public final String getGesperrtText()
	{
		if (this.gesperrt.get())
		{
			return "GESPERRT";
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * liefert die ordentlich formatierte Kontonummer
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert()
	{
		return String.format("%10d", this.nummer);
	}
	
	/**
	 * liefert den ordentlich formatierten Kontostand
	 * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol €
	 */
	public String getKontostandFormatiert()
	{
		return String.format("%10.2f " + getAktuelleWaehrung().toString(), this.getKontostand());
	}
	
	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich,
	 * wen sie die gleiche Kontonummer haben
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other)
	{
		if(this == other)
			return true;
		if(other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		return this.nummer == ((Konto) other).nummer;
	}
	
	@Override
	public int hashCode()
	{
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
	}

	@Override
	public int compareTo(Konto other)
	{
		return Long.compare(this.getKontonummer(), other.getKontonummer());
	}

	/**
	 * Erstellt einen Kaufauftrag für Aktien, die bis zu einem gewissen Preis/stück gekauft werden sollen, in der geünschten Anzahl
	 * @param a Aktie die gekauft werden soll
	 * @param anzahl zu kaufende Anzahl
	 * @param hoechstpreis Preis den Aktie maximal haben darf
	 * @return ges. Kaufpreis
	 */
	public Future<Double> kaufauftrag(Aktie a, int anzahl, double hoechstpreis){
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Callable<Double> aC = () -> {
			Lock l = a.getLock();
			Condition c = a.getCondition();
			double preis;
				do {
				try {
					l.lock();
					c.await();
				} catch (InterruptedException ignored){}
				finally {
					preis = anzahl * a.getKurs();
					l.unlock();
				}
			}while (a.getKurs() > hoechstpreis);
				l.lock();

				if (this.abheben(preis)) {
					long tmp = 0;
					if (aktiendepot.containsKey(a)) {
						tmp = aktiendepot.get(a);
						aktiendepot.put(a, aktiendepot.get(a) + anzahl);
					}
					else aktiendepot.put(a, (long) anzahl);
					propertyChangeSupport.firePropertyChange("Aktie", tmp, aktiendepot.get(a));
				}
				else {
					preis = 0;
				}
				l.unlock();
				return preis;
		};
		return executorService.submit(aC);
	}

	/**
	 * Erstellt einen Verkaufsauftrag, der beim Erreichen eines gewissen Kurses der gewünschten Aktie, alle Aktien mit dieser WKN verkauft.
	 * @param wkn Wertkennnummer der Aktie
	 * @param minimalpreis Minimaler preis der erreicht sein muss um zu verkaufen
	 * @return Wert der erhalten wurde
	 */
	public Future<Double> verkaufsauftrag(String wkn, double minimalpreis){

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Callable<Double> aC = () -> {
			if (aktiendepot.keySet().stream().anyMatch(k -> k.getWertpapierKennnummer().equals(wkn))){
				for (Aktie aktie : aktiendepot.keySet()) {
					if (aktie.getWertpapierKennnummer().equals(wkn)) {
						double preis;
						Lock l = aktie.getLock();
						Condition c = aktie.getCondition();
						do {
							try {
								l.lock();
								c.await();
							} catch (InterruptedException ignored) {
							} finally {
								preis = aktie.getKurs() * aktiendepot.get(aktie);
								l.unlock();
							}
						} while (aktie.getKurs() < minimalpreis);
						long tmp = aktiendepot.get(aktie);
						aktiendepot.remove(aktie);
						propertyChangeSupport.firePropertyChange("Aktie", tmp, 0);
						this.einzahlen(preis);
						return preis;
					}
				}
		}
			return Double.valueOf(0);
		};
		return executorService.submit(aC);
	}
}