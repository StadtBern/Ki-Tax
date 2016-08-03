package ch.dvbern.ebegu.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractFinanzielleSituation.class)
public abstract class AbstractFinanzielleSituation_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> familienzulage;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> geleisteteAlimente;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, Boolean> steuerveranlagungErhalten;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> schulden;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> bruttovermoegen;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus2;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahr;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> ersatzeinkommen;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> erhalteneAlimente;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus1;
	public static volatile SingularAttribute<AbstractFinanzielleSituation, Boolean> steuererklaerungAusgefuellt;

}

