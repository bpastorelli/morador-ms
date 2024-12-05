package br.com.morador.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<GETResidenciaResponseDto> residencias;
	
}