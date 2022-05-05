package de.htw;

public class KontoDoesntExistException extends Exception {
    public KontoDoesntExistException(long kontonummer)
    {
        super("Zu dieser Kontonummer existiert kein Konto..." + kontonummer);
    }
}
