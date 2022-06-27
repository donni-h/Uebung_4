package de.htw;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
Finanzaktien.
 */
public class Aktie implements Serializable {
    //Name der Aktie
    private String name;
    //Kennnummer der Aktie
    private String wertpapierKennnummer;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Lock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public String getWertpapierKennnummer() {
        return wertpapierKennnummer;
    }

    /**
     * Kurs zu dem die Aktie gehandelt wird
     */
    private double kurs;

    /**
     *
     * @param name name der Aktie
     * @param wertpapierKennnummer Kennnummer der Aktie
     */
    public Aktie(String name, String wertpapierKennnummer){
        this.name = name;
        this.wertpapierKennnummer = wertpapierKennnummer;
        Runnable kursAenderung = () -> {
            Random r = new Random();
            double scale = Math.pow(10, 2);
            lock.lock();
            condition.signalAll();
            lock.unlock();
            kurs = Math.round((ThreadLocalRandom.current().nextDouble(-3, 3)) * scale) / scale;
        };
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(kursAenderung, 0, 1, TimeUnit.SECONDS);

    }

    /**
     * gibt den jetzigen Kurs der aktie wieder
     * @return Kurs in Währung
     */
    public double getKurs(){
        return this.kurs;
    }
}
