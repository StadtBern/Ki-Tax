package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.DokumentTyp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AnlageDokument.class)
public abstract class AnlageDokument_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AnlageDokument, String> dokumentName;
	public static volatile SingularAttribute<AnlageDokument, DokumentGrund> dokumentGrund;
	public static volatile SingularAttribute<AnlageDokument, DokumentTyp> dokumentTyp;

}

