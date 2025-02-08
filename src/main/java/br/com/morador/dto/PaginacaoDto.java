package br.com.morador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginacaoDto {
	
	private int pagina;
	
	private int paginaAnterior;
	
	private int proximaPagina;
	
	private int totalPaginas;
	
	private long totalItems;

}
