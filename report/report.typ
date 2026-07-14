#import "@preview/lilaq:0.4.0" as lq

#align(center, text(18pt)[*Assignment 4 di Programmazione Concorrente e Distribuita 2025/2026*])
#align(center, text(
  12pt,
)[Mattia Ronchi, matr. 0001236997 \ Samorì Andrea matr. 0001235969 \ Andrea Monaco matr. 0001225150])

= RabbitMQ
= Analisi del problema

L'assignment ha l'obiettivo di realizzare un middleware di alto livello che permetta il rilascio di una sezione critica per processi che vivono in un sistema distribuito.

= Design della soluzione

La soluzione da noi implementata utilizza `RabbitMQ` Message-Oriented Middleware. Viene creata una queue con un solo messaggio all'interno. Ogni nodo che vuole entrare in sezione critica richieda a questa queue un messaggio. Quando la queue risponde il processo entra in sezione critica. Finita la sezione critica il processo invia un nuovo messaggio alla queue. A questo punto la queue può rispondere ad un altro processo che vuole entrare in sezione critica.

== Match

Il match rappresenta una singola partita svolta da due giocatori. I due giocatori inviano al match il numero scelto. Il match decreta il vincitore e invia ai partecipanti il risultato.

== Giocatori

Un giocatore tramite la funzione `play` gioca al torneo. Per ogni round, il giocatore genera un numero e lo invia al `match`, mettendosi in attesa del risultato. Una volta ricevuto, esistono due opzioni:
- in caso di vittoria il giocatore aspetta che tutti gli altri match del round finiscano per poi avanzare al round successivo
- in caso di sconfitta esce dal torneo

== Comunicazione

Ogni giocatore comunica tramite la struttura `played`.
I campi `id`, `send`, `reply` vengono utilizzati per comunicare con il match. `send` rappresenta il canale in cui viene passato il numero scelto dal giocatore mentre `reply` è il canale dove il giocatore si aspetta il verdetto del match. `notify` e `barrier` sono utilizzati per comunicare con il `roundManager`.

```go
type played struct {
	id      int
	send    chan int
	reply   chan bool
	notify  []chan struct{}
	barrier []chan struct{}
}
```
