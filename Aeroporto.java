import java.util.concurrent.Semaphore;

public class Aeroporto {

    // Aree dell'aeroporto
    static final int NUM_PISTE = 3;
    static final int MAX_AEREI_SOSTA = 3;
    static final int MAX_AEREI_HANGAR = 5;

    static Semaphore piste = new Semaphore(NUM_PISTE, true); // Semaforo per le piste (3 disponibili)
    static Semaphore areaSosta = new Semaphore(MAX_AEREI_SOSTA, true); // Semaforo per l'area di sosta (3 posti)
    static Semaphore hangar = new Semaphore(MAX_AEREI_HANGAR, true); // Semaforo per l'hangar (5 posti)
    static Semaphore servizioRifornimento = new Semaphore(1, true); // Semaforo per il veicolo di rifornimento (1 disponibile)
    static Semaphore servizioBagagli = new Semaphore(1, true); // Semaforo per il veicolo di servizio bagagli (1 disponibile)
    static Semaphore mutexBagagli = new Semaphore(1, true); // Mutex per la gestione dei bagagli
    static Semaphore mutexViaggiatori = new Semaphore(1, true); // Mutex per la gestione dei viaggiatori

    // Metodi P e V per chiarezza
    static void P(Semaphore s) throws InterruptedException {
        s.acquire();
    }

    static void V(Semaphore s) {
        s.release();
    }

    public static void main(String[] args) {
        // Esempio di utilizzo
        for (int i = 1; i <= 10; i++) {
            Aereo aereo = new Aereo("Volo-" + i, "Costruttore-" + i, 100, 0, 5000, 0, Math.random() < 0.5); // Crea aereo con viaggio di sola andata o andata e ritorno
            new Thread(aereo).start();
        }
    }

    static class Aereo implements Runnable {
        String codiceVolo;
        String impresaCostruttrice;
        int maxViaggiatori;
        int numViaggiatori;
        int pesoMaxBagagli;
        int pesoBagagli;
        boolean soloAndata;

        public Aereo(String codiceVolo, String impresaCostruttrice, int maxViaggiatori, int numViaggiatori, int pesoMaxBagagli, int pesoBagagli, boolean soloAndata) {
            this.codiceVolo = codiceVolo;
            this.impresaCostruttrice = impresaCostruttrice;
            this.maxViaggiatori = maxViaggiatori;
            this.numViaggiatori = numViaggiatori;
            this.pesoMaxBagagli = pesoMaxBagagli;
            this.pesoBagagli = pesoBagagli;
            this.soloAndata = soloAndata;
        }

        @Override
        public void run() {
            try {
                P(hangar); // L'aereo è nell'hangar
                System.out.println(codiceVolo + ": In Hangar");
                Thread.sleep((long) (Math.random() * 2000)); // Simula attesa in hangar

                // L'aereo si sposta nell'area di sosta
                P(areaSosta); // Richiede un posto nell'area di sosta
                V(hangar); // Libera il posto nell'hangar
                System.out.println(codiceVolo + ": In Area di Sosta");

                // Rifornimento
                P(servizioRifornimento);
                System.out.println(codiceVolo + ": Rifornimento in corso");
                Thread.sleep((long) (Math.random() * 1000)); // Simula rifornimento
                V(servizioRifornimento);

                // Carico/scarico bagagli
                P(servizioBagagli);
                P(mutexBagagli); // Protegge l'accesso ai bagagli
                System.out.println(codiceVolo + ": Gestione bagagli in corso");
                Thread.sleep((long) (Math.random() * 1000)); // Simula carico/scarico bagagli
                pesoBagagli = pesoMaxBagagli; // Simula il raggiungimento del peso massimo
                V(mutexBagagli);
                V(servizioBagagli);

                // Carico/scarico passeggeri
                P(mutexViaggiatori); // Protegge l'accesso ai viaggiatori
                System.out.println(codiceVolo + ": Gestione passeggeri in corso");
                Thread.sleep((long) (Math.random() * 1000));
                numViaggiatori = maxViaggiatori; // Simula il raggiungimento del numero massimo di passeggeri
                V(mutexViaggiatori);

                // L'aereo si sposta sulla pista per il decollo/atterraggio
                P(piste);
                System.out.println(codiceVolo + ": Sulla pista");
                Thread.sleep((long) (Math.random() * 1000)); // Simula decollo/atterraggio
                V(piste);

                if (soloAndata) {
                    System.out.println(codiceVolo + ": Volo di sola andata, rientro in Hangar");
                    P(hangar); // Ritorna all'hangar
                    V(areaSosta); // Libera il posto nell'area di sosta
                } else {
                    System.out.println(codiceVolo + ": Volo di andata e ritorno, attesa in area di sosta"); //
                    Thread.sleep((long) (Math.random() * 2000)); // Simula attesa per il ritorno
                    // ... (Codice per il volo di ritorno)
                    V(areaSosta);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}