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

###########
# cleanup #
###########

RUN rm -rf -- $JBOSS_HOME/customization
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

EXPOSE 9990
WORKDIR $JBOSS_HOME
# properties beim serverstart mitgeben
COPY ebegu-lokal.properties ebegu.properties
CMD ["--properties=ebegu.properties", "-bmanagement", "0.0.0.0"]
