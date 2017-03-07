<#-- @ftlvariable name="mitteilung" type="ch.dvbern.ebegu.entities.Mitteilung" -->
<#-- @ftlvariable name="templateConfiguration" type="ch.dvbern.ebegu.mail.MailTemplateConfiguration" -->
<#-- @ftlvariable name="configuration" type="ch.dvbern.ebegu.config.EbeguConfiguration" -->
<#-- @ftlvariable name="empfaengerMail" type="java.lang.String" -->
From: ${configuration.senderAddress}
To: ${mitteilung.empfaenger.fullName} <${empfaengerMail}>
Subject: <@base64Header>Neue Nachricht vom Jugendamt</@base64Header>
Content-Type: text/html;charset=utf-8

<html>
<head>
${templateConfiguration.mailCss}
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<title>Ki-Tax: Neue Nachricht</title>

</head>

<body>

<div>
	<p>
		Sehr geehrte Familie
	</p>
	<p>
        Das Jugendamt hat Ihnen eine
		<a href="<#if configuration.clientUsingHTTPS>https://<#else>http://</#if>${configuration.hostname}/mitteilungen/${mitteilung.fall.id}">Nachricht</a>
        geschrieben.
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
