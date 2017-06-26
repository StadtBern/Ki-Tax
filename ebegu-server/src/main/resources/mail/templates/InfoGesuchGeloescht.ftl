<#-- @ftlvariable name="gesuch" type="ch.dvbern.ebegu.entities.Gesuch" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
<#assign datumGeloescht = .now>
From: ${configuration.senderAddress}
To: " ${gesuchsteller.fullName} <${gesuchsteller.mail}>
Subject: <@base64Header>Ki-Tax - Ihr Gesuch wurde gelöscht</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Ki-Tax - Ihr Gesuch wurde gelöscht</title>

</head>

<body>

<div>
    <p>
        Guten Tag
    </p>
    <p>
        Sie haben Ihr Antrag auf <a href="www.bern.ch/ki-tax">www.bern.ch/ki-tax</a> bearbeitet, diesen aber nicht
        freigegeben oder die Freigabequittung nicht eingereicht. Wir hatten Sie diesbezüglich bereits informiert.
    </p
    <p>
        Ihre Angaben wurden am ${datumGeloescht?date} automatisch gelöscht.
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
