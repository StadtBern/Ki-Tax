#host-ip auslesen
alias hostip="ip route show 0.0.0.0/0 | grep -Eo 'via \S+' | awk '{ print \$2 }'"

#Datenbank initialisieren
docker run -d --name EBEGU-MARIADB  -e MYSQL_DATABASE=ebegu  -e MYSQL_USER=ebegu -e MYSQL_PASSWORD=ebegu -e MYSQL_ROOT_PASSWORD=ebegu  mariadb:latest --character-set-server=utf8 --collation-server=utf8_unicode_ci --verbose
#Auf Datenbank warten
docker run --rm --link EBEGU-MARIADB:mysql digit/wait-for-mysql
#Wildfly Server starten
docker run -dP --name EBEGU-WILDFLY -p 8080:8080 -p 8443:8443 -p 9990:9990  --link EBEGU-MARIADB:EBEGU-MARIADB docker.dvbern.ch:5000/stadt-bern/ebegu-wildfly
#Applikation starten
docker run -dP --name EBEGU-NGNIX --link EBEGU-WILDFLY:EBEGU-WILDFLY -p 3000:80   docker.dvbern.ch:5000/stadt-bern/ebegu-ngnix
