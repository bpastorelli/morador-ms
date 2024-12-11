package br.com.morador.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GETVinculoResidenciaMoradorResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public GETResidenciaResponseDto residencia;
	
}
