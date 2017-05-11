<#-- @ftlvariable name="gesuch" type="ch.dvbern.ebegu.entities.Gesuch" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="anzahlTage" type="java.lang.String" -->
<#-- @ftlvariable name="datumLoeschung" type="java.lang.String" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
From: ${configuration.senderAddress}
To: " ${gesuchsteller.fullName} <${gesuchsteller.mail}>
Subject: <@base64Header>Ki-Tax - Freigabequittung ausstehend</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Ki-Tax - Freigabequittung ausstehend</title>

</head>

<body>

<div>
    <p>
        Guten Tag
    </p>
    <p>
        Sie haben via Ki-Tax Ihr Gesuch vollständig erfasst, besten Dank!
    </p>
    <p>
        Leider ist Ihre Freigabequittung bisher nicht bei uns eingetroffen. Bitte schicken Sie uns das unterschriebene
        Dokument spätestens bis zum ${datumLoeschung} per Post oder per Mail (<a href="mailto:kinderbetreuung@bern.ch">kinderbetreuung@bern.ch</a>).
        Andernfalls erfolgt innert ${anzahlTage} Tagen eine automatische Löschung.
    </p>
    <p>
        Falls Ihr Kind in eine Kita geht, beachten Sie bitte: Beim Gesuch um einen Betreuungsgutschein ist das Eingangsdatum entscheidend.
    </p>
    <p>
        Bei Fragen stehen wir während den Bürozeiten gerne zur Verfügung (Tel. 031 321 51 15).
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
