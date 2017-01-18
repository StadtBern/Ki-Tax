FROM docker.dvbern.ch:5000/dvbern/appserv-wildfly-10.1.0:1.2

MAINTAINER Markus Hofstetter <markus.hofstetter@dvbern.ch>


###################
# Configure JBoss #
###################


RUN mkdir $JBOSS_HOME/customization
WORKDIR $JBOSS_HOME/customization
COPY customization/ebegu-customization.cli ebegu-customization.cli

RUN $JBOSS_HOME/bin/jboss-cli.sh --file=ebegu-customization.cli

#Wildfly Admin user einfuegen
RUN $JBOSS_HOME/bin/add-user.sh ebegu-admin jbtwLs --silent

#Applikation hinzufuegen
ADD ebegu-rest.war /opt/jboss/wildfly/standalone/deployments/

#Healthcheck
HEALTHCHECK CMD curl -f http://$(hostname -i || echo '127.0.0.1'):8080/ebegu/api/v1/swagger.json || exit 1

###########
# cleanup #
###########

RUN rm -rf -- $JBOSS_HOME/customization
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

EXPOSE 9990
WORKDIR $JBOSS_HOME
# properties beim serverstart mitgeben
COPY ebegu-lokal.properties $JBOSS_HOME/standalone/configuration/ebegu.properties
CMD ["--properties=$JBOSS_HOME/standalone/configuration/ebegu.properties", "-bmanagement", "0.0.0.0"]
