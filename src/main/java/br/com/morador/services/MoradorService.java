package br.com.morador.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.morador.amqp.producer.impl.MoradorProducer;
import br.com.morador.amqp.producer.impl.ProcessoCadastroMoradorProducer;
import br.com.morador.amqp.producer.impl.VinculosProducer;
import br.com.morador.converter.Converter;
import br.com.morador.dto.AtualizaMoradorDto;
import br.com.morador.dto.AtualizaProcessoCadastroDto;
import br.com.morador.dto.CabecalhoResponsePublisherDto;
import br.com.morador.dto.GETMoradorResponseDto;
import br.com.morador.dto.GETMoradorSemResidenciasResponseDto;
import br.com.morador.dto.GETMoradoresResponseDto;
import br.com.morador.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.morador.dto.GETVinculoMoradorResidenciaResponseDto;
import br.com.morador.dto.GETVinculoResidenciaMoradorResponseDto;
import br.com.morador.dto.MoradorDto;
import br.com.morador.dto.ProcessoCadastroDto;
import br.com.morador.dto.ResponsePublisherDto;
import br.com.morador.dto.VinculoRequestDto;
import br.com.morador.dto.VinculoResidenciaRequestDto;
import br.com.morador.entities.Morador;
import br.com.morador.errorheadling.RegistroException;
import br.com.morador.filter.MoradorFilter;
import br.com.morador.mappers.MoradorMapper;
import br.com.morador.repositories.MoradorRepository;
import br.com.morador.response.Response;
import br.com.morador.senders.VinculosSender;
import br.com.morador.validators.Validators;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MoradorService {
	
	@Value("${guide.limit}")
	private int guideLimit;
	
	@Autowired
	private MoradorProducer producer;

	@Autowired
	private VinculosProducer vinculoProducer;
	
	@Autowired
	private ProcessoCadastroMoradorProducer processoProducer;
	
	@Autowired
	private MoradorMapper moradorMapper;
	
	@Autowired
	private MoradorRepository moradorRepository;
	
	@Autowired
	private VinculosSender vinculosSender;
	
	@Autowired
	private Validators<MoradorDto, AtualizaMoradorDto> validar;
	
	@Autowired
	private Validators<ProcessoCadastroDto, AtualizaProcessoCadastroDto> validarProcesso;
	
	@Autowired
	private Converter<List<GETMoradorSemResidenciasResponseDto>, List<Morador>> converter;
	
	@Autowired
	private Converter<GETMoradoresSemResidenciaResponseDto, List<Morador>> converterMorador;
	
	public ResponsePublisherDto salvar(MoradorDto moradorRequestBody) throws RegistroException {
		
		log.info("Cadastrando um morador: {}", moradorRequestBody.toString());
		
		moradorRequestBody.setGuide(UUID.randomUUID().toString()); 	
		
		this.validar.validarPost(moradorRequestBody);
		
		//Envia para a fila de Morador
		log.info("Enviando mensagem " +  moradorRequestBody.toString() + " para o consumer.");
		
		this.producer.producerAsync(moradorRequestBody);
		
		if (moradorRequestBody.getResidenciaId().compareTo(0L) != 0){
			VinculoRequestDto requestVinculo = VinculoRequestDto.builder()
					.cpfMorador(moradorRequestBody.getCpf())
					.residenciaId(moradorRequestBody.getResidenciaId().toString())
					.build();
			this.vinculoProducer.producerAsync(requestVinculo);
		}
		
		ResponsePublisherDto response = ResponsePublisherDto
				.builder()
				.ticket(CabecalhoResponsePublisherDto
						.builder()
						.ticket(moradorRequestBody.getGuide())
						.build())
				.build();
		
		return response;
		
	}
	
	public ResponsePublisherDto atualizar(AtualizaMoradorDto moradorRequestBody, Long id) throws RegistroException {

		log.info("Atualizando um morador: {}", moradorRequestBody.toString());
		
		moradorRequestBody.setGuide(UUID.randomUUID().toString()); 	
		
		this.validar.validarPut(moradorRequestBody, id);
		
		//Envia para a fila de Morador
		log.info("Enviando mensagem " +  moradorRequestBody.toString() + " para o consumer.");
		
		this.producer.producerAsync(this.mergeObject(this.moradorMapper.moradorToMoradorDto(moradorRepository.findById(id).get()), moradorRequestBody));
		
		ResponsePublisherDto response = ResponsePublisherDto
				.builder()
				.ticket(CabecalhoResponsePublisherDto
						.builder()
						.ticket(moradorRequestBody.getGuide())
						.build())
				.build();
		
		return response;
		
	}
	
	public ResponsePublisherDto salvarProcesso(ProcessoCadastroDto processoRequestBody) throws RegistroException {
		
		log.info("Cadastrando um morador: {}", processoRequestBody.toString());
		
		processoRequestBody.setGuide(UUID.randomUUID().toString());
		
		this.validarProcesso.validarPost(processoRequestBody);
		
		//Envia para a fila de Morador
		log.info("Enviando mensagem " +  processoRequestBody.toString() + " para o consumer.");
		
		this.processoProducer.producerAsync(processoRequestBody);
		
		VinculoRequestDto requestVinculo = VinculoRequestDto.builder()
				.cpfMorador(processoRequestBody.getMorador().getCpf())
				.cepResidencia(processoRequestBody.getMorador().getResidencia().getCep())
				.numeroResidencia(processoRequestBody.getMorador().getResidencia().getNumero())
				.complementoResidencia(processoRequestBody.getMorador().getResidencia().getComplemento())
				.build();
		this.vinculoProducer.producerAsync(requestVinculo);
		
		ResponsePublisherDto response = ResponsePublisherDto
				.builder()
				.ticket(CabecalhoResponsePublisherDto
						.builder()
						.ticket(processoRequestBody.getGuide())
						.build())
				.build();
		
		return response;
		
	}

	public Response<GETMoradoresSemResidenciaResponseDto> buscarPorResidencia(Long residenciaId) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		log.info("Buscando morador(es) por residencia id {}", residenciaId); 
		
		Response<GETMoradoresSemResidenciaResponseDto> response = new Response<GETMoradoresSemResidenciaResponseDto>();
		
		List<String> ids = new ArrayList<>();
		
		VinculoResidenciaRequestDto request = VinculoResidenciaRequestDto.builder()
				.residenciaId(residenciaId)
				.build();
		
		GETVinculoResidenciaMoradorResponseDto vinculos = this.vinculosSender.buscarMoradoresPorResidencia(request);
		
		GETMoradoresSemResidenciaResponseDto moradores = new GETMoradoresSemResidenciaResponseDto();
		
		ids = vinculos.getResidencia().getMoradores().moradores.stream().map(m -> m.getId().toString()).collect(Collectors.toList());
		moradores.setMoradores(this.converter.convert(this.moradorRepository.findMoradoresById(ids)));
		
		response.setData(moradores);
		
		return response;
	}
	
	public Response<GETMoradoresSemResidenciaResponseDto> buscarPorFiltros(MoradorFilter filter) {
		
		log.info("Buscando morador(es) por filtros..."); 
		
		Response<GETMoradoresSemResidenciaResponseDto> response = new Response<GETMoradoresSemResidenciaResponseDto>();
				
		GETMoradoresSemResidenciaResponseDto moradores = new GETMoradoresSemResidenciaResponseDto();
		
		moradores.setMoradores(this.converter.convert(this.moradorRepository.findMoradorBy(filter)));
		
		response.setData(moradores);
		
		return response;
	}

	public Page<?> buscar(MoradorFilter filtros, Pageable pageable) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		log.info("Buscando morador(es)...");
		
		if (filtros.getDetalhaResidencia() == null)
			filtros.setDetalhaResidencia(Boolean.FALSE);
		
		Response<GETMoradoresResponseDto> response = new Response<GETMoradoresResponseDto>(); 
		List<GETMoradorResponseDto> listMoradores = new ArrayList<>();
		
		List<Morador> moradores = this.moradorRepository.findMoradorBy(filtros, pageable);
		
		long total = this.moradorRepository.totalRegistros(filtros);
		
		GETMoradorResponseDto moradorResponse = null;
		for (Morador morador : moradores) {			
			moradorResponse = moradorMapper.moradorToGETMoradorResponseDto(morador);
			if (filtros.getDetalhaResidencia().equals(Boolean.TRUE)) {
				
				VinculoResidenciaRequestDto request = VinculoResidenciaRequestDto.builder()
						.moradorId(morador.getId())
						.build();
				
				GETVinculoMoradorResidenciaResponseDto vinculos = this.vinculosSender.buscarResidenciasPorMorador(request);
				
				moradorResponse.setResidencias(vinculos.getMorador().getResidencias());
			} else {
				moradorResponse.setResidencias(null);
			}
			
			listMoradores.add(moradorResponse);
		}
		
		GETMoradoresResponseDto moradoresResponse = GETMoradoresResponseDto.builder()
				.moradores(listMoradores)
				.build();
		
		response.setData(moradoresResponse);
		
		return new PageImpl<>(response.getData().getMoradores(), pageable, total);
	}

	public Optional<GETMoradoresSemResidenciaResponseDto> buscar(MoradorFilter filter) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		log.info("Buscando morador(es)...");
		
		VinculoResidenciaRequestDto request = VinculoResidenciaRequestDto.builder()
				.residenciaId(filter.getResidenciaId())
				.build();
		
		GETVinculoResidenciaMoradorResponseDto vinculos = this.vinculosSender.buscarMoradoresPorResidencia(request);
		
		List<String> ids = vinculos.getResidencia().getMoradores().moradores.stream().map(m -> m.getId().toString()).collect(Collectors.toList());
		
		return Optional.ofNullable(this.converterMorador.convert(this.moradorRepository.findMoradoresById(ids)));
	}
	
	public Response<GETMoradoresSemResidenciaResponseDto> buscarPorIds(List<String> ids){
		
		log.info("Buscando residencia(s)...");
		
		List<GETMoradorSemResidenciasResponseDto> listaMoradores = new ArrayList<>();
		
		Response<GETMoradoresSemResidenciaResponseDto> response = new Response<GETMoradoresSemResidenciaResponseDto>();
		
		List<Morador> moradores = this.moradorRepository.findMoradoresById(ids);
		
		for (Morador morador : moradores) {			
			GETMoradorSemResidenciasResponseDto moradorResponse = moradorMapper.moradorToGETMoradorSemResidenciasResponseDto(morador);
			listaMoradores.add(moradorResponse);
		}
		
		GETMoradoresSemResidenciaResponseDto queryMoradores = new GETMoradoresSemResidenciaResponseDto();
		
		queryMoradores.setMoradores(listaMoradores);
		
		response.setData(queryMoradores);
		
		return response;
		
	}
	
	public MoradorDto mergeObject(MoradorDto t, AtualizaMoradorDto x) {
		
		t.setNome(x.getNome());
		t.setEmail(x.getEmail());
		t.setRg(x.getRg());
		t.setTelefone(x.getTelefone());
		t.setCelular(x.getCelular());
		t.setPerfil(x.getPerfil());
		t.setResidenciaId(x.getResidenciaId());
		t.setAssociado(x.getAssociado());
		t.setPosicao(x.getPosicao());
		t.setGuide(x.getGuide());
		
		return t;
	}

}
