package de.htw;

public class NichtUeberweisungsfaehigException extends Exception{
    public NichtUeberweisungsfaehigException(long kontonummer)
    {
        super("Konto ist nicht überweisungsfähig... Kontonummer: " + kontonummer);
    }
    public NichtUeberweisungsfaehigException(String message)
    {
        super(message);
    }
}
