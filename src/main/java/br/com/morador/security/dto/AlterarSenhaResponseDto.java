package br.com.morador.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlterarSenhaResponseDto {
	
	private String senha;

}
