package ch.dvbern.ebegu.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;

/**
 * Allgemeine Utils fuer Verfuegung
 */
public class VerfuegungUtil {


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

	public static Optional<VerfuegungZeitabschnitt> findZeitabschnittSameGueltigkeitSameBetrag(List<VerfuegungZeitabschnitt> vorgaengerZeitabschnittList,
		VerfuegungZeitabschnitt
			newZeitabschnitt) {
		for (VerfuegungZeitabschnitt zeitabschnittGSM : vorgaengerZeitabschnittList) {
			if (zeitabschnittGSM.getGueltigkeit().equals(newZeitabschnitt.getGueltigkeit())
				&& zeitabschnittGSM.getVerguenstigung().compareTo(newZeitabschnitt.getVerguenstigung()) == 0
				&& zeitabschnittGSM.getAnspruchberechtigtesPensum() == newZeitabschnitt.getAnspruchberechtigtesPensum()) {
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
				if (zeitabschnittGSM.getZahlungsstatus() == VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET) {
					return VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET;
				}
				if (zeitabschnittGSM.getZahlungsstatus() == VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET_KORRIGIERT) {
					return VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET_KORRIGIERT;
				}
				if (zeitabschnittGSM.getZahlungsstatus() == VerfuegungsZeitabschnittZahlungsstatus.IGNORIEREND) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIEREND;
				}
				if (zeitabschnittGSM.getZahlungsstatus() == VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT;
				}
				if (zeitabschnittGSM.getZahlungsstatus() == VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT_KORRIGIERT) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT_KORRIGIERT;
				}
			}
		}
		return VerfuegungsZeitabschnittZahlungsstatus.NEU;
	}
}
