package ch.dvbern.ebegu.mail;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
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

	private final Configuration freeMarkerConfiguration;

	@Inject
	private EbeguConfiguration ebeguConfiguration;


	public MailTemplateConfiguration() {
		final Configuration ourFreeMarkerConfig = new Configuration();
		ourFreeMarkerConfig.setClassForTemplateLoading(MailTemplateConfiguration.class, "/mail/templates");
		ourFreeMarkerConfig.setDefaultEncoding("UTF-8");
		this.freeMarkerConfiguration = ourFreeMarkerConfig;
	}

	public String getInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung, Gesuchsteller gesuchsteller) {
		return processTemplate("InfoBetreuungAbgelehnt.ftl", betreuung, gesuchsteller);
	}

	public String getInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller) {
		return processTemplate("InfoBetreuungenBestaetigt.ftl", gesuch, gesuchsteller);
	}

	public String getInfoVerfuegtGesuch(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller) {
		return processTemplate("InfoVerfuegtGesuch.ftl", gesuch, gesuchsteller);
	}

	public String getInfoVerfuegtMutaion(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller) {
		return processTemplate("InfoVerfuegtMutation.ftl", gesuch, gesuchsteller);
	}

	public String getInfoMahnung(@Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller) {
		return processTemplate("InfoMahnung.ftl", gesuch, gesuchsteller);
	}

	private String processTemplate(@Nonnull String nameOfTemplate, @Nonnull Gesuch gesuch, Gesuchsteller gesuchsteller, Object[]... extraValuePairs) {
		Object[][] paramsToPass = Arrays.copyOf(extraValuePairs, extraValuePairs.length + 2);
		paramsToPass[paramsToPass.length - 1] = new Object[] { "gesuch", gesuch };
		paramsToPass[paramsToPass.length - 2] = new Object[] { "gesuchsteller", gesuchsteller };
		return processtemplate(nameOfTemplate, DEFAULT_LOCALE, paramsToPass);
	}

	private String processTemplate(@Nonnull String nameOfTemplate, @Nonnull Betreuung betreuung, Gesuchsteller gesuchsteller, Object[]... extraValuePairs) {
		Object[][] paramsToPass = Arrays.copyOf(extraValuePairs, extraValuePairs.length + 2);
		paramsToPass[paramsToPass.length - 1] = new Object[] { "betreuung", betreuung };
		paramsToPass[paramsToPass.length - 2] = new Object[] { "gesuchsteller", gesuchsteller };
		return processtemplate(nameOfTemplate, DEFAULT_LOCALE, paramsToPass);
	}

	private String processtemplate(final String name, @Nonnull Locale loc, final Object[]... extraValuePairs) {
		assert name != null;
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

	public String getMailCss() {
		return "<style type=\"text/css\">\n" +
			"        body {\n" +
			"            font-family: \"Open Sans\", Arial, Helvetica, sans-serif;\n" +
			"        }\n" +
			"      .kursInfoHeader {background-color: #bce1ff; font-weight: bold;}" +
			"    </style>";
	}
}
