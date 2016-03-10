Datenbank einrichten (PostgreSQL):
=================================
- Ubuntu Software-Center: "postgresql" installieren
- Datenbankbenutzer kurstool anlegen: sudo -u postgres createuser -D -R -S -P kurstool (set Password to "kurstool")
- Datenbank kurstool anlegen: sudo -u postgres createdb -O kurstool kurstool

Wildfly konfigurieren
=========================
PostgreSQL Driver als Modul in Jboss einfügen, Datasource konfigurieren:
-----------------------------------------------------------------------
- ./jboss-cli.sh
- connect
- module add --name=org.postgres --resources=/home/hefr/Downloads/postgresql-9.4-1205.jdbc4.jar --dependencies=javax.api,javax.transaction.api
- /subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgres",driver-class-name=org.postgresql.Driver)
- data-source add --jndi-name=java:/jdbc/kurstool --name=fzlPool --connection-url=jdbc:postgresql://localhost/kurstool --driver-name=postgres --user-name=kurstool --password=kurstool

oder

- modules kopieren