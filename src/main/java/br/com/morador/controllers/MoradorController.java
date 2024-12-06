package br.com.morador.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.morador.dto.AtualizaMoradorDto;
import br.com.morador.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.morador.dto.MoradorDto;
import br.com.morador.dto.ProcessoCadastroDto;
import br.com.morador.dto.ResponsePublisherDto;
import br.com.morador.errorheadling.RegistroException;
import br.com.morador.errorheadling.RegistroExceptionHandler;
import br.com.morador.filter.MoradorFilter;
import br.com.morador.services.MoradorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
	
@Slf4j
@RestController
@Api(tags = "Cadastro de Moradores")
@RequestMapping("/sgc/morador")
@CrossOrigin(origins = "*")
public class MoradorController extends RegistroExceptionHandler {
	
	@Autowired
	private MoradorService moradorService;
		
	/**
	 * Envia um objeto tipo MoradorDto para o Consumer
	 * @param moradorRequestBody
	 * @param result 
	 * @return ResponsePublisher
	 * @throws Exception
	 */
	@ApiOperation(value = "Produz uma nova mensagem no Kafka para cadastramento de um novo morador.")
	@PostMapping(value = "/amqp/novo")
	public ResponseEntity<?> cadastrarNovoAMQP( 
			@Valid @RequestBody MoradorDto moradorRequestBody,
			BindingResult result) throws RegistroException{
		
		log.info("Enviando mensagem para o consumer...");
		
		ResponsePublisherDto response = this.moradorService.salvar(moradorRequestBody);
		
		return response.getTicket() == null ? 
				ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response.getErrors()) : 
				ResponseEntity.status(HttpStatus.ACCEPTED).body(response.getTicket());
		
	}
	
	/**
	 * Envia um objeto tipo MoradorDto para o Consumer
	 * @param moradorRequestBody
	 * @param result 
	 * @return ResponsePublisher
	 * @throws Exception
	 */
	
	@ApiOperation(value = "Produz uma nova mensagem no Kafka para cadastro de um novo morador e uma nova residência (se não existir).")
	@PostMapping(value = "/amqp/processo")
	public ResponseEntity<?> processoCadastroAMQP( 
			@Valid @RequestBody ProcessoCadastroDto processoRequestBody,
			BindingResult result) throws RegistroException{
		
		log.info("Enviando mensagem para o consumer...");
		
		ResponsePublisherDto response = this.moradorService.salvarProcesso(processoRequestBody);
		
		return response.getTicket() == null ? 
				ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response.getErrors()) : 
				ResponseEntity.status(HttpStatus.ACCEPTED).body(response.getTicket());
		
	}
	
	@ApiOperation(value = "Produz uma nova mensagem no Kafka para atualização de um cadastro de morador.")
	@PutMapping(value = "/amqp/alterar")
	public ResponseEntity<?> alterarAMQP( 
			@Valid @RequestBody AtualizaMoradorDto moradorRequestBody,
			@RequestParam(value = "id", defaultValue = "null") Long id,
			BindingResult result) throws RegistroException{
		
		log.info("Enviando mensagem para o consumer...");
		
		moradorRequestBody.setId(id);
		ResponsePublisherDto response = this.moradorService.atualizar(moradorRequestBody, id);
		
		return response.getTicket() == null ? 
				ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response.getErrors()) : 
				ResponseEntity.status(HttpStatus.ACCEPTED).body(response.getTicket());
		
	}
	
	@ApiOperation(value = "Pesquisa moradores a partir dos filtros informados.")
	@GetMapping(value = "/filtro")
	public ResponseEntity<?> buscarMoradoresFiltro(
			MoradorFilter filters,
			@PageableDefault(sort = "nome", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) throws NoSuchAlgorithmException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		Page<?> moradores = this.moradorService.buscar(filters, paginacao);
		
		return filters.isContent() ? new ResponseEntity<>(moradores.getContent(), HttpStatus.OK) :
					new ResponseEntity<>(moradores, HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "Pesquisa moradores a partir do id da residência.")
	@GetMapping(value = "/residencia")
	public ResponseEntity<?> buscarMoradoresPorResidencia(
			@RequestParam(value = "residenciaId", defaultValue = "0") Long residenciaId) throws NoSuchAlgorithmException {
		
		GETMoradoresSemResidenciaResponseDto moradores = this.moradorService.buscar(residenciaId);
		
		return new ResponseEntity<>(moradores.getMoradores(), HttpStatus.OK);
		
	}

	
}
