package br.com.morador.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.morador.errorheadling.ErroRegistro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ResponsePublisherDto {
	
	private CabecalhoResponsePublisherDto ticket;
	
	@Builder.Default
	private List<ErroRegistro> errors = new ArrayList<ErroRegistro>();

}