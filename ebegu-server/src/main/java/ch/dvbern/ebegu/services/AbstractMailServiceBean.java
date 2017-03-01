package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.errors.MailException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.Validate;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;


/**
 * Allgemeine Mailing-Funktionalität
 */
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals"})
public abstract class AbstractMailServiceBean extends AbstractBaseService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractMailServiceBean.class.getSimpleName());
	private static final int CONNECTION_TIMEOUT = 15000;

	@Inject
	private EbeguConfiguration configuration;


	public void sendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		Validate.notNull(subject);
		Validate.notNull(messageBody);
		Validate.notNull(mailadress);
		if (configuration.isSendingOfMailsDisabled()) {
			pretendToSendMessage(messageBody, mailadress);
		} else {
			doSendMessage(subject, messageBody, mailadress);
		}
	}

	private void doSendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		try {
			Email email = new SimpleEmail();
			email.setHostName(configuration.getSMTPHost());
			email.setSmtpPort(configuration.getSMTPPort());
			email.setSSLOnConnect(false);
			email.setFrom(configuration.getSenderAddress());
			email.setSubject(subject);
			email.setMsg(messageBody);
			email.addTo(mailadress);
			email.send();
		} catch (final EmailException e) {
			LOG.error("Error while sending Mail to: '" + mailadress + "'", e);
			throw new MailException("Error while sending Mail to: '" + mailadress + "'", e);
		}
	}

	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	private void doSendMessage(@Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		final SMTPClient client = new SMTPClient("UTF-8");
		try {
			client.setDefaultTimeout(CONNECTION_TIMEOUT);
			client.connect(configuration.getSMTPHost(), configuration.getSMTPPort());
			client.setSoTimeout(CONNECTION_TIMEOUT);
			assertPositiveCompletion(client);
			client.helo(configuration.getHostname());
			assertPositiveCompletion(client);
			client.setSender(configuration.getSenderAddress());
			assertPositiveCompletion(client);
			client.addRecipient(mailadress);
			assertPositiveCompletion(client);
			final Writer writer = client.sendMessageData();
			writer.write(messageBody);
			writer.close();
			assertPositiveIntermediate(client);
			client.quit();
		} catch (final Exception e) {
			LOG.error("Error while sending Mail to: '" + mailadress + "'", e);
			throw new MailException("Error while sending Mail to: '" + mailadress + "'", e);
		} finally {
			if (client.isConnected()) {
				try {
					client.disconnect();
				} catch (final IOException e) {
					LOG.error("Could not disconnetct client", e);
				}
			}
		}
	}

	protected void sendMessageWithTemplate(@Nonnull final String messageBody, @Nonnull final String mailadress) throws MailException {
		Validate.notNull(mailadress);
		Validate.notNull(messageBody);
		if (configuration.isSendingOfMailsDisabled()) {
			pretendToSendMessage(messageBody, mailadress);
		} else {
			doSendMessage(messageBody, mailadress);
		}
	}

	private void pretendToSendMessage(final String messageBody, final String mailadress) {
		LOG.info("Sending of Emails disabled. Mail would be sent to " + mailadress + " : {}", messageBody);
	}

	private void assertPositiveIntermediate(final SMTPClient client) {
		assertPositiveIntermediate(client.getReplyCode());
	}

	private void assertPositiveIntermediate(final int replyCode) {
		if (!SMTPReply.isPositiveIntermediate(replyCode)) {
			throw new IllegalStateException("Reply code is not as expected: " + replyCode);
		}
	}

	private void assertPositiveCompletion(final int replyCode) {
		if (!SMTPReply.isPositiveCompletion(replyCode)) {
			throw new IllegalStateException("Reply code is not as expected: " + replyCode);
		}
	}

	private void assertPositiveCompletion(final SMTPClient client) {
		assertPositiveCompletion(client.getReplyCode());
	}
}
