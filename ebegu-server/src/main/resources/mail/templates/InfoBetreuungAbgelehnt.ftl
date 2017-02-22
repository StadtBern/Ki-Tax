<#-- @ftlvariable name="betreuung" type="ch.dvbern.ebegu.entities.Betreuung" -->
<#-- @ftlvariable name="gesuchsteller" type="ch.dvbern.ebegu.entities.Gesuchsteller" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
From: ${configuration.senderAddress}
To: ${gesuchsteller.fullName} <${gesuchsteller.mail}>
Subject: <@base64Header>Betreuungsplatz abgelehnt</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<title>Betreuungsplatz abgelehnt</title>

</head>

<body>

<div>
	<p>
		Sehr geehrte Familie,
	</p>
	<p>
        Ihr Betreuungsangebot für ${betreuung.kind.kindJA.fullName} / ${betreuung.institutionStammdaten.institution.name} wurde abgelehnt.
		Die Betreuungsangebote können
		<a href="<#if configuration.clientUsingHTTPS>https://<#else>http://</#if>${configuration.hostname}/gesuch/betreuungen/${betreuung.extractGesuch().id}">hier</a>
		bearbeitet werden.
	</p>
	<p>
		Freundliche Grüsse
    </p>
    <p>
        Jugendamt, Stadt Bern
	</p>
	<p>
		Dies ist eine automatisch versendete E-Mail. Bitte antworten Sie nicht auf diese Nachricht.
	</p>
</div>

</body>

</html>
