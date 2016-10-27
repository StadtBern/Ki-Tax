<%--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
   Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
  
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.

   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.

   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"

   $Id: index.jsp,v 1.14 2009/06/09 20:28:30 exu Exp $

    Portions Copyrighted 2013-2016 ForgeRock AS.
 --%>


<%@ include file="header.jspf" %>
<%--
    samlinfo.jsp contains links to test SP or IDP initiated Single Sign-on
--%>

<%
    String fedletBaseUrl = "/ebegu";
    String spMetaAlias = "/egov_bern/sp";
    String idpEntityID = "https://elogin-test.bern.ch/am";

%>

<h2>Validate Fedlet Setup</h2>
<p><br>
<table border="0" width="700">

    <tr>
        <td colspan="2"><a
                href="<%= fedletBaseUrl %>/saml2/jsp/fedletSSOInit.jsp?metaAlias=<%= spMetaAlias %>&idpEntityID=<%= idpEntityID%>&binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST&RelayState=http%3A%2F%2Fapp.ebegu.ch%3A3000%2F%23%2Ffaelle?sendRedirectForValidationNow=true">Run
            Fedlet (SP) initiated Single Sign-On using HTTP POST binding</a></td>
    </tr>
    <tr>
        <td colspan="2"><a
                href="<%= fedletBaseUrl %>/saml2/jsp/fedletSSOInit.jsp?metaAlias=<%= spMetaAlias %>&idpEntityID=<%= idpEntityID %>&binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact">Run
            Fedlet (SP) initiated Single Sign-On using HTTP Artifact binding</a></td>
    </tr>
</table>

</body>
</html>
