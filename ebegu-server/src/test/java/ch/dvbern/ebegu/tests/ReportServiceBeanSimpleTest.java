package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import org.junit.Assert;
import org.junit.Test;


public class ReportServiceBeanSimpleTest {

	@Test
	public void testGesuchStichtagQueryAssumptions() {
		Assert.assertEquals("IN_BEARBEITUNG_JA", AntragStatus.IN_BEARBEITUNG_JA.name());
		Assert.assertEquals("FREIGABEQUITTUNG", AntragStatus.FREIGABEQUITTUNG.name());
		Assert.assertEquals("BESCHWERDE_HAENGIG", AntragStatus.BESCHWERDE_HAENGIG.name());
		Assert.assertEquals("PRUEFUNG_STV", AntragStatus.PRUEFUNG_STV.name());
		Assert.assertEquals("GEPRUEFT_STV", AntragStatus.GEPRUEFT_STV.name());
		Assert.assertEquals("TAGESSCHULE", BetreuungsangebotTyp.TAGESSCHULE.name());
	}

	@Test
	public void testGesuchZeitraumQueryAssumptions() {
		Assert.assertEquals("ONLINE", Eingangsart.ONLINE.name());
		Assert.assertEquals("PAPIER", Eingangsart.PAPIER.name());

		Assert.assertEquals("ERSTGESUCH", AntragTyp.ERSTGESUCH.name());
		Assert.assertEquals("ERNEUERUNGSGESUCH", AntragTyp.ERNEUERUNGSGESUCH.name());
		Assert.assertEquals("MUTATION", AntragTyp.MUTATION.name());

		Assert.assertEquals("ABWESENHEIT", WizardStepName.ABWESENHEIT.name());
		Assert.assertEquals("BETREUUNG", WizardStepName.BETREUUNG.name());
		Assert.assertEquals("DOKUMENTE", WizardStepName.DOKUMENTE.name());
		Assert.assertEquals("EINKOMMENSVERSCHLECHTERUNG", WizardStepName.EINKOMMENSVERSCHLECHTERUNG.name());
		Assert.assertEquals("ERWERBSPENSUM", WizardStepName.ERWERBSPENSUM.name());
		Assert.assertEquals("FAMILIENSITUATION", WizardStepName.FAMILIENSITUATION.name());
		Assert.assertEquals("FINANZIELLE_SITUATION", WizardStepName.FINANZIELLE_SITUATION.name());
		Assert.assertEquals("FREIGABE", WizardStepName.FREIGABE.name());
		Assert.assertEquals("GESUCH_ERSTELLEN", WizardStepName.GESUCH_ERSTELLEN.name());
		Assert.assertEquals("GESUCHSTELLER", WizardStepName.GESUCHSTELLER.name());
		Assert.assertEquals("KINDER", WizardStepName.KINDER.name());
		Assert.assertEquals("UMZUG", WizardStepName.UMZUG.name());
		Assert.assertEquals("VERFUEGEN", WizardStepName.VERFUEGEN.name());

		Assert.assertEquals("MUTIERT", WizardStepStatus.MUTIERT.name());

		Assert.assertEquals("TAGESSCHULE", BetreuungsangebotTyp.TAGESSCHULE.name());

		Assert.assertEquals("VERFUEGT", AntragStatus.VERFUEGT.name());
		Assert.assertEquals("NUR_SCHULAMT", AntragStatus.NUR_SCHULAMT.name());
		Assert.assertEquals("BESCHWERDE_HAENGIG", AntragStatus.BESCHWERDE_HAENGIG.name());
		Assert.assertEquals("PRUEFUNG_STV", AntragStatus.PRUEFUNG_STV.name());
		Assert.assertEquals("IN_BEARBEITUNG_STV", AntragStatus.IN_BEARBEITUNG_STV.name());
		Assert.assertEquals("GEPRUEFT_STV", AntragStatus.GEPRUEFT_STV.name());
		Assert.assertEquals("KEIN_ANGEBOT", AntragStatus.KEIN_ANGEBOT.name());
	}
}
