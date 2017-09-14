<#-- @ftlvariable name="betreuung" type="ch.dvbern.ebegu.entities.Betreuung" -->
<#-- @ftlvariable name="kind" type="ch.dvbern.ebegu.entities.Kind" -->
<#-- @ftlvariable name="fall" type="ch.dvbern.ebegu.entities.Fall" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="institution" type="ch.dvbern.ebegu.entities.Institution" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
<#-- @ftlvariable name="empfaengerMail" type="java.lang.String" -->
From: ${configuration.senderAddress}
To: ${institution.name} <${empfaengerMail}>
Subject: <@base64Header>Ki-Tax - Betreuung gelöscht</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Ki-Tax - Betreuung gelöscht</title>

</head>

<body>

<div>
    <p>
        Guten Tag
    </p>
    <p>
        Folgende Betreuung wurde entfernt:
    </p>
    <table>
        <tbody>
        <#--TODO with Reviewer format fallnummer (0000... vornedrann) -->
        <tr>
            <td width="200">Fall:</td>
            <td width="280">${fall.fallNummer} ${gesuchsteller.nachname}</td>
        </tr>
        <tr>
            <td>Kind:</td>
        <#--TODO with Reviewer format date -->
            <td>${kind.fullName}, ${kind.geburtsdatum}</td>
        </tr>
        <tr>
            <td>Betreuungsangebot:</td>
            <td>${betreuung.betreuungsangebotTyp.toString()}</td>
        </tr>
        <tr>
            <td>Institution:</td>
            <td>${institution.name}</td>
        </tr>
        <tr>
            <td>Periode:</td>
            <td>${betreuung.extractGesuchsperiode().getGesuchsperiodeString()}</td>
        </tr>
        <tr>
            <td>Status der entfernten Betreuung:</td>
            <td>${betreuung.getBetreuungsstatus().toString()}</td>
        </tr>
        </tbody>
    </table>
    <br/>
    <p>
    <#--TODO with Reviewer format Date-->
        Die Betreuung wurde erstellt am ${betreuung.timestampErstellt}.
    <#--TODO with Reviewer add Link of Betreuung -->
        Die aktuell gültige Betreuung finden Sie <a href="">hier</a>.
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
