# MayAI

![](May.png)

MayAI è un assistente virtuale che assiste i clienti in linguaggio naturale
L’idea è di portare dentro i negozi e sugli smartphone dei clienti May, una tecnologia ad oggi applicata quasi esclusivamente online, affinchè posssa rispondere alle domande più comuni dei clienti e suggerire nuovi prodotti da scoprire e acquistare

MayAI permette 3 tipi di funzionalità

• *Dove si trova un prodotto?*: May restituisce la posizione del prodotto e una mappa nel negozio evidenziando la posizione 
• *Mi dai un consiglio?*: May può fornire suggerimenti sulla base dei dati degli utenti o sui prodotti su cui sa che si vuole puntare
• *Mi fai parlare con qualcuno?*: May permette di chiamare un membro del personale, avvertendolo via SMS e fornendogli una panoramica delle informazioni già note

MayAI è stata costruita con in mente la facilità di utilizzo e l'accessibilità, in modo che chiunque, a prescindere da età, provenienza o disabilità, possa migliorare la qualità del proprio tempo all'interno degli ambienti retail



Un esempio di funzionamento dell'app può essere trovato qui: [MayAI/Esempio funzionamento.mp4 at master · PietroMB/MayAI · GitHub](https://github.com/PietroMB/MayAI/blob/master/Esempio%20funzionamento.mp4) 



### Video di presentazione

[YouTube]([YouTube](https://youtu.be/j_Og_vknBq8))

### API utilizzate:

- DialogFlow

- TIM SMS

### App

È scritta in kotlin, presenta alcuni bug, tra cui:

- la prima volta che si avvia si interrompe (in impostazioni vanno dati i permessi)

- la registrazione ha una codifica che non va bene per dialogflow

- non è possibile registrare due audio in una singola sessione (va terminata e riaperta)

- c'è un bug in khttp che non aspetta la risposta e va in timeout

### logica

- l'utente registra un audio

- l'audio viene mandato a un server nginx che si occcupa di criptare il tutto

- il server nginx contatta Dialogflow/TIM e restituisce il risultato all'app

```apacheconf
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       1080;
        server_name  localhost liver.mynetgear.com;

        #charset koi8-r;

        access_log  /Users/stelabo/ingSL/HackTim/CiBotto/logs/http_access.log;

        location / {
                root   /Users/stelabo/ingSL/HackTim/CiBotto/www;
                index  index.html index.htm;
        }


    }




    # HTTPS server

    server {
        listen       1443 ssl;
        server_name  localhost liver.mynetgear.com;

        ssl_certificate      /etc/letsencrypt/live/liver.mynetgear.com/fullchain.pem;
        ssl_certificate_key  /etc/letsencrypt/live/liver.mynetgear.com/privkey.pem;

        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;

        ssl_ciphers  HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers  on;

        access_log  /Users/stelabo/ingSL/HackTim/CiBotto/logs/https_access.log;

        location / {
                root   /Users/stelabo/ingSL/HackTim/CiBotto/www;
                index  index.html index.htm;
        }
        location /webhook {
            proxy_pass http://127.0.0.1:8080/webhook;
        }
        location /api {
            proxy_pass http://127.0.0.1:8080/api;
        }
    }
    include servers/*;
}
```

## Esempio di curl per chiamare l'API

```shell
curl -H 'Content-Type: application/json'  \
-H 'Authorization: auth.TODO' \
-X POST https://liver.mynetgear.com:1443/api/v1/detectIntent -d \
'{
    "iid" : "123456",
    "sid" : 1593767927000,
    "text": "Devo festeggiare un compleanno",
    "inputAudio" : null,
    "lc"  : "it",
    "outputAudio" : true
}'
```

| iid         | id univoco applicazione                                |
| ----------- | ------------------------------------------------------ |
| sid         | timestamp millisecondi da prima richiesta              |
| text        | deve essere null de è popolato inputAudio              |
| inputAudio  | è la codifica base64 della registrazione               |
| lc          | è la lingua [it/en]                                    |
| outputAudio | se si desidera ricevere l'output audio oltre che testo |
