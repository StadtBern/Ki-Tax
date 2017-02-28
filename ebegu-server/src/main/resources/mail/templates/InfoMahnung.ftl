<#-- @ftlvariable name="gesuch" type="ch.dvbern.ebegu.entities.Gesuch" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
From: ${configuration.senderAddress}
To: " ${gesuchsteller.fullName} <${gesuchsteller.mail}>
Subject: <@base64Header>Ki-Tax - Unvollständige Unterlagen</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Ki-Tax - Unvollständige Unterlagen</title>

</head>

<body>

<div>
    <p>
        Sehr geehrte Familie
    </p>
    <p>
        Am ${gesuch.getEingangsdatumFormated()} haben Sie ein Gesuch via Ki-Tax eingereicht.
        Leider sind die eingereichten Unterlagen gemäss einer ersten Vorprüfung unvollständig, daher wurden die
        fehlenden Dokumente heute erneut angefordert. Unser Schreiben finden Sie
        <a href="<#if configuration.clientUsingHTTPS>https://<#else>http://</#if>${configuration.hostname}/gesuch/freigabe/${gesuch.id}">hier</a>.
    </p>
    <p>
        Freundliche Grüsse <br/>
        Jugendamt der Stadt Bern
    </p>
    <p>
        Dies ist eine automatisch versendete E-Mail. Bitte antworten Sie nicht auf diese Nachricht.
    </p>
</div>

</body>

</html>
