package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nonnull;
import java.time.LocalDate;

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
}
