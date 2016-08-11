package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FinanzielleSituation.class)
public abstract class FinanzielleSituation_ extends ch.dvbern.ebegu.entities.AbstractFinanzielleSituation_ {

	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> nettolohn;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus2;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus1;

}

