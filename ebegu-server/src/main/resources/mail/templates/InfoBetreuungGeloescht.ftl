<#-- @ftlvariable name="betreuung" type="ch.dvbern.ebegu.entities.Betreuung" -->
<#-- @ftlvariable name="kind" type="ch.dvbern.ebegu.entities.Kind" -->
<#-- @ftlvariable name="fall" type="ch.dvbern.ebegu.entities.Fall" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="institution" type="ch.dvbern.ebegu.entities.Institution" -->
<#-- @ftlvariable name="empfaengerMail" type="java.lang.String" -->
<#-- @ftlvariable name="datumErstellung" type="java.lang.String" -->
<#-- @ftlvariable name="birthday" type="java.lang.String" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
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
        <#--TODO with Reviewer format fallnummer (0000... vornedrann) ??? -->
        <tr>
            <td width="300">Fall:</td>
            <td width="300">${fall.fallNummer} ${gesuchsteller.nachname}</td>
        </tr>
        <tr>
            <td>Kind:</td>
            <td>${kind.fullName}, ${birthday}</td>
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
        Die Betreuung wurde erstellt am ${datumErstellung}.
        Die aktuell gültigen Betreuungen finden Sie <a href="<#if configuration.clientUsingHTTPS>https://<#else>http://</#if>${configuration.hostname}/gesuch/betreuungen/${betreuung.extractGesuch().id}">hier</a>.
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
