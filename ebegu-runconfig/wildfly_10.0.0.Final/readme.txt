Datenbank einrichten (mariadb):
=================================
- mariadb installieren: sudo apt-get install mariadb-server
- schema und Benutzer konfigurieren (in mysql command line):
create schema ebegu;
create user 'ebegu'@'localhost' identified by 'ebegu';
grant all on 'ebegu.*' to 'ebegu';

Wildfly konfigurieren
=========================
MariaDB Driver als Modul in Jboss einfügen, Datasource konfigurieren:
-----------------------------------------------------------------------
- modules und noetige standalone.xml Anpassungen kopieren  aus config ordner





Datenbank einrichten (PostgreSQL):
=================================
- Ubuntu Software-Center: "postgresql" installieren
- Datenbankbenutzer ebegu anlegen: sudo -u postgres createuser -D -R -S -P ebegu (set Password to "ebegu")
- Datenbank ebegu anlegen: sudo -u postgres createdb -O ebegu ebegu

Wildfly konfigurieren
=========================
PostgreSQL Driver als Modul in Jboss einfügen, Datasource konfigurieren:
-----------------------------------------------------------------------
- ./jboss-cli.sh
- connect
- module add --name=org.postgres --resources=/home/hefr/Downloads/postgresql-9.4-1205.jdbc4.jar --dependencies=javax.api,javax.transaction.api
- /subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgres",driver-class-name=org.postgresql.Driver)
- data-source add --jndi-name=java:/jdbc/ebegu --name=fzlPool --connection-url=jdbc:postgresql://localhost/ebegu --driver-name=postgres --user-name=ebegu --password=ebegu

oder

- modules kopieren