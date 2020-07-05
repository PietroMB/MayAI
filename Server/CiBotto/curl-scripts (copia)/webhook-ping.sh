#!/bin/bash
# il driverid è implicito nel token
# simula invio di un evento lsdk

curl -H 'Content-Type: application/json'  \
-H 'AuthToken: 2c5146cda1693b5558602f6b90e30666' \
-X GET "https://liver.mynetgear.com:1443/webhook/ping"
