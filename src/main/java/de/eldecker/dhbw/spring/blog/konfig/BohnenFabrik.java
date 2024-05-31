package de.eldecker.dhbw.spring.blog.konfig;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;


/**
 * Diese Configuration-Klasse enthält Methoden, die mit {@code Bean} annotiert sind,
 * weil sie bestimmte Beans erzeugen.
 */
@Configuration
public class BohnenFabrik {

    /**
     * Liefert konfiguriertes ObjectMapper-Objekt zurück, welches für Object-nach-JSON (Serialisierung)
     * oder JSON-nach-Objekt (Deserialisierung) benötigt wird.
     *
     * @return Konfigurierter Object-Mapper
     */
    @Bean
    public ObjectMapper erzeugeObjectMapper() {

        return JsonMapper.builder()
                         .disable( FAIL_ON_UNKNOWN_PROPERTIES ) // Ignoriert unbekannte JSON-Felder beim Deserialisieren
                         .disable( WRITE_DATES_AS_TIMESTAMPS  ) // Schreibt Datum und Zeit im ISO-8601-Format
                         .enable( INDENT_OUTPUT               ) // Erzeugtes JSON mit Einrückungen, damit gut für Menschen lesbar
                         .build();
    }
    
}
