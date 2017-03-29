package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Allgemeine Utils fuer Verfuegung
 */
public class VerfuegungUtil {


	/**
	 * Fuer die gegebene DateRange wird berechnet, wie viel Verguenstigung es insgesamt berechnet wurde.
	 * Diese wird dann als BigDecimal zurueckgegeben
	 * Formel: Verguenstigung * (overlappedDays / totalDays)
	 */
	public static BigDecimal getVerguenstigungZeitInterval(@Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull DateRange interval) {
		BigDecimal totalVerguenstigung = BigDecimal.ZERO;
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			final DateRange abschnittGueltigkeit = zeitabschnitt.getGueltigkeit();

			final Optional<DateRange> overlap = interval.getOverlap(abschnittGueltigkeit);
			if (overlap.isPresent()) { // only if there is overlap is it needed to calculate
				final BigDecimal overlappedDays = new BigDecimal(overlap.get().getDays());
				final BigDecimal totalAbschnittDays = new BigDecimal(abschnittGueltigkeit.getDays());
				final BigDecimal rate = overlappedDays.divide(totalAbschnittDays, 5, RoundingMode.HALF_UP);
				final BigDecimal overlappedElternbeitrag = zeitabschnitt.getElternbeitrag().multiply(rate);
				final BigDecimal overlappedVollkosten = zeitabschnitt.getVollkosten().multiply(rate);
				final BigDecimal verguenstigung = overlappedVollkosten.subtract(overlappedElternbeitrag);
				totalVerguenstigung = totalVerguenstigung.add(verguenstigung);
			}
		}
		return totalVerguenstigung.setScale(2, RoundingMode.HALF_UP);
	}

	public static void setIsSameVerfuegungsdaten(@Nonnull Verfuegung verfuegung) {
		final Verfuegung verfuegungOnGesuchForMutation = verfuegung.getBetreuung().getVorgaengerVerfuegung();
		if (verfuegungOnGesuchForMutation != null) {
			final List<VerfuegungZeitabschnitt> newZeitabschnitte = verfuegung.getZeitabschnitte();
			final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = verfuegungOnGesuchForMutation.getZeitabschnitte();

			for (VerfuegungZeitabschnitt newZeitabschnitt : newZeitabschnitte) {
				// todo imanol Dies sollte auch subzeitabschnitte vergleichen
				Optional<VerfuegungZeitabschnitt> oldSameZeitabschnitt = findZeitabschnittSameGueltigkeit(zeitabschnitteGSM, newZeitabschnitt);
				if (oldSameZeitabschnitt.isPresent()) {
					newZeitabschnitt.setSameVerfuegungsdaten(newZeitabschnitt.isSamePersistedValues(oldSameZeitabschnitt.get()));
					newZeitabschnitt.setSameVerguenstigung(Objects.equals(newZeitabschnitt.getVerguenstigung(), oldSameZeitabschnitt.get().getVerguenstigung()));
				}
				else { // no Zeitabschnitt with the same Gueltigkeit has been found, so it must be different
					newZeitabschnitt.setSameVerfuegungsdaten(false);
					newZeitabschnitt.setSameVerguenstigung(false);
				}
			}
		}
	}

	private static Optional<VerfuegungZeitabschnitt> findZeitabschnittSameGueltigkeit(List<VerfuegungZeitabschnitt> zeitabschnitteGSM, VerfuegungZeitabschnitt newZeitabschnitt) {
		for (VerfuegungZeitabschnitt zeitabschnittGSM : zeitabschnitteGSM) {
			if (zeitabschnittGSM.getGueltigkeit().equals(newZeitabschnitt.getGueltigkeit())) {
				return Optional.of(zeitabschnittGSM);
			}
		}
		return Optional.empty();
	}

	public static void setZahlungsstatus(@Nonnull Verfuegung verfuegung) {
		final Verfuegung verfuegungOnGesuchForMutation = verfuegung.getBetreuung().getVorgaengerVerfuegung();
		if (verfuegungOnGesuchForMutation != null) {
			final List<VerfuegungZeitabschnitt> newZeitabschnitte = verfuegung.getZeitabschnitte();
			final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = verfuegungOnGesuchForMutation.getZeitabschnitte();

			for (VerfuegungZeitabschnitt newZeitabschnitt : newZeitabschnitte) {
				VerfuegungsZeitabschnittZahlungsstatus oldStatusZeitabschnitt = findStatusOldZeitabschnitt(zeitabschnitteGSM, newZeitabschnitt);
				newZeitabschnitt.setZahlungsstatus(oldStatusZeitabschnitt);
			}
		}
	}

	private static VerfuegungsZeitabschnittZahlungsstatus findStatusOldZeitabschnitt(List<VerfuegungZeitabschnitt> zeitabschnitteGSM, VerfuegungZeitabschnitt newZeitabschnitt) {
		for (VerfuegungZeitabschnitt zeitabschnittGSM : zeitabschnitteGSM) {
			if (zeitabschnittGSM.getGueltigkeit().getOverlap(newZeitabschnitt.getGueltigkeit()).isPresent()) {
				// wir gehen davon aus, dass Zahlung immer fuer einen ganzen Monat gemacht werden, deswegen reicht es wenn ein Zeitabschnitt VERRECHNET bzw. IGNORIERT ist
				if (zeitabschnittGSM.getZahlungsstatus().equals(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET)) {
					return VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET;
				}
				else if (zeitabschnittGSM.getZahlungsstatus().equals(VerfuegungsZeitabschnittZahlungsstatus.IGNORIEREND)) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIEREND;
				}
				else if (zeitabschnittGSM.getZahlungsstatus().equals(VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT)) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT;
				}
			}
		}
		return VerfuegungsZeitabschnittZahlungsstatus.NEU;
	}
}
