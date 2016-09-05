package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(WizardStep.class)
public abstract class WizardStep_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<WizardStep, WizardStepName> wizardStepName;
	public static volatile SingularAttribute<WizardStep, WizardStepStatus> wizardStepStatus;
	public static volatile SingularAttribute<WizardStep, String> bemerkungen;
	public static volatile SingularAttribute<WizardStep, Gesuch> gesuch;
	public static volatile SingularAttribute<WizardStep, Boolean> verfuegbar;

}

