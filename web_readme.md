# Installation Ki-Tax GUI

## nvm installieren
NVM dient der Verwaltung der benutzten node (npm) Version
 nvm installieren. Geht für Linux zum Beispiel mit
- curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.0/install.sh | bash

## Node installieren
Ki-Tax benoetigt ca Version 4.4 von Node. Kann mit nvm install v4.4.3 installiert werden
 - nvm install v4.4.3
 - sicherstellen, dass npm -v grösser als v3.8.1 ist
 
 
## Projektdependencies installieren (einmalig, bzw. nach Änderung)
- <home>:~/workspaces/ebegu/ebegu-web$ npm install

## Starten
- <home>:~/workspaces/ebegu/ebegu-web$ npm run hotstart
- <home>:~/workspaces/ebegu/ebegu-web$ npm run server:dev


## Formularstruktur
### Inputelement-Gruppe
Wir verwenden Bootstrap zum Layouten der Seite und Angular-Material Input-Komponenten. 

WICHTIG: Es ist darauf zu achten, dass 
Formularelemente (Typischerweise label element, input element, error message elment) in einem parent Element mit der 
Klasse *'form-group'* zusammengefasst werden (meistens entspricht dieser Parent bei uns einer Bootstrap-Column.

1. Label  
 Wir verwenden, wann immer moeglich, <label> tags mit einem 'for' attribut fuer die Bezeichnung. Es ist keine spezielle Styleclass erforderlich 
2. Inputelemente  
 Dem Element der Gruppe, welches als Input dient, sollte die Klasse *'input-element'* oder *'form-control'* gegeben werden, damit es identifiziert werden kann

3. Fehlerausgabe  
 Containern in denen die Fehler angezeigt werden sollen, muss die Stylecalss *'error'* gegeben werden.

Durch das Vergeben der richtigen Styleclasses wird sichergestellt, dass die Funktion der Elemente anhand der Styleclass
ermittelt werden kann. Dadurch kann zum Beispiel das Errorstyling leicht veraendert werden

### RadioButtons Gottcha

Wir verwenden zur Zeit Angular-Material im Hintergrund. Um die Verwendung von 
Radiobuttons fuer den Normalfall etwas zu vereinfachen, sollte dv-radio-container benutzt werden.
Muss aus einem Grund direkt md-radio-group verwendet werden so sollte darauf geachtet werden, dass
die Direktive dv-suppress-form-submit-on-enter als attribut hinzugefügt wird. Ansonsten
wird bei Radiobuttons ungewollt mit dem Enter Key das Formular submitted statt der erste
type=submitt Button geklickt.
