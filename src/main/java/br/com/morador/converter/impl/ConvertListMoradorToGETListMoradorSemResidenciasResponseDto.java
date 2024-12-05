package br.com.morador.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.morador.converter.Converter;
import br.com.morador.dto.GETMoradorResponseDto;
import br.com.morador.dto.GETMoradoresResponseDto;
import br.com.morador.entities.Morador;
import br.com.morador.mappers.MoradorMapper;

@Component
public class ConvertListMoradorToGETListMoradorSemResidenciasResponseDto implements Converter<GETMoradoresResponseDto, List<Morador>> {
	
	@Autowired
	private MoradorMapper moradorMapper;
	
	@Override
	public GETMoradoresResponseDto convert(List<Morador> moradores) {
		
		List<GETMoradorResponseDto> moradoresSemResidencias = new ArrayList<GETMoradorResponseDto>();
		
		moradores.forEach(m -> {
			GETMoradorResponseDto morador = new GETMoradorResponseDto();
			morador = this.moradorMapper.moradorToGETMoradorResponseDto(m);
			moradoresSemResidencias.add(morador);
		});
		
		GETMoradoresResponseDto moradoresResponse = GETMoradoresResponseDto.builder()
				.moradores(moradoresSemResidencias)
				.build();
		
		return moradoresResponse;
	}

}
