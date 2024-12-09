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
import br.com.morador.converter.Converter;
import br.com.morador.dto.AtualizaMoradorDto;
import br.com.morador.dto.AtualizaProcessoCadastroDto;
import br.com.morador.dto.CabecalhoResponsePublisherDto;
import br.com.morador.dto.GETMoradorResponseDto;
import br.com.morador.dto.GETMoradorSemResidenciasResponseDto;
import br.com.morador.dto.GETMoradoresResponseDto;
import br.com.morador.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.morador.dto.GETVinculoMoradorResidenciaResponseDto;
import br.com.morador.dto.MoradorDto;
import br.com.morador.dto.ProcessoCadastroDto;
import br.com.morador.dto.ResponsePublisherDto;
import br.com.morador.dto.VinculoResidenciaRequestDto;
import br.com.morador.entities.Morador;
import br.com.morador.errorheadling.RegistroException;
import br.com.morador.filter.MoradorFilter;
import br.com.morador.mappers.MoradorMapper;
import br.com.morador.repositories.MoradorRepository;
import br.com.morador.repositories.VinculoResidenciaRepository;
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
	private ProcessoCadastroMoradorProducer processoProducer;
	
	@Autowired
	private MoradorMapper moradorMapper;
	
	@Autowired
	private MoradorRepository moradorRepository;
	
	@Autowired
	private VinculoResidenciaRepository vinculoRepository;
	
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
		
		moradorRequestBody.setGuide(this.gerarGuide()); 	
		
		this.validar.validarPost(moradorRequestBody);
		
		//Envia para a fila de Morador
		log.info("Enviando mensagem " +  moradorRequestBody.toString() + " para o consumer.");
		
		this.producer.producerAsync(moradorRequestBody);
		
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
		
		moradorRequestBody.setGuide(this.gerarGuide()); 	
		
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
		
		processoRequestBody.setGuide(this.gerarGuide());
		
		this.validarProcesso.validarPost(processoRequestBody);
		
		//Envia para a fila de Morador
		log.info("Enviando mensagem " +  processoRequestBody.toString() + " para o consumer.");
		
		this.processoProducer.producerAsync(processoRequestBody);
		
		ResponsePublisherDto response = ResponsePublisherDto
				.builder()
				.ticket(CabecalhoResponsePublisherDto
						.builder()
						.ticket(processoRequestBody.getGuide())
						.build())
				.build();
		
		return response;
		
	}

	public Response<GETMoradoresSemResidenciaResponseDto> buscarPorResidencia(Long residenciaId) {
		
		log.info("Buscando morador(es) por residencia id {}", residenciaId); 
		
		Response<GETMoradoresSemResidenciaResponseDto> response = new Response<GETMoradoresSemResidenciaResponseDto>();
		
		List<String> ids = vinculoRepository.findByResidenciaId(residenciaId).stream().map(m -> m.getMorador().getId().toString()).collect(Collectors.toList());
		
		GETMoradoresSemResidenciaResponseDto moradores = new GETMoradoresSemResidenciaResponseDto();
		
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
		
		Response<GETMoradoresResponseDto> response = new Response<GETMoradoresResponseDto>(); 
		List<GETMoradorResponseDto> listMoradores = new ArrayList<>();
		
		List<Morador> moradores = this.moradorRepository.findMoradorBy(filtros, pageable);
		
		long total = this.moradorRepository.totalRegistros(filtros);
		
		for (Morador morador : moradores) {
			
			GETMoradorResponseDto moradorResponse = moradorMapper.moradorToGETMoradorResponseDto(morador);
			
			VinculoResidenciaRequestDto request = VinculoResidenciaRequestDto.builder()
					.moradorId(morador.getId())
					.build();
			
			GETVinculoMoradorResidenciaResponseDto vinculos = this.vinculosSender.buscarResidencias(request);
			
			moradorResponse.setResidencias(vinculos.getMorador().getResidencias());
			listMoradores.add(moradorResponse);
		}
		
		GETMoradoresResponseDto moradoresResponse = GETMoradoresResponseDto.builder()
				.moradores(listMoradores)
				.build();
		
		response.setData(moradoresResponse);
		
		return new PageImpl<>(response.getData().getMoradores(), pageable, total);
	}

	public Optional<GETMoradoresSemResidenciaResponseDto> buscar(MoradorFilter filter) {
		
		log.info("Buscando morador(es)...");
		
		List<String> ids = vinculoRepository.findByResidenciaId(filter.getResidenciaId()).stream().map(m -> m.getMorador().getId().toString()).collect(Collectors.toList());
		
		return Optional.ofNullable(this.converterMorador.convert(this.moradorRepository.findMoradoresById(ids)));
	}
	
	public String gerarGuide() {

		String guide = null;
		int i = 0;
		boolean ticketValido = false;
		
		do {
			i++;
			if(this.moradorRepository.findByGuide(guide).isPresent())
				guide = UUID.randomUUID().toString();
			else if(guide == null)
				guide = UUID.randomUUID().toString();
			else {
				ticketValido = true;
			}
			
		}while(!ticketValido && i < guideLimit);
		
		return guide;
		
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
