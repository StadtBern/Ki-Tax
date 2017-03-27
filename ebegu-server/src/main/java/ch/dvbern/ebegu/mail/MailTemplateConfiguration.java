package ch.dvbern.ebegu.mail;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Configuration For Freemarker Templates
 */
@Dependent
public class MailTemplateConfiguration {

	private static final Locale DEFAULT_LOCALE = new Locale("de", "CH");
	public static final String EMPFAENGER_MAIL = "empfaengerMail";

	private final Configuration freeMarkerConfiguration;

	@Inject
	private EbeguConfiguration ebeguConfiguration;


	public MailTemplateConfiguration() {
		final Configuration ourFreeMarkerConfig = new Configuration();
		ourFreeMarkerConfig.setClassForTemplateLoading(MailTemplateConfiguration.class, "/mail/templates");
		ourFreeMarkerConfig.setDefaultEncoding("UTF-8");
		this.freeMarkerConfiguration = ourFreeMarkerConfig;
	}

	public String getInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung, @Nonnull Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateBetreuung("InfoBetreuungAbgelehnt.ftl", betreuung, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch, @Nonnull Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("InfoBetreuungenBestaetigt.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoMitteilungErhalten(@Nonnull Mitteilung mitteilung, @Nonnull String empfaengerMail) {
		return processTemplateMitteilung("InfoMitteilungErhalten.ftl", mitteilung, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoVerfuegtGesuch(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("InfoVerfuegtGesuch.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoVerfuegtMutaion(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("InfoVerfuegtMutation.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoMahnung(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("InfoMahnung.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getWarnungGesuchNichtFreigegeben(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail, int anzahlMonate) {
		return processTemplateGesuch("WarnungGesuchNichtFreigegeben.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail), toArgumentPair("anzahlMonate", anzahlMonate));
	}

	public String getWarnungFreigabequittungFehlt(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("WarnungFreigabequittungFehlt.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	public String getInfoGesuchGeloescht(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, @Nonnull String empfaengerMail) {
		return processTemplateGesuch("InfoGesuchGeloescht.ftl", gesuch, gesuchsteller, toArgumentPair(EMPFAENGER_MAIL, empfaengerMail));
	}

	private String processTemplateGesuch(@Nonnull String nameOfTemplate, @Nonnull Gesuch gesuch, @Nonnull Gesuchsteller gesuchsteller, @Nonnull Object[]... extraValuePairs) {
		Object[][] paramsToPass = Arrays.copyOf(extraValuePairs, extraValuePairs.length + 2);
		paramsToPass[paramsToPass.length - 1] = new Object[] { "gesuch", gesuch };
		paramsToPass[paramsToPass.length - 2] = new Object[] { "gesuchsteller", gesuchsteller };
		return processtemplate(nameOfTemplate, DEFAULT_LOCALE, paramsToPass);
	}

	private String processTemplateBetreuung(@Nonnull String nameOfTemplate, @Nonnull Betreuung betreuung, @Nonnull Gesuchsteller gesuchsteller, @Nonnull Object[]... extraValuePairs) {
		Object[][] paramsToPass = Arrays.copyOf(extraValuePairs, extraValuePairs.length + 2);
		paramsToPass[paramsToPass.length - 1] = new Object[] { "betreuung", betreuung };
		paramsToPass[paramsToPass.length - 2] = new Object[] { "gesuchsteller", gesuchsteller };
		return processtemplate(nameOfTemplate, DEFAULT_LOCALE, paramsToPass);
	}

	private String processTemplateMitteilung(@Nonnull String nameOfTemplate, @Nonnull Mitteilung mitteilung, @Nonnull Object[]... extraValuePairs) {
		Object[][] paramsToPass = Arrays.copyOf(extraValuePairs, extraValuePairs.length + 1);
		paramsToPass[paramsToPass.length - 1] = new Object[] { "mitteilung", mitteilung };
		return processtemplate(nameOfTemplate, DEFAULT_LOCALE, paramsToPass);
	}

	private String processtemplate(@Nonnull final String name, @Nonnull Locale loc, final Object[]... extraValuePairs) {
		try {
			final Map<Object, Object> rootMap = new HashMap<>();
			rootMap.put("configuration", ebeguConfiguration);
			rootMap.put("templateConfiguration", this);
			rootMap.put("base64Header", new UTF8Base64MailHeaderDirective());
			if (extraValuePairs != null) {
				for (final Object[] extraValuePair : extraValuePairs) {
					if (extraValuePair.length > 0) {
						assert extraValuePair.length == 2;
						rootMap.put(extraValuePair[0], extraValuePair[1]);
					}
				}
			}

			final Template template = freeMarkerConfiguration.getTemplate(name, loc);
			final StringWriter out = new StringWriter(50);
			template.process(rootMap, out);

			return out.toString();
		} catch (final IOException e) {
			throw new EbeguRuntimeException("processtemplate()", String.format("Failed to load template %s.", name), e);
		} catch (final TemplateException e) {
			throw new EbeguRuntimeException("processtemplate()", String.format("Failed to process template %s.", name), e);
		}
	}

	private Object[] toArgumentPair(String key, Object value) {
		Object[] args = new Object[2];
		args[0] = key;
		args[1] = value;
		return args;
	}

	public String getMailCss() {
		return "<style type=\"text/css\">\n" +
			"        body {\n" +
			"            font-family: \"Open Sans\", Arial, Helvetica, sans-serif;\n" +
			"        }\n" +
			"      .kursInfoHeader {background-color: #bce1ff; font-weight: bold;}" +
			"    </style>";
	}
}
