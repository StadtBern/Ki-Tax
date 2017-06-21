package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;

/**
 * Service fuer erstellen und mutieren von Testfällen
 */
public interface TestfaelleService {

	String WAELTI_DAGMAR = "1";
	String FEUTZ_IVONNE = "2";
	String PERREIRA_MARCIA = "3";
	String WALTHER_LAURA = "4";
	String LUETHI_MERET = "5";
	String BECKER_NORA = "6";
	String MEIER_MERET = "7";
	String UMZUG_AUS_IN_AUS_BERN = "8";
	String ABWESENHEIT = "9";
	String UMZUG_VOR_GESUCHSPERIODE = "10";
	String ASIV1 = "ASIV1";
	String ASIV2 = "ASIV2";
	String ASIV3 = "ASIV3";
	String ASIV4 = "ASIV4";
	String ASIV5 = "ASIV5";
	String ASIV6 = "ASIV6";
	String ASIV7 = "ASIV7";
	String ASIV8 = "ASIV8";
	String ASIV9 = "ASIV9";
	String ASIV10 = "ASIV10";


	String heirat = "1";

	@Nonnull
	StringBuilder createAndSaveTestfaelle(@Nonnull String fallid,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen, @Nullable String gesuchsPeriodeId);

	@Nonnull
	StringBuilder createAndSaveAsOnlineGesuch(@Nonnull String fallid,
											  boolean betreuungenBestaetigt,
											  boolean verfuegen,
											  @Nonnull String username, @Nullable String gesuchsPeriodeId);

	@Nullable
	Gesuch createAndSaveTestfaelle(@Nonnull String fallid,
								   boolean betreuungenBestaetigt,
								   boolean verfuegen);

	@Nullable
	Gesuch mutierenHeirat(@Nonnull Long fallNummer,
						  @Nonnull String gesuchsperiodeId,
						  @Nonnull LocalDate eingangsdatum, @Nonnull LocalDate aenderungPer, boolean verfuegen);

	@Nullable
	Gesuch mutierenScheidung(@Nonnull Long fallNummer,
							 @Nonnull String gesuchsperiodeId,
							 @Nonnull LocalDate eingangsdatum, @Nonnull LocalDate aenderungPer, boolean verfuegen);

	/**
	 * loescht alle Gesuche des Gesuchstellers mit dem gegebenen Namen
	 * @param username Username des Besitzers der Gesuche die entferntw erden sollen
	 */
	void removeGesucheOfGS(@Nonnull String username);

	/**
	 * Gibt die Institutionsstammdaten zurück, welche in den gelieferten Testfällen verwendet werden,
	 * also Brünnen und Weissenstein Kita und Tagi
	 */
	@Nonnull
	List<InstitutionStammdaten> getInstitutionsstammdatenForTestfaelle();

	@Nonnull
	Gesuch createAndSaveGesuch(@Nonnull AbstractTestfall fromTestfall, boolean verfuegen, @Nullable Benutzer besitzer);

	void gesuchVerfuegenUndSpeichern(boolean verfuegen, @Nonnull Gesuch gesuch, boolean mutation);
}
