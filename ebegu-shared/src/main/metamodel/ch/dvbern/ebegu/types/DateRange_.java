package ch.dvbern.ebegu.types;

import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DateRange.class)
public abstract class DateRange_ {

	public static volatile SingularAttribute<DateRange, Boolean> before;
	public static volatile SingularAttribute<DateRange, Boolean> stichtag;
	public static volatile SingularAttribute<DateRange, LocalDate> gueltigAb;
	public static volatile SingularAttribute<DateRange, Long> days;
	public static volatile SingularAttribute<DateRange, Boolean> after;
	public static volatile SingularAttribute<DateRange, LocalDate> gueltigBis;

}

