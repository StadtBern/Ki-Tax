Dies ist ein Dockerfile welches wildlfy-10 definiert und mit einer lokalen maria DB verbindet.
Das Admin gui wird ebenfalls aufgeschaltet und es wird ein user dafuer erzeugt


1. DATENBANK INSTALLIEREN
    Der Name der Datenbank muss "ebegu" sein: connection String sieht so aus "jdbc:mysql://localhost/ebegu"

2. DOCKER INSTALLIEREN
    Zum verwenden bitte Docker gemaess Anleitung installieren.

    Linux: https://docs.docker.com/engine/installation/linux/ubuntulinux/
    Windows: https://docs.docker.com/engine/installation/windows/


3A. DOCKER FILE BUILDEN UND STARTEN (beinhaltet ebegu-rest nicht)
    Wenn man etwas am File ändern will oder
    Danach kann das Dockerfile mit folgenden befehl gebaut werden:
    docker build --rm --tag dvbern/deb-wildfly-ebegu-dev .

    Das Ausfuehren sollte lokal mit  folgenden Befehl gemacht werden
    docker run -it --net=host dvbern/deb-wildfly-ebegu-dev

ODER
3B. DOCKER IMAGE AUS DEM DVBERN REPO STARTEN (inkl. ebegu-rest)

    Per Maven wird zudem ein image in das dvbern docker repo gepushed welches ausgefuehrt werden kann

    docker run -it --net=host docker.dvbern.ch:5000/dvbern/ebegu