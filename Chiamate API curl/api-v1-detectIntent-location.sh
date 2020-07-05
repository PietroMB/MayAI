#!/bin/bash
# il driverid è implicito nel token
# simula invio di un evento lsdk

curl -H 'Content-Type: application/json'  \
-H 'Authorization: auth.TODO' \
-X POST https://liver.mynetgear.com:1443/api/v1/detectIntent -d \
'{
    "iid" : "123456",
    "sid" : 1593767927000,
    "text": "Mi servirebbe del prosciutto",
    "inputAudio" : null,
    "lc"  : "it",
    "outputAudio" : false
}'
