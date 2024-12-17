package br.com.morador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinculoRequestDto {
	
	private String cpfMorador;
	
	private String cepResidencia;
	
	private Long numeroResidencia;
	
	private String complementoResidencia;
	
	private String residenciaId;

}
