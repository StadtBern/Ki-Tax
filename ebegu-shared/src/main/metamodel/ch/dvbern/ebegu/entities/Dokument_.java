package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.DokumentTyp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Dokument.class)
public abstract class Dokument_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Dokument, String> dokumentPfad;
	public static volatile SingularAttribute<Dokument, String> dokumentName;
	public static volatile SingularAttribute<Dokument, DokumentGrund> dokumentGrund;
	public static volatile SingularAttribute<Dokument, DokumentTyp> dokumentTyp;

}

