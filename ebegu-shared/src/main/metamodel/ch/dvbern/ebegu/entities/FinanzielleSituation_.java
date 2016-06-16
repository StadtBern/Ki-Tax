package ch.dvbern.ebegu.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FinanzielleSituation.class)
public abstract class FinanzielleSituation_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geleisteteAlimente;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus2;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> erhalteneAlimente;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahrMinus1;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> nettolohn;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> familienzulage;
	public static volatile SingularAttribute<FinanzielleSituation, Boolean> steuerveranlagungErhalten;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> schulden;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> bruttovermoegen;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> geschaeftsgewinnBasisjahr;
	public static volatile SingularAttribute<FinanzielleSituation, BigDecimal> ersatzeinkommen;
	public static volatile SingularAttribute<FinanzielleSituation, Boolean> selbstaendig;
	public static volatile SingularAttribute<FinanzielleSituation, Boolean> steuererklaerungAusgefuellt;

}

