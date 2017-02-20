/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.lib;

import javax.annotation.Nonnull;
import java.io.Serializable;

public interface MergeField extends Serializable {
	@Nonnull
	String getKey();

	@Nonnull
	Type getType();

	@Nonnull
	Converter getConverter();

	enum Type {
		/**
		 * Ein einfacher Platzhalter
		 */
		SIMPLE(true, false, false),
		/**
		 * Ein Platzhalter in den Ueberschriften, der mehrere Spalten hat (z.B. Ueberschrift mit den Kita-Namen)
		 */
		REPEAT_COL(true, true, true),
		REPEAT_VAL(true, true, false),
		/**
		 * Kennzeichnet eine Excel-Row, die wiederholt werden soll
		 */
		REPEAT_ROW(false, false, false);

		private final boolean mergeValue;
		private final boolean consumesValue;
		private final boolean hideColumOnEmpty;

		Type(boolean mergeValue, boolean consumesValue, boolean hideColumOnEmpty) {
			this.mergeValue = mergeValue;
			this.consumesValue = consumesValue;
			this.hideColumOnEmpty = hideColumOnEmpty;
		}

		public boolean doMergeValue() {
			return mergeValue;
		}

		public boolean doConsumeValue() {
			return consumesValue;
		}

		public boolean doHideColumnOnEmpty() {
			return hideColumOnEmpty;
		}
	}
}
