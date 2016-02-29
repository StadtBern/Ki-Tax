package ch.dvbern.ebegu.converters;

import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Converter to provide Java 8 Date/Time API Support to JPA
 */
@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    @Nullable
    public Date convertToDatabaseColumn(@Nullable LocalDate entityValue) {
	    if (entityValue != null) {
		    //Zeit wird auf 0 uhr gesetzt
		    return Date.from(entityValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    }
	    return null;
    }

    @Override
    @Nullable
    public LocalDate convertToEntityAttribute(@Nullable Date databaseValue) {
	    if (databaseValue != null) {
		    Instant instant = Instant.ofEpochMilli(databaseValue.getTime());
		    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
	    }
	    return null;
    }
}
