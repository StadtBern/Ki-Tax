package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(InstitutionStammdaten.class)
public abstract class InstitutionStammdaten_ extends ch.dvbern.ebegu.entities.AbstractDateRangedEntity_ {

	public static volatile SingularAttribute<InstitutionStammdaten, Institution> institution;
	public static volatile SingularAttribute<InstitutionStammdaten, BetreuungsangebotTyp> betreuungsangebotTyp;
	public static volatile SingularAttribute<InstitutionStammdaten, IBAN> iban;
	public static volatile SingularAttribute<InstitutionStammdaten, BigDecimal> oeffnungsstunden;
	public static volatile SingularAttribute<InstitutionStammdaten, BigDecimal> oeffnungstage;

}

