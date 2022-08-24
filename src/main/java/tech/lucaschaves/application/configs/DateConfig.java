package tech.lucaschaves.application.configs;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

//Classe de configuração GLOBAL para as Date - PADRÃO ISO0861 UTC

@Configuration
public class DateConfig {

												//Padrão ISO 0861 UTC
	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; 	                                     //setando o padrão informado           
	public static LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
	
	@Bean
	@Primary //ObjectMapper para mapear a config
	public ObjectMapper objectMapper() {
		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LOCAL_DATETIME_SERIALIZER);
		return new ObjectMapper()
				.registerModule(module);
	}
	
}
