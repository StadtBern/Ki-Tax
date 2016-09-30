package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(GeneratedDokument.class)
public abstract class GeneratedDokument_ extends ch.dvbern.ebegu.entities.File_ {

	public static volatile SingularAttribute<GeneratedDokument, GeneratedDokumentTyp> typ;
	public static volatile SingularAttribute<GeneratedDokument, Gesuch> gesuch;

}

