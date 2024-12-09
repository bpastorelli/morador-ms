package br.com.morador.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GETVinculoMoradorResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public GETMoradorResponseDto morador;
	
}
