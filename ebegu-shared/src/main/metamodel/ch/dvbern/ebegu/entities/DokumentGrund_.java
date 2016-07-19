package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AnlageGrundTyp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DokumentGrund.class)
public abstract class DokumentGrund_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<DokumentGrund, String> tag1;
	public static volatile SingularAttribute<DokumentGrund, AnlageGrundTyp> anlageGrundTyp;
	public static volatile SetAttribute<DokumentGrund, AnlageDokument> anlageDokumente;
	public static volatile SingularAttribute<DokumentGrund, Gesuch> gesuch;
	public static volatile SingularAttribute<DokumentGrund, String> tag2;

}

