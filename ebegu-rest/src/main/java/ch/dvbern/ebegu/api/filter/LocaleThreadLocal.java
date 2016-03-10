package ch.dvbern.ebegu.api.filter;

import java.util.Locale;

/**
 ** A {@link ThreadLocal}um zu speichern was fuer eine Sprache wir verwenden fuer den aktuellen Request
 */
public class LocaleThreadLocal {


    public static final ThreadLocal<Locale> THREAD_LOCAL = new ThreadLocal<Locale>();


    public static Locale get() {
		return (THREAD_LOCAL.get() == null) ? Locale.getDefault() : THREAD_LOCAL.get();
    }


    public static void set(Locale locale) {
        THREAD_LOCAL.set(locale);
    }


    public static void unset() {
        THREAD_LOCAL.remove();
    }
}
