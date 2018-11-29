<#-- @ftlvariable name="gesuch" type="ch.dvbern.ebegu.entities.Gesuch" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
From: ${configuration.senderAddress}
To: " ${gesuch.fall.verantwortlicherSCH.fullName} <${gesuch.fall.verantwortlicherSCH.email}>
Subject: <@base64Header>Ki-Tax – Gesuch durch Jugendamt verfügt</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<title>Ki-Tax – Gesuch durch Jugendamt verfügt</title>

</head>

<body>

<div>
	<p>
		Liebe/r ${gesuch.fall.verantwortlicherSCH.fullName}
	</p>
	<p>
		Das Mischgesuch mit der Fallnummer ${gesuch.jahrAndFallnummer} wurde soeben vom Jugendamt verfügt.
		Es kann nun auch für das Schulamt abgeschlossen werden.
	</p
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
