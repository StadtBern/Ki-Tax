package ch.dvbern.ebegu.persistence;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by imanol on 04.03.16.
 * In hibernate 5 die Strategies werden nicht mehr von NamingStrategy implementiert.
 * Aus diesem Grund muss man seine eigene PhysicalStrategy bauen.
 * Check http://stackoverflow.com/questions/32437202/improvednamingstrategy-no-longer-working-in-hibernate-5
 */
public class EbeguPhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 7069586216789441113L;

	public static final EbeguPhysicalNamingStrategyImpl INSTANCE = new EbeguPhysicalNamingStrategyImpl();

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return new Identifier(addUnderscores(name.getText()), name.isQuoted());
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
		return new Identifier(addUnderscores(name.getText()), name.isQuoted());
	}


	protected static String addUnderscores(String name) {
		final StringBuilder buf = new StringBuilder(name.replace('.', '_'));
		for (int i = 1; i < buf.length() - 1; i++) {
			if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i))
					&& Character.isLowerCase(buf.charAt(i + 1))) {
				buf.insert(i++, '_');
			}
		}
		return buf.toString().toLowerCase(Locale.ROOT);
	}
}
