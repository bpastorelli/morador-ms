package br.com.morador.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.morador.controllers.GETMoradoresSemResidenciaResponseDto;
import br.com.morador.converter.Converter;
import br.com.morador.dto.GETMoradorSemResidenciasResponseDto;
import br.com.morador.entities.Morador;
import br.com.morador.mappers.MoradorMapper;

@Component
public class ConvertListMoradorToGETListMoradoresSemResidenciasResponseDto implements Converter<GETMoradoresSemResidenciaResponseDto, List<Morador>> {

	@Autowired
	private MoradorMapper moradorMapper;
	
	@Override
	public GETMoradoresSemResidenciaResponseDto convert(List<Morador> moradores) {

		List<GETMoradorSemResidenciasResponseDto> moradoresSemResidencias = new ArrayList<GETMoradorSemResidenciasResponseDto>();
		
		moradores.forEach(m -> {
			GETMoradorSemResidenciasResponseDto morador = new GETMoradorSemResidenciasResponseDto();
			morador = this.moradorMapper.moradorToGETMoradorSemResidenciasResponseDto(m);
			moradoresSemResidencias.add(morador);
		});
		
		GETMoradoresSemResidenciaResponseDto moradoresResponse = GETMoradoresSemResidenciaResponseDto.builder()
				.moradores(moradoresSemResidencias)
				.build();
		
		return moradoresResponse;
		
	}

	
	
}
