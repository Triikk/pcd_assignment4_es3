#import "@preview/lilaq:0.4.0" as lq

#align(center, text(18pt)[*Assignment 4 di Programmazione Concorrente e Distribuita 2025/2026*])
#align(center, text(
  12pt,
)[Mattia Ronchi, matr. 0001236997 \ Samorì Andrea matr. 0001235969 \ Andrea Monaco matr. 0001225150])

= RabbitMQ
= Analisi del problema

L'assignment ha l'obiettivo di realizzare un middleware di alto livello che permetta il rilascio di una sezione critica per processi che vivono in un sistema distribuito.

= Design della soluzione

La soluzione da noi implementata utilizza `RabbitMQ` come Message-Oriented Middleware. Viene creata una coda che identifica una sezione critica con un solo messaggio all'interno. Ogni nodo che vuole entrare in sezione critica richieda a questa coda un messaggio. Quando la coda risponde il processo entra in sezione critica. Finita la sezione critica il processo invia un nuovo messaggio alla coda. A questo punto la coda può rispondere ad un altro processo che vuole entrare in sezione critica.

Abbiamo proposto due soluzioni:
- `Subscriber.java`: ogni subscriber richiede sempre la sezione critica in loop.
- `CriticalSection.java`: ogni subscriber alterna una sezione non critica con una critica.

= Implementazione

Il primo client si connette ad una coda e, sapendo di essere il primo a farlo, pubblica il messaggio per iniziallizzare la coda. Questo viene fatto tramite un argomento booleano `first` a riga di comando. In un sistema che utilizza questa soluzione, l'iniziallizzazione spetterebbe ad un apposito componente (es `Initializer`) che inserirebbe il primo messaggio in ogni coda.

== Subscriber.java

In questa soluzione i client hanno sempre bisogno di accedere ad una sezione critica (nel nostro caso, una `Thread.sleep()`). Questa viene consegnata loro tramite round-robin (garantito da RabbitMQ).

Per fare ciò ogni client si iscrive ad una coda e, alla ricezione di un messaggio, esegue la `callback` per entrare in sezione critica.

== CriticalSection.java

In questa soluzione, immaginiamo che i client abbiamo questo comportamento:
- varie computazioni non critiche;
- richiesta di sezione critica (bloccante);
- ricezione del messaggio che permette l'entrata in sezione critica;
- varie computazioni critiche;
- rilascio della sezione critica (tramite invio di messaggio sulla coda).

Immaginiamo che questa sequenza di passaggi sia ripetuta all'infinito.
