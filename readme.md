# Installation E-BEGU GUI

## nvm installieren
NVM dient der Verwaltung der benutzten node (npm) Version
 nvm installieren gemäss Wik. Geht aber für Linus zum Beispiel mit
- curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.0/install.sh | bash

## Node installieren
ebegu benoetigt ca Version 4.4 von Node. Kann mit nvm install v4.4.3 installiert werden
 - nvm install v4.4.3
 - sicherstellen das npm -v grösser als 3.8.1 ist
 
 
## Projektdependencies installieren (einmalig, bzw. nach Änderung)
- hefr@PDVB:~/workspaces/ebegu/ebegu-web$ npm install

## Starten
- hefr@PDVB:~/workspaces/ebegu/ebegu-web$ npm run hotstart
