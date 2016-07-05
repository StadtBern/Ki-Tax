package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Regel die prueft ob das maximal moegliche Einkommen ueberschritten ist
 */
public class MaximalesEinkommenRule extends AbstractEbeguRule {


	private BigDecimal maximalesEinkommen;


	public MaximalesEinkommenRule(DateRange validityPeriod, BigDecimal maximalesEinkommen) {
		super(RuleKey.MAXIMALES_EINKOMMEN, RuleType.REDUKTIONSREGEL, validityPeriod);
		this.maximalesEinkommen = maximalesEinkommen;
	}

	@Nonnull
	@Override
	protected List<VerfuegungZeitabschnitt> createVerfuegungsZeitabschnitte(@Nonnull Betreuung betreuung, @Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte, @Nonnull FinanzielleSituationResultateDTO finSitResultatDTO) {
		// TODO Einkommensverschlechterung(en) berücksichtigen mit deren Stichdatum (immer 1. des Monats)
		// TODO Gehen wir hier davon aus, dass die "EinkommensverschlechterungsRegel" schon die Schnitze für anderes Einkommen gemacht hat?
		List<VerfuegungZeitabschnitt> einkommensAbschnitte = new ArrayList<>();
		VerfuegungZeitabschnitt finanzielleSituationAbschnitt = new VerfuegungZeitabschnitt(betreuung.extractGesuchsperiode().getGueltigkeit());
		finanzielleSituationAbschnitt.setMassgebendesEinkommen(readMassgebendesEinkommen(finSitResultatDTO));
		einkommensAbschnitte.add(finanzielleSituationAbschnitt);
		return einkommensAbschnitte;
	}

	@Override
	protected void executeRule(@Nonnull Betreuung betreuung, @Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		if (verfuegungZeitabschnitt.getMassgebendesEinkommen().compareTo(maximalesEinkommen) > 0) {
			verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(0);
			verfuegungZeitabschnitt.addBemerkung(RuleKey.MAXIMALES_EINKOMMEN.name() + ": Maximales Einkommen überschritten");
		}
	}

	/**
	 * Beim auslesen des Massgebenden Einkommens ist die FinanzielleSituationResultatDTO bzw die
	 * Einkommensverschlechterung relevant. Das heisst je nach Datum ist das massgebende Einkommen anders
	 */
	private BigDecimal readMassgebendesEinkommen(FinanzielleSituationResultateDTO finSitResultatDTO) {
		return finSitResultatDTO.getMassgebendesEinkommen();
	}
}
