package br.com.morador.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaginacaoDto {
	
	private int pagina;
	
	private int paginaAnterior;
	
	private int proximaPagina;
	
	private int totalPaginas;
	
	private long totalItems;

}
