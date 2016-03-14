FROM jboss/wildfly:10.0.0.Final

#Skripte hinzufuegen
ADD customization /opt/jboss/wildfly/customization/

#Treiber hinzufuegen
ADD modules /opt/jboss/wildfly/modules/


#Applikation hinzufuegen
 ADD ebegu-rest.war /opt/jboss/wildfly/standalone/deployments/

# Run constomization script that adds driver and datasource
USER root
RUN /opt/jboss/wildfly/customization/execute.sh
 # Fix for Error: Could not rename /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history
RUN chown -R jboss:jboss /opt/jboss/wildfly/
USER jboss

#Admin user einfuegen
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

 CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]


 # info this dockerfile could be run with   docker run -it -p 8080:8080 -p 9990:9990 my-tag name  oder mit
 # docker run -it --net=host docker.dvbern.ch:5000/dvbern/ebegu