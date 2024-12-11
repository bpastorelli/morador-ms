package br.com.morador.filter;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MoradorFilter implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String nome;
	
	private String cpf;
	
	private String rg;
	
	private String email;
	
	private Long posicao;
	
	private String guide;
	
	private Long residenciaId;
	
	private Boolean detalhaResidencia;
	
	private boolean content;

}
