/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.util;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.util.function.Procedure;
import net.bull.javamelody.MonitoringProxy;

import static java.util.Objects.requireNonNull;

public final class MonitoringUtil {
	private MonitoringUtil() {
		// utility class
	}

	/**
	 * Monitored einen einzelnen Methodenaufruf.
	 * JavaMelody braucht eigentlich immer ein Interface fuer seinen Proxy, da es intern die normale Java-Proxy Klasse nutzt.
	 * Aber: mit Java8 gibts mit {@link Supplier} das (fuer diesen Zweck) simpelst moegliche Interface fuer Wrapper :)
	 *
	 * Die uebergebene Methode wird sofort(!) aufgerufen.
	 *
	 * Usage:
	 * <pre>
	 * Ohne Monitoring: MyStuff result = myMethod(asdf);
	 * Mit Monitoring:  MyStuff result = monitor(FooClass.class, "monitoring of myMethod", () -> myMethod(asdf));
	 * </pre>
	 *
	 * @param monitoredClass Dient ausschliesslich zum generieren des Monitor-Namens, damit die einzelnen Monitor-Namen moeglichst ohne grossen Aufwand eindeutig sind.
	 * @param monitorNameSuffix Wird (durch ":" getrennt) an den Namen der uebergebenen Klasse angehaengt.
	 */
	public static <T> T monitor(@Nonnull Class<?> monitoredClass, @Nonnull String monitorNameSuffix, @Nonnull Supplier<T> call) {
		requireNonNull(monitorNameSuffix);
		requireNonNull(call);

		// JavaMelody
		return MonitoringProxy
			.createProxy(call, monitoredClass.getSimpleName() + ':' + monitorNameSuffix)
			.get();
	}

	public static <T extends Exception> void monitor(@Nonnull Class<?> monitoredClass, @Nonnull String monitorNameSuffix, @Nonnull Procedure<T> call) throws T {
		requireNonNull(monitorNameSuffix);
		requireNonNull(call);

		// JavaMelody
		MonitoringProxy
			.createProxy(call, monitoredClass.getSimpleName() + ':' + monitorNameSuffix)
			.execute();
	}

	/**
	 * Convenience: reicht den Aufruf an {@link #monitor(Class, String, Supplier)} weiter mit monitoredClassInstance.getClass().
	 * Dadurch kann meistens einfach &quot;this&quot; uebergeben werden.
	 */
	public static <T> T monitor(@Nonnull Object monitoredClassInstance, @Nonnull String monitorNameSuffix, @Nonnull Supplier<T> call) {
		return monitor(monitoredClassInstance.getClass(), monitorNameSuffix, call);
	}
}
