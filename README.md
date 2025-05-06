# Progetto Raianer - Simulazione di Aeroporto

Questo progetto simula l'operatività di un aeroporto, gestendo l'interazione tra aerei e le risorse aeroportuali come piste, area di sosta e hangar. Il sistema implementa una logica di precedenze e priorità nell'accesso alle risorse utilizzando i semafori.

## 1. Analisi dei Requisiti

Il sistema simula le seguenti aree, mezzi e interazioni:

**Aree:**
- Piste: 3 (accesso esclusivo)
- Area di sosta: 3 aerei
- Hangar: 5 aerei

**Mezzi:**
- Aerei
- Veicoli di servizio rifornimento
- Veicoli di servizio bagagli

**Interazioni:**
- Spostamento aerei
- Rifornimento
- Carico/scarico bagagli
- Decollo/atterraggio
- Gestione voli sola andata/ritorno

## 2. Scelte del Codice

Per gestire la concorrenza e l'accesso alle risorse condivise, abbiamo utilizzato i **semafori**.

### Semafori Implementati:

- **`piste`**: Semaforo inizializzato a 3. Controlla l'accesso alle tre piste. Un aereo esegue `P(piste)` per richiedere una pista e `V(piste)` per rilasciarla.
- **`areaSosta`**: Semaforo inizializzato a 3. Rappresenta i posti disponibili nell'area di sosta. Gli aerei usano `P(areaSosta)` per entrare e `V(areaSosta)` per uscire.
- **`hangar`**: Semaforo inizializzato a 5. Indica i posti liberi nell'hangar. Gli aerei eseguono `P(hangar)` per entrare e `V(hangar)` per uscire.
- **`servizioRifornimento`**: Semaforo binario (mutex) inizializzato a 1. Garantisce l'accesso esclusivo al servizio di rifornimento.
- **`servizioBagagli`**: Semaforo binario (mutex) inizializzato a 1. Assicura che solo un aereo alla volta possa essere servito dai veicoli di servizio bagagli.
- **`mutexBagagli`**: Semaforo binario (mutex) inizializzato a 1. Protegge la variabile condivisa `pesoBagagli` all'interno della classe `Aereo`.
- **`mutexViaggiatori`**: Semaforo binario (mutex) inizializzato a 1. Protegge la variabile condivisa `numViaggiatori` all'interno della classe `Aereo`.

### Classe `Aereo`

- Rappresenta un aereo con i suoi attributi specifici (codice volo, costruttore, capacità, ecc.).
- Il metodo `run()` definisce la sequenza di operazioni eseguite da un aereo: ingresso nell'hangar, sosta, rifornimento, gestione bagagli, decollo/atterraggio e uscita.
- L'utilizzo di `Thread.sleep()` all'interno di `run()` simula il tempo necessario per le diverse attività.
- L'attributo booleano `soloAndata` gestisce il comportamento degli aerei che effettuano solo un volo di andata.

## 3. Descrizione Dettagliata del Codice

### Classe `Aeroporto`

- Il metodo `main()` crea e avvia più thread, ognuno dei quali esegue un'istanza della classe `Aereo`. Questo simula l'attività di più aerei nell'aeroporto.
- I semafori sono dichiarati come variabili `static` della classe `Aeroporto`, rendendoli accessibili a tutti i thread `Aereo` per la competizione sulle risorse.
- I metodi `P()` e `V()` sono implementati come metodi `static` all'interno della classe `Aeroporto`. Questa scelta incapsula le operazioni sui semafori e migliora la leggibilità del codice, allineandosi alla notazione standard di Dijkstra.

### Classe `Aereo` (Dettagli del Metodo `run()`)

La sequenza di operazioni eseguite da un aereo è la seguente:

1.  **`P(hangar)`**: L'aereo tenta di entrare nell'hangar. Se non ci sono posti disponibili, l'aereo attende finché un posto non si libera.
2.  **`P(areaSosta)`**, **`V(hangar)`**: L'aereo tenta di accedere all'area di sosta. Se riesce, rilascia un posto nell'hangar, mantenendo la corretta gestione del numero di aerei in ciascuna area.
3.  **`P(servizioRifornimento)`**, **`V(servizioRifornimento)`**: L'aereo acquisisce e rilascia l'accesso esclusivo al servizio di rifornimento.
4.  **`P(servizioBagagli)`**, **`P(mutexBagagli)`**, **`V(mutexBagagli)`**, **`V(servizioBagagli)`**: L'aereo richiede l'accesso esclusivo al servizio bagagli e utilizza un mutex (`mutexBagagli`) per proteggere l'accesso alla variabile condivisa `pesoBagagli` durante le operazioni di carico/scarico.
5.  **`P(mutexViaggiatori)`**, **`V(mutexViaggiatori)`**: Un mutex (`mutexViaggiatori`) protegge l'accesso alla variabile condivisa `numViaggiatori` durante le operazioni di imbarco/sbarco.
6.  **`P(piste)`**, **`V(piste)`**: L'aereo richiede e rilascia l'accesso esclusivo a una pista per effettuare il decollo o l'atterraggio.
7.  La logica `if (soloAndata)` gestisce il comportamento degli aerei che effettuano solo un viaggio di andata. Dopo l'atterraggio, questi aerei tornano direttamente all'hangar.

Questo documento fornisce una panoramica della struttura e delle scelte implementative del progetto Raianer. Per dettagli specifici sull'implementazione, si rimanda al codice sorgente.
