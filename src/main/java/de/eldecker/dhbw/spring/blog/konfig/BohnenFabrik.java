package de.eldecker.dhbw.spring.blog.konfig;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2B;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
                         .enable(  INDENT_OUTPUT              ) // Erzeugtes JSON mit Einrückungen, damit gut für Menschen lesbar
                         .build();
    }

    
    /**
     * Erzeugt entsprechend konfiguriertes {@link BCryptPasswordEncoder}-Objekt für die Verhashung
     * von Passwörtern:
     * <ul>
     * <li>Stärke des Hash-Algorithmus (cost, Kostenfaktor):
     *     {@code 12} (Default-Wert ist {@code 10}).
     *     Diese Zahl wird als Potenz für die Basis {@code 2} verwendet,
     *     um die Anzahl der Iterationen zu berechnen.
     *     Für {@code 12} bedeutet das: {@code 2^12 = 4.096} Iterationen.
     *     Je höher der Wert, desto länger dauert das Hashen und desto sicherer ist der Hash.
     * </li>
     * <li>Verwendet {@code SecureRandom}-Objekt für Zufallswerte für den Salt.</li>
     * <li>Version {@code $2b$} (die derzeit neueste von 2014)</li>
     * </ul>
     * <br><br>
     *
     * Beispielwert für einen gehashten String "g3h3im":
     * {@code $2b$12$kbseQ0b2vDSZSCpx8leMa.Q8dOxuwUaij5ApLWnka8ReQdvLEBnM6}
     * <br>
     * Hierbei ist {@code kbseQ0b2vDSZSCpx8leMa} der Salt-Wert (128 Bit) in Base64-Kodierung
     * und {@code Q8dOxuwUaij5ApLWnka8ReQdvLEBnM6} der Hash-Wert in Base64-Kodierung.
     * <br><br>
     *
     * Passwörter sollten nie im Klartext gespeichert werden, weil sonst bei einem
     * Hack-Angriff ein Angreifer die Passwörter selbst für Anmeldungen im Namen
     * der Benutzer verwenden könnte oder auf anderen Web-Seite (wenn der Benutzer
     * dort unvorsichtigerweise das selbe Passwort verwendet hat).
     * Der Salt-Wert verhindert, dass sog. "Rainbow Tables" verwendet werden können,
     * mit denen Hash-Werte "zurückgerechnet" werden können.
     * <br><br>
     *
     * siehe auch:
     * <a href="https://en.wikipedia.org/wiki/Bcrypt" target="_blank">Artikel zu "Bcrypt" in engl. Wikipedia</a>.
     * <br><br>
     *
     * Alternative zur Erzeugung von Bcrypt-Hashes für Demo-User:
     * Online-Dienste wie <a href="https://bcrypt.online/" target="_blank">bcrypt.online</a>.
     */
    @Bean
    public BCryptPasswordEncoder erzeugeBcryptPasswordEncoder() {

        final SecureRandom zufallsgenerator = new SecureRandom();

        final int staerke = 12;
        return new BCryptPasswordEncoder( $2B, staerke, zufallsgenerator );
    }

}
