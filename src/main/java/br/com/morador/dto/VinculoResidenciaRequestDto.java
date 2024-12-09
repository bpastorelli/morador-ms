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
public class VinculoResidenciaRequestDto {
	
	public Long residenciaId;
	
	public Long moradorId;
	
	public String guide;

}
