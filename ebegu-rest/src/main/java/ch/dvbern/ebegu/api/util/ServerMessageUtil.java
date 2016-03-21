package ch.dvbern.ebegu.api.util;

import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Util welche einfach erlaubt eine Message aus dem server Seitigen Message Bundle zu lesen
 */
public final class ServerMessageUtil {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(Constants.SERVER_MESSAGE_BUNDLE_NAME, Constants.DEFAULT_LOCALE);


	private ServerMessageUtil() {
	}

	public static String getMessage(String key) {
		return readStringFromBundleOrReturnKey(BUNDLE, key);
	}

	public static String getMessage(String key, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(Constants.SERVER_MESSAGE_BUNDLE_NAME, locale);
		return readStringFromBundleOrReturnKey(bundle, key);
	}

	/**
	 * Da wir aller wahrscheinlichkeit eine Exceptionmessage uebersetzten wollen macht es nicht gross Sinn heir falls ein
	 * Key fehlt MissingResourceException werfen zu lassen.
	 * @param bundle
	 * @param key
	 * @return
	 */
	private static String readStringFromBundleOrReturnKey(ResourceBundle bundle, String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException ex) {
			return "???" + key + "???";
		}
	}


	public static String getMessage(String key, Object... args) {
		return MessageFormat.format(getMessage(key), args);
	}

	public static String getMessage(String key, Locale locale, Object... args) {
		return MessageFormat.format(getMessage(key, locale), args);
	}

	/**
	 * Uebersetzt einen Enum-Wert
	 */
	@Nonnull
	public static String translateEnumValue(@Nullable final Enum<?> e, Locale locale) {
		if (e == null) {
			return StringUtils.EMPTY;
		}
		return getMessage(getKey(e),locale);
	}



	/**
	 * Gibt den Bundle-Key für einen Enum-Wert zurück.
	 * Schema: Klassenname_enumWert, also z.B. CodeArtType_MANDANT
	 */
	@Nonnull
	private static String getKey(@Nonnull Enum<?> e) {
		return e.getClass().getSimpleName() + '_' + e.name();
	}
}
