package de.htw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class KontoListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()){
            case "Waehrung":
                System.out.println("Die Währung wurde verändert " + e.getOldValue() +" --> "+ e.getNewValue());
            case "Kontostand":
                System.out.println("Der Kontostand wurde verändert " + e.getOldValue() +" --> "+ e.getNewValue());
            case "Kunde":
                System.out.println("Der Kunde wurde verändert " + e.getOldValue().toString() +System.lineSeparator()+ e.getNewValue().toString());
            case "Sperrung":
                System.out.println("Der Sperrungsstatus wurde angepasst: " + e.getNewValue());
            case "Aktie":
                System.out.println("Die Anzahl der Aktie wurde geändert: "+ e.getOldValue()+" --> "+e.getNewValue());
        }
    }
}
