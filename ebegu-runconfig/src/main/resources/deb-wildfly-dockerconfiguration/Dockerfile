FROM debian:8.3

MAINTAINER Markus Hofstetter <markus.hofstetter@dvbern.ch>

ENV JDK_PACKAGE_VERSION=8u77+8u77arm-1~webupd8~3
ENV WILDFLY_VERSION=10.0.0.Final
ENV WILDFLY_SHA1=c0dd7552c5207b0d116a9c25eb94d10b4f375549
ENV MARIADB_DRIVER_VERSION=1.3.7

##############
# Prepare OS #
##############
ENV JBOSS_HOME=/opt/jboss/wildfly
RUN useradd --home-dir /opt/jboss --create-home --user-group --shell /bin/false jboss

###################
# Install Wildfly #
###################
# Do this first as it take some time to download :(

RUN apt-get update && apt-get install -y curl

RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:jboss $JBOSS_HOME

# Damit werden in standalone.sh die Signale (HUP, KILL, ...) an Java weitergereicht
ENV LAUNCH_JBOSS_IN_BACKGROUND=true




################
# Install JDK8 #
################

# Activate the official oracle Java repo
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886

# Auto-Accept licence and actually install
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get update && apt-get install -y \
	oracle-java8-set-default=$JDK_PACKAGE_VERSION \
	oracle-java8-installer=$JDK_PACKAGE_VERSION
RUN which java && java -version
# Beim Build anzeigen ob aktuellere Versionen ferfuegbar waeren
RUN apt-cache policy oracle-java8-installer


# Configure jboss
RUN mkdir $JBOSS_HOME/customization
WORKDIR $JBOSS_HOME/customization
COPY customization/ebegu-customization.cli ebegu-customization.cli
ADD http://nexus.dvbern.dvb.ch/nexus/service/local/repositories/central/content/org/mariadb/jdbc/mariadb-java-client/$MARIADB_DRIVER_VERSION/mariadb-java-client-$MARIADB_DRIVER_VERSION.jar \
		mariadb-java-client.jar
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=ebegu-customization.cli

#Wildfly Admin user einfuegen
RUN $JBOSS_HOME/bin/add-user.sh ebegu-admin jbtwLs --silent

#Applikation hinzufuegen
ADD ebegu-rest.war /opt/jboss/wildfly/standalone/deployments/

###########
# cleanup #
###########
RUN apt-get -y autoclean && apt-get -y autoremove
RUN rm -rf -- $JBOSS_HOME/customization

#####################
# Run configuration #
#####################
RUN chown -R jboss:jboss $JBOSS_HOME
USER jboss

VOLUME $JBOSS_HOME/standalone/configuration
VOLUME $JBOSS_HOME/standalone/deployments
VOLUME $JBOSS_HOME/standalone/log
EXPOSE 8080
EXPOSE 9990
WORKDIR $JBOSS_HOME
# properties beim serverstart mitgeben
COPY ebegu-lokal.properties ebegu.properties
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "--properties=ebegu.properties", "-bmanagement", "0.0.0.0"]
