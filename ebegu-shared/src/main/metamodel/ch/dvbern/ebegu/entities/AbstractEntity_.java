package ch.dvbern.ebegu.entities;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractEntity.class)
public abstract class AbstractEntity_ {

	public static volatile SingularAttribute<AbstractEntity, String> id;
	public static volatile SingularAttribute<AbstractEntity, LocalDateTime> timestampErstellt;
	public static volatile SingularAttribute<AbstractEntity, String> userMutiert;
	public static volatile SingularAttribute<AbstractEntity, Long> version;
	public static volatile SingularAttribute<AbstractEntity, String> userErstellt;
	public static volatile SingularAttribute<AbstractEntity, LocalDateTime> timestampMutiert;

}

