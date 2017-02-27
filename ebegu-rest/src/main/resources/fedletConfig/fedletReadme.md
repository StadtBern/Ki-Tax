#Fedlet Konfiguration
## In diesem Verzeichnis findet sich die Konfiguration der Fedlet Applikation


###Konfiguration
Damit die Verbindung richtig klappt muessen die Metadaten (insbesondere des Serviceproviders, also sp.xml und sp-extended.xml)
richtig eingelesen werden. Fuer jede Installation des Systems gibt es einen eigenen Konfigurationsordner

- http_app_ebegu_ch: Entwicklung lokal mit http
- https_act_ebegu_bern_ch: StadtBern Testumgebung mit https
- https_app_ebegu_ch: Entwicklung lokal mit https
- https_prod_ebegu_bern_ch: Produktion bei Stadt Bern
- https_test_ebegu_dvbern_ch: Testumgebung bei DV Bern

Darin werden die Metadaten und der PublicKey des IAM fuer die Signierung gespeichert. Um einzustellen welche Konfiguration
gelesen wird muss das Property `ebegu.fedlet.config.path` gesetzt sein. Der Konfigurationsordner wird dann in 
`SamRegistrationListener#setConfigurationLocationForFedlet()`
gesetzt.


- in /etc/hosts oder aequivalent muss eine "Pseudodomain" fuer app.ebegu.ch eingetrgen werden
`127.0.0.1  app.ebegu.ch`
- Im Standalone.xml muss das "jaspitest" loginmodul vorhanden sein.
- Fuer die https Konfiguration muss der wildfly fuer https Verbindungen auf 8443 konfiguriert sein

 

###Ablauf
Wir verwenden zum anmelden per SAML die Fedlet Applikation von OpenIAM. Diese ist schon etwas aelter und definiert eine 
JSP Applikation welche besipielhaft zum IDP verbindet.

Wird ein Aufruf auf das REST API gemacht welcher nicht authentifiziert ist, kommt 401 zurück. Der Client erkennt dies
und leitet automatisch auf unsere Loginseite um. Diese Loginseite wiederum leitet auf die IAM Loginseite um.

Wie Lange wir warten bis wir aufs IAM Umleiten kann eingestellt werden
Der Link sieht in etwa so aus: http://localhost:8080/ebegu/saml2/jsp/fedletSSOInit.jsp?metaAlias=/egov_bern/sp&idpEntityID=https://elogin-test.bern.ch/am&binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST&RelayState=http%3A%2F%2Fapp.ebegu.ch%3A3000%2F%23%2Ffaelle?sendRedirectForValidationNow=true


Die Antwort wurde urspruenglich von fedletSampleApp.jsp verwertet. Wir haben diese in ein Servlet konvertiert welches den Callback vom IAM verarbeitet
- FedletSamlServlet



Das Servlet nimmt die Daten entgegen und schreibt Sie in die Benutzertabelle falls der Benutzer noch nicht vorhanden ist. Ansonsten wird der bestehende User updated.
Zudem erstellt das Servlet in der authentifizierte_benutzer Tabelle einen Eintrag fuer den neu angemeldeten Benutzer
Zum Schluss wird in der Response an den Client ein Cookie mit dem login-token gesetzt. Danach wird auf die im RelayState uebergebene
Seite redirected.
Wenn nun der nacheste Servicecall erfolgt, werden automatisch die gesetzten Cookies mitgegeben.
Nun kommt das Jaspi LoginModul CookieTokenAuthModule zum Zug. Es liest das relevante Cookie aus und prueft ob das Token in
der Tabelle der authorisierten_benutzer vorhanden ist. Wenn ja wird der User mit der dort gespeicherten Rolle im Container
angemeldet.



#### Grobe Erklaerung der Konfigurationsfiles des fedlet
- .keypass: Passwort fuer keystore?
- .storepass: Passwort fuer keystore?
- FederationConfig.properties: Diverse Konfigurationseinstellungen fuer das verhalten des Servlets (logging etc)
- fedlet.cot: Definiert den Circle of trust
- idp.xml und idp-extended.xml definiert die Metadaten des identity provieders. Also von IAM
- keystore.jks: File mit dem Schluessel fuer die Signaturpruefug. PW ist changeit
- sp.xml und sp-extended.xml File fuer die Metadaten des ServiceProviders also EBEGU



