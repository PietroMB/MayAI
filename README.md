# MayAI

![](May.png)

Il prodotto ha il fine di essere installato in dei totem all'interno di un negozio per aiutare il personale, fornendo indicazioni sulla locazione dei prodotti e dando piccoli suggerimenti, permette al personale di avere più tempo da dedicare a richieste più complesse o alla gestione del magazzino.

### API utilizzate:

- DialogFlow

- TIM SMS

### App

È scritta in kotlin, presenta alcuni bug, tra cui:

- la prima volta che si avvia si interrompe (in impostazioni vanno dati i permessi)

- la registrazione ha una codifica che non va bene per dialogflow

- non è possibile registrare due audio in una singola sessione (va terminata e riaperta)

- c'è un bug in khttp (non dipendente da noi) che spesso fa crashare l'app



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


