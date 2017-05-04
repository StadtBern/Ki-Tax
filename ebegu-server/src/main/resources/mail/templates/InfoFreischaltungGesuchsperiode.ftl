<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
<#-- @ftlvariable name="empfaengerMail" type="java.lang.String" -->
<#-- @ftlvariable name="startDatum" type="java.lang.String" -->
<#-- @ftlvariable name="gesuchsperiode" type="ch.dvbern.ebegu.entities.Gesuchsperiode" -->
From: ${configuration.senderAddress}
To: ${gesuchsteller.fullName} <${empfaengerMail}>
Subject: <@base64Header>Ki-Tax - Neue Gesuchsperiode freigeschaltet</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Ki-Tax - Neue Gesuchsperiode freigeschaltet</title>
</head>

<body>

<div>
    <p>
        Guten Tag
    </p>
    <p>
        Gerne möchten wir Sie mit dieser Mail informieren, dass die Gesuchperiode ab ${startDatum}
        für die Erfassung Ihrer Daten in Ki-Tax offen steht. Weitere Informationen finden Sie unter:
        <a href="www.bern.ch/kinderbetreuung">www.bern.ch/kinderbetreuung</a> und
        <a href="www.bern.ch/kinderbetreuung/ki-tax">www.bern.ch/kinderbetreuung/ki-tax</a>.<br/>
        Bei Fragen stehen wir während den Bürozeiten gerne zur Verfügung (Tel. 031 321 51 15).
    </p>
    <p>
        Falls Sie für die Periode ${gesuchsperiode.gesuchsperiodeString} kein Gesuch stellen möchten, sind für Sie keine weiteren Schritte notwendig.
    <p>
        Freundliche Grüsse <br/>
        Stadt Bern
    </p>
    <p>
        Dies ist eine automatisch versendete E-Mail. Bitte antworten Sie nicht auf diese Nachricht.
    </p>
</div>

</body>

</html>
