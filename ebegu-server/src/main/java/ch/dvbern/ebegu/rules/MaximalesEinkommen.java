package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Regel die prueft ob das maximal moegliche Einkommen ueberschritten ist
 */
public class MaximalesEinkommen extends AbstractEbeguRule {


	private BigDecimal maximalesEinkommen;


	public MaximalesEinkommen(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.MAXIMALES_EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}




	@Override
	public List<VerfuegungZeitabschnitt> calculate(@Nonnull BetreuungspensumContainer betreuungspensumContainer,
												   @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte,
												   @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {

		//immer wenn das massgebende Einkommen aendert muss ein neuer Zeitabschnitt definiert werden
		for (int i = 0; i < 1; i++) { //todo hier ueber finanzielleSituation sowie Verschlechterungen iterieren und jeweils das ereignisdatum verwenden um einen zeitabschnitt zu machen
			//Beispiel, einkommensverschlechterung auf Maerz
			VerfuegungZeitabschnitt initialabschnitt = new VerfuegungZeitabschnitt(betreuungspensumContainer.extractGesuchsperiode().getGueltigkeit());
			if (readMassgebendesEinkommen(finSitResultatDTO).compareTo(maximalesEinkommen) > 0) {
				initialabschnitt.setAnspruchberechtigtesPensum(0); //todo korrekte regel implementieren
			}
			zeitabschnitte.add(initialabschnitt); //

		}


		return zeitabschnitte;
	}

	/**
	 * Beim auslesen des Massgebenden Einkommens ist die FinanzielleSituationResultatDTO bzw die
	 * Einkommensverschlechterung relevant. Das heisst je nach Datum ist das massgebende Einkommen anders
	 * @param finSitResultatDTO
	 * @return
	 */
	private BigDecimal readMassgebendesEinkommen(FinanzielleSituationResultateDTO finSitResultatDTO) {
//		if(finSitResultatDTO.get)


		return finSitResultatDTO.getMassgebendesEinkommen();
	}
}
