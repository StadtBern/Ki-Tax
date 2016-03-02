package ch.dvbern.ebegu.api.resource.util;

import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.JaxApplicationProperties;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;


@Dependent
@SuppressWarnings({"PMD.NcssTypeCount","unused"})
public class JaxBConverter {

	private static final Logger LOG = LoggerFactory.getLogger(JaxBConverter.class);


	@Nonnull
	public String toResourceId(@Nonnull final AbstractEntity entity) {
		return String.valueOf(Objects.requireNonNull(entity.getId()));
	}

	@Nonnull
	public Long toEntityId(@Nonnull final String resourceId) {
		// TODO wahrscheinlich besser manuell auf NULL pruefen und gegebenenfalls eine IllegalArgumentException werfen
		return Long.valueOf(Objects.requireNonNull(resourceId));
	}

	@Nonnull
	public Long toEntityId(@Nonnull final JaxAbstractDTO resource) {
		return toEntityId(Objects.requireNonNull(resource.getId()));
	}

	@Nonnull
	private <T extends JaxAbstractDTO> T convertAbstractFieldsToJAX (@Nonnull final AbstractEntity abstEntity, T jaxDTOToConvertTo) {
		jaxDTOToConvertTo.setTimestampErstellt(abstEntity.getTimestampErstellt());
		jaxDTOToConvertTo.setTimestampMutiert(abstEntity.getTimestampMutiert());
		jaxDTOToConvertTo.setId(checkNotNull(abstEntity.getId()));
		return jaxDTOToConvertTo;
	}

	@Nonnull
	public JaxApplicationProperties applicationPropertieToJAX(@Nonnull final ApplicationProperty applicationProperty) {
		JaxApplicationProperties jaxProperty = new JaxApplicationProperties();
		convertAbstractFieldsToJAX(applicationProperty, jaxProperty);
		jaxProperty.setName(applicationProperty.getName());
		jaxProperty.setValue(applicationProperty.getValue());
		return jaxProperty;
	}

	@Nonnull
	public ApplicationProperty applicationPropertieToEntity(JaxApplicationProperties jaxAP, @Nonnull final ApplicationProperty applicationProperty) {
		Validate.notNull(applicationProperty);
		Validate.notNull(jaxAP);

		applicationProperty.setName(jaxAP.getName());
		applicationProperty.setValue(jaxAP.getValue());

		return applicationProperty;
	}
}
