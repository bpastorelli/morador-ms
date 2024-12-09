package br.com.morador.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonUnwrapped
	public List<GETResidenciaResponseDto> residencias;
	
}
