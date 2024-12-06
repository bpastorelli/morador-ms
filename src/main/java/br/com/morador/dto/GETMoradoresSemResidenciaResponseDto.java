package br.com.morador.dto;

import java.io.Serializable;
import java.util.List;

import br.com.morador.dto.GETMoradorSemResidenciasResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GETMoradoresSemResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<GETMoradorSemResidenciasResponseDto> moradores;

	
	
}
