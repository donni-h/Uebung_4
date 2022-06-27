package de.htw;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

public class Main {
    /**
     * Testprogramm für Konten
     * @param args wird nicht benutzt
     */
    public static void main(String[] args) throws GesperrtException, KontoDoesntExistException, InterruptedException {
        Kunde ich = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));
        Girokonto girokonto1 = new Girokonto();
        Girokonto girokonto2 = new Girokonto();
        Girokonto girokonto3= new Girokonto();
        Aktie aktie1 = new Aktie("aktie1", "1");
        Aktie aktie2 = new Aktie("aktie2", "2");
        Aktie aktie3 = new Aktie("aktie3", "3");

        Runnable aktientest1 = new Aktientest(girokonto1, aktie1);
        Runnable aktientest2 = new Aktientest(girokonto2, aktie2);
        Runnable aktientest3 = new Aktientest(girokonto3, aktie3);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(aktientest1);
        executorService.submit(aktientest2);
        executorService.submit(aktientest3);
        executorService.shutdown();
        while (!executorService.isTerminated()){
            Thread.sleep(1000);
        }
        System.exit(0);
    }
    private static class Aktientest implements Runnable{
        Girokonto girokonto;
        Aktie aktie;
        public Aktientest(Girokonto girokonto, Aktie aktie){
            this.aktie = aktie;
            this.girokonto = girokonto;
        }
        @Override
        public void run() {
            Future<Double> kaufFuture = girokonto.kaufauftrag(aktie, 3, 2.5);
            while (!kaufFuture.isDone()){
                try {
                    System.out.println(aktie.getWertpapierKennnummer()+" "+aktie.getKurs() + "€");
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            try {
                System.out.println("---- Kaufspreis von Aktie" + aktie.getWertpapierKennnummer()+" "+kaufFuture.get());
                System.out.println("---- Jetziger Kontostand: "+girokonto.getKontostand());
            } catch (InterruptedException | ExecutionException ignored) {
            }
            Future<Double> verkaufsFuture = girokonto.verkaufsauftrag(aktie.getWertpapierKennnummer(), 2.5);
            while (!verkaufsFuture.isDone()){
                try {
                    System.out.println(aktie.getWertpapierKennnummer()+" "+aktie.getKurs() + "€");

                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            try {
                System.out.println("---- Verkaufspreis von Aktie" + aktie.getWertpapierKennnummer()+" "+verkaufsFuture.get());
                System.out.println("---- Das Konto hat jetzt: "+ girokonto.getKontostand());
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
    }
}
