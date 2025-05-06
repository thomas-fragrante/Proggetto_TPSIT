# Proggetto_TPSIT
Documentazione per progetto Raianer
1. Analisi dei Requisiti 
•	Aree: Piste (3, accesso esclusivo), Area di sosta (3 aerei), Hangar (5 aerei).   
•	Mezzi: Aerei, Veicoli di servizio rifornimento, Veicoli di servizio bagagli.   
•	Interazioni: Spostamento aerei, rifornimento, carico/scarico, decollo/atterraggio, gestione voli sola andata/ritorno.   
3. Scelte del codice
o	Abbiamo usato i semafori per poter applicare una logica di precedenze e priorita.
o	piste: Questo semaforo, inizializzato a 3, rappresenta le tre piste dell'aeroporto. L'operazione P(piste) viene eseguita da un aereo quando richiede l'accesso a una pista per il decollo o l'atterraggio. Se non ci sono piste disponibili (il valore del semaforo è 0), l'aereo si blocca fino a quando un'altra pista non si libera. L'operazione V(piste) viene eseguita quando l'aereo rilascia la pista dopo averla utilizzata.
o	areaSosta: Questo semaforo, inizializzato a 3, rappresenta i posti disponibili nell'area di sosta. Gli aerei eseguono P(areaSosta) per entrare nell'area di sosta e V(areaSosta) per uscire.
o	hangar : Questo semaforo, inizializzato a 5, rappresenta i posti disponibili nell'hangar. Gli aerei eseguono P(hangar) per entrare nell'hangar e V(hangar) per uscire. 
o	servizioRifornimento e servizioBagagli: Questi semafori sono semafori binari (inizializzati a 1) e fungono da mutex per garantire l'accesso esclusivo ai servizi di rifornimento e bagagli. Solo un aereo alla volta può essere rifornito o servito dai veicoli di servizio bagagli. 
o	mutexBagagli e mutexViaggiatori: Anche questi sono semafori binari usati come mutex. Proteggono le variabili condivise pesoBagagli e numViaggiatori all'interno della classe Aereo. 
•	Classe Aereo
o	La classe Aereo rappresenta un aereo e ha all’ interno i suoi attributi (codice volo, costruttore, capacità, ecc.).
o	Il metodo run() di Aereo definisce la sequenza di azioni che un aereo compie, dall'ingresso nell'hangar, alla sosta, al decollo/atterraggio, fino all'uscita.
o	L'uso di Thread.sleep() all'interno di run() simula il tempo impiegato dalle varie operazioni (es., rifornimento, carico/scarico).
o	L'attributo soloAndata (booleano) gestisce il comportamento degli aerei che effettuano solo un viaggio di andata.   
4. Descrizione Dettagliata del Codice
•	Classe Aeroporto
o	Il metodo main() crea più thread, ognuno dei quali esegue un'istanza della classe Aereo. Questo simula l'arrivo e la partenza di più aerei all'aeroporto.
o	I semafori sono definiti come variabili statiche della classe Aeroporto per renderli accessibili a tutti i thread Aereo. Questo permette a tutti gli aerei di competere per le risorse dell'aeroporto.
o	I metodi P() e V() sono definiti come metodi statici di Aeroporto per incapsulare le operazioni sui semafori, migliorando la leggibilità del codice e allineandolo alla notazione standard di Dijkstra.
•	Classe Aereo (Dettagli del Metodo run())
o	P(hangar): L'aereo tenta di entrare nell'hangar. Se l'hangar è pieno, l'aereo attende.
o	P(areaSosta), V(hangar): L'aereo tenta di entrare nell'area di sosta e, se ci riesce, libera un posto nell'hangar. Questa sequenza è importante per mantenere il numero corretto di aerei in ogni area.
o	P(servizioRifornimento), V(servizioRifornimento): L'aereo richiede e rilascia l'accesso esclusivo al servizio di rifornimento.
o	P(servizioBagagli), P(mutexBagagli), V(mutexBagagli), V(servizioBagagli): L'aereo richiede l'accesso esclusivo al servizio bagagli e protegge l'accesso alla variabile pesoBagagli con un mutex.
o	P(mutexViaggiatori), V(mutexViaggiatori): Protegge l'accesso alla variabile numViaggiatori.
o	P(piste), V(piste): L'aereo richiede e rilascia l'accesso esclusivo a una pista per il decollo/atterraggio.
o	La logica if (soloAndata) gestisce il comportamento degli aerei di sola andata, che ritornano all'hangar dopo l'atterraggio.
