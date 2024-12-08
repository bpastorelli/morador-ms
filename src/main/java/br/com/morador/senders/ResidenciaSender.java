package br.com.morador.senders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.morador.dto.QueryResidenciaResponseDto;
import br.com.morador.dto.ResidenciaRequestDto;
import br.com.morador.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResidenciaSender {
	
	@Value("${morador.residencia.url}")
	public String URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public QueryResidenciaResponseDto buscarResidencias(ResidenciaRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Consultando residencias no endpoint: {}", URL);
		
		RestTemplateUtil rest = RestTemplateUtil.builder()
				.URL(URL)
				.mediaType(MediaType.APPLICATION_JSON)
				.method(HttpMethod.GET)
				.restTemplate(restTemplate)
				.params(request)
				.build();
		
		return (QueryResidenciaResponseDto) rest.execute(QueryResidenciaResponseDto.class);
		
	}

}
