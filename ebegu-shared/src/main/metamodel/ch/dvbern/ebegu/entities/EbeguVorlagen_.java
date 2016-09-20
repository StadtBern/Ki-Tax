package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EbeguVorlage.class)
public abstract class EbeguVorlagen_ extends ch.dvbern.ebegu.entities.AbstractDateRangedEntity_ {

	public static volatile SingularAttribute<EbeguVorlage, EbeguVorlageKey> name;
	public static volatile SingularAttribute<EbeguVorlage, Vorlage> vorlage;

}

