#remove security domain sp-basic
/data/examples/wildfly-saml-demos/wildfly-8.2.0.Final-olddemo/bin/jboss-cli.sh --connect --file=/data/examples/wildfly-saml-demos/jboss-picketlink-quickstarts/picketlink-federation-saml-sp-post-basic/remove-security-domain.cli

 http://localhost:8080/sales-post

#add security domain for service provider chooser
/data/examples/wildfly-saml-demos/wildfly-8.2.0.Final-olddemo/bin/jboss-cli.sh --connect --file=/data/examples/wildfly-saml-demos/jboss-picketlink-quickstarts/picketlink-federation-saml-sp-idp-chooser/configure-security-domain-wildfly.cli




#add identity provider
/data/examples/wildfly-saml-demos/wildfly-8.2.0.Final-olddemo/bin/jboss-cli.sh --connect --file=/data/examples/wildfly-saml-demos/jboss-picketlink-quickstarts/picketlink-federation-saml-idp-with-signature/configure-security-domain.cli


/data/examples/wildfly-saml-demos/wildfly-8.2.0.Final-olddemo/bin/jboss-cli.sh --connect --file=/data/examples/wildfly-saml-demos/jboss-picketlink-quickstarts/picketlink-federation-saml-sp-post-basic/configure-security-domain-wildfly.cli
/data/examples/wildfly-saml-demos/wildfly-8.2.0.Final-olddemo/bin/jboss-cli.sh --connect --file=/data/examples/wildfly-saml-demos/jboss-picketlink-quickstarts/picketlink-federation-saml-idp-basic/configure-security-domain-wildfly.cli


cd /data/examples/wildfly-saml-demos/wildfly-10.0.0.Final-spdemo && ./bin/standalone.sh