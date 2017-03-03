package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.testfaelle.AbstractTestfall;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

/**
 * Service zum berechnen und speichern der Verfuegung
 */
public interface TestfaelleService {

	String WaeltiDagmar = "1";
	String FeutzIvonne = "2";
	String PerreiraMarcia = "3";
	String WaltherLaura = "4";
	String LuethiMeret = "5";
	String BeckerNora = "6";
	String MeierMeret = "7";
	String UmzugAusInAusBern = "8";
	String Abwesenheit = "9";
	String UmzugVorGesuchsperiode = "10";
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

	StringBuilder createAndSaveTestfaelle(String fallid,
										  Integer iterationCount,
										  boolean betreuungenBestaetigt,
										  boolean verfuegen);

	StringBuilder createAndSaveAsOnlineGesuch(@Nonnull String fallid,
											  boolean betreuungenBestaetigt,
											  boolean verfuegen,
											  @Nonnull String username);

	Gesuch createAndSaveTestfaelle(String fallid,
								   boolean betreuungenBestaetigt,
								   boolean verfuegen);

	Gesuch mutierenHeirat(@Nonnull Long fallNummer,
						  @Nonnull String gesuchsperiodeId,
						  @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen);

	Gesuch mutierenScheidung(@Nonnull Long fallNummer,
							 @Nonnull String gesuchsperiodeId,
							 @Nonnull LocalDate eingangsdatum, LocalDate aenderungPer, boolean verfuegen);

	/**
	 * loescht alle Gesuche des Gesuchstellers mit dem gegebenen Namen
	 * @param username Username des Besitzers der Gesuche die entferntw erden sollen
	 */
	void removeGesucheOfGS(String username);

	/**
	 * Gibt die Institutionsstammdaten zurück, welche in den gelieferten Testfällen verwendet werden,
	 * also Brünnen und Weissenstein Kita und Tagi
	 */
	List<InstitutionStammdaten> getInstitutionsstammdatenForTestfaelle();

	Gesuch createAndSaveGesuch(AbstractTestfall fromTestfall, boolean verfuegen, @Nullable Benutzer besitzer);

	void gesuchVerfuegenUndSpeichern(boolean verfuegen, Gesuch gesuch, boolean mutation);
}
