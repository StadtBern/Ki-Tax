FROM jboss/wildfly:10.0.0.Final

#Skripte hinzufuegen
ADD customization /opt/jboss/wildfly/customization/

#Treiber hinzufuegen
ADD modules /opt/jboss/wildfly/modules/


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

 #ADD your-awesome-app.war /opt/jboss/wildfly/standalone/deployments/


 #info this dockerfile could be run with  sudo docker run -it -p 8080:8080 -p 9990:9990 wildfly-test