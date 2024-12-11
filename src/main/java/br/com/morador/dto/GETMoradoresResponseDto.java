package br.com.morador.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

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
public class GETMoradoresResponseDto {

	@JsonUnwrapped
	public List<GETMoradorResponseDto> moradores;
	
}
