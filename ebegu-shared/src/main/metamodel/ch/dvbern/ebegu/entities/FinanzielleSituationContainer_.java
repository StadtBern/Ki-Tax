package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FinanzielleSituationContainer.class)
public abstract class FinanzielleSituationContainer_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<FinanzielleSituationContainer, Gesuchsteller> gesuchsteller;
	public static volatile SingularAttribute<FinanzielleSituationContainer, Integer> jahr;
	public static volatile SingularAttribute<FinanzielleSituationContainer, FinanzielleSituation> finanzielleSituationJA;
	public static volatile SingularAttribute<FinanzielleSituationContainer, FinanzielleSituation> finanzielleSituationGS;

}

