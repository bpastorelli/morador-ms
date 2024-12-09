package br.com.morador.senders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.morador.dto.GETVinculoMoradorResidenciaResponseDto;
import br.com.morador.dto.VinculoResidenciaRequestDto;
import br.com.morador.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VinculosSender {
	
	@Value("${vinculos-ms.url}")
	public String URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public GETVinculoMoradorResidenciaResponseDto buscarResidencias(VinculoResidenciaRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Consultando residencias no endpoint: {}", URL);
		
		RestTemplateUtil rest = RestTemplateUtil.builder()
				.URL(URL)
				.mediaType(MediaType.APPLICATION_JSON)
				.method(HttpMethod.GET)
				.restTemplate(restTemplate)
				.params(request)
				.build();
		
		return (GETVinculoMoradorResidenciaResponseDto) rest.execute(GETVinculoMoradorResidenciaResponseDto.class);
		
	}

}
