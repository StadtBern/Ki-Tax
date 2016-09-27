package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.SequenceType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Sequence.class)
public abstract class Sequence_ extends ch.dvbern.ebegu.entities.AbstractMandantEntity_ {

	public static volatile SingularAttribute<Sequence, Long> currentValue;
	public static volatile SingularAttribute<Sequence, SequenceType> sequenceType;

}

