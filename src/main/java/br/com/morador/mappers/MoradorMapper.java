package br.com.morador.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import br.com.morador.dto.AtualizaMoradorDto;
import br.com.morador.dto.GETMoradorResponseDto;
import br.com.morador.dto.GETMoradorSemResidenciasResponseDto;
import br.com.morador.dto.MoradorDto;
import br.com.morador.entities.Morador;

@Mapper(componentModel = "spring")
public interface MoradorMapper {
	
	public abstract Morador moradorDtoToMorador(MoradorDto dto);
	
	public abstract MoradorDto moradorToMoradorDto(Morador morador);
	
	public abstract List<GETMoradorResponseDto> listMoradorToListGETMoradorResponseDto(List<Morador> moradores);
	
	public abstract List<Morador> listMoradorDtoToListMorador(List<MoradorDto> moradoresDto);
	
	@Mapping(source = "nome", target = "nome", qualifiedByName = "ToUpperCase")
	public abstract GETMoradorResponseDto moradorToGETMoradorResponseDto(Morador morador);
	
	@Mapping(source = "nome", target = "nome", qualifiedByName = "ToUpperCase")
	public abstract GETMoradorSemResidenciasResponseDto moradorToGETMoradorSemResidenciasResponseDto(Morador morador);
	
	public abstract MoradorDto atualizaMoradorDtoToMoradorDto(AtualizaMoradorDto dto);
	
	public abstract Morador getMoradorResponseDtoToMorador(GETMoradorResponseDto dto);
	
	@Named("ToUpperCase")
	default String toUpperCase(String value) {
		
		return value.toUpperCase();
		
	}

}
