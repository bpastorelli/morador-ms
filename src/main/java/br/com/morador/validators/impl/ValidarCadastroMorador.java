package br.com.morador.validators.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.morador.commons.ValidaCPF;
import br.com.morador.dto.AtualizaMoradorDto;
import br.com.morador.dto.MoradorDto;
import br.com.morador.entities.Morador;
import br.com.morador.enums.PerfilEnum;
import br.com.morador.errorheadling.ErroRegistro;
import br.com.morador.errorheadling.RegistroException;
import br.com.morador.repositories.MoradorRepository;
import br.com.morador.validators.Validators;

@Component
public class ValidarCadastroMorador implements Validators<MoradorDto, AtualizaMoradorDto> {
	
	@Autowired
	private MoradorRepository moradorRepository;
	
	private static final String TITULO = "Cadastro de morador recusado!";
	
	@Override
	public void validarPost(List<MoradorDto> t) throws RegistroException {
		
		RegistroException errors = new RegistroException();
		
		if(t.size() == 0) 
			errors.getErros().add(new ErroRegistro("", TITULO, " Você deve informar ao menos um morador"));	
		
		for(MoradorDto morador : t) {
				
				morador.setGuide(UUID.randomUUID().toString());
				morador.setPerfil(morador.getPerfil() == null ? PerfilEnum.ROLE_USUARIO : PerfilEnum.ROLE_ADMIN);
				morador.setResidenciaId(morador.getResidenciaId() == null ? 0 : morador.getResidenciaId());
				morador.setPosicao((long) 1);
				
				if(morador.getNome().isEmpty())
					errors.getErros().add(new ErroRegistro("", TITULO, " O campo Nome é obrigatório"));
				
				if(morador.getCpf().isEmpty())
					errors.getErros().add(new ErroRegistro("", TITULO, " O campo CPF é obrigatório"));
				
				if(!ValidaCPF.isCPF(morador.getCpf()))
					errors.getErros().add(new ErroRegistro("", TITULO, " CPF " + morador.getCpf() + " inválido"));
				
				if(morador.getRg().isEmpty())
					errors.getErros().add(new ErroRegistro("", TITULO, " O campo RG é obrigatório"));
				
				if(morador.getEmail().isEmpty())
					errors.getErros().add(new ErroRegistro("" , TITULO, " O campo e-mail é obrigatório"));
				
				if(morador.getTelefone().isEmpty() && morador.getCelular().isEmpty())
					errors.getErros().add(new ErroRegistro("", TITULO, " Você deve informar um número de telefone ou celular"));
			

		}
		
		t.forEach(morador ->{
			
				this.moradorRepository.findByNome(morador.getNome())
				.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " Nome '" + morador.getNome() + "' já existe")));	

		});
		
		t.forEach(morador ->{

				this.moradorRepository.findByCpf(morador.getCpf())
					.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " CPF '" + morador.getCpf() + "' já existe")));
		});
		
		t.forEach(morador ->{
			
				this.moradorRepository.findByRg(morador.getRg())
				.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " RG '" + morador.getRg() + "' já existe")));	
		});
	
		t.forEach(morador ->{
		
				this.moradorRepository.findByEmail(morador.getEmail())
				.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " E-mail '" + morador.getEmail() + "' já existe")));	
		});
		
		//Valida se o CPF não está duplicado na requisição.
		t.forEach(morador -> {
			if(t
					.stream()
					.filter(pessoa -> pessoa.getCpf()
					.equals(morador.getCpf())).count() > 1)
				errors.getErros().add(new ErroRegistro("", TITULO, " CPF '" + morador.getCpf() + "' está duplicado"));
		});	
		
		//Valida se o RG não está duplicado na requisição.
		t.forEach(morador -> {
			if(t
					.stream()
					.filter(pessoa -> pessoa.getRg()
					.equals(morador.getRg())).count() > 1)
				errors.getErros().add(new ErroRegistro("", TITULO, " RG '" + morador.getRg() + "' está duplicado"));
		});
		
		//Valida se o E-mail não está duplicado na requisição.
		t.forEach(morador -> {
			if(t
				.stream()
				.filter(pessoa -> pessoa.getEmail()
				.equals(morador.getEmail())).count() > 1) {
				errors.getErros().add(new ErroRegistro("", TITULO, " E-mail '" + morador.getEmail() + "' está duplicado"));				
			}
		});

		if(!errors.getErros().isEmpty())
			throw errors;
		
	}

	@Override
	public void validarPut(AtualizaMoradorDto morador, Long id) throws RegistroException {
		
		RegistroException errors = new RegistroException();
							
		Optional<Morador> moradorSource = this.moradorRepository.findById(morador.getId());
				
		if(!moradorSource.isPresent()) {
			errors.getErros().add(new ErroRegistro("", TITULO, " O morador informado não existe!"));
			throw errors;
		}
				
		if(!morador.getNome().toUpperCase().equals(moradorSource.get().getNome().toUpperCase())) {
			if(this.moradorRepository.findByNome(morador.getNome()).isPresent())
				errors.getErros().add(new ErroRegistro("", TITULO, " O novo nome (" + morador.getNome() + ") informado já existe!"));
		}
		
		if(!morador.getEmail().toLowerCase().equals(moradorSource.get().getEmail().toLowerCase())) {
			if(this.moradorRepository.findByEmail(morador.getEmail()).isPresent())
				errors.getErros().add(new ErroRegistro("", TITULO, " O novo e-mail (" + morador.getEmail() + ") informado já existe!"));
		}
				
		morador.setPerfil(moradorSource.get().getPerfil());
		morador.setGuide(moradorSource.get().getGuide());

		if(!errors.getErros().isEmpty())
			throw errors;
		
		
	}

	@Override
	public void validarPost(MoradorDto morador) throws RegistroException {
		
		RegistroException errors = new RegistroException();
				
		morador.setGuide(UUID.randomUUID().toString());
		morador.setPerfil(morador.getPerfil() == null ? PerfilEnum.ROLE_USUARIO : PerfilEnum.ROLE_ADMIN);
		morador.setResidenciaId(morador.getResidenciaId() == null ? 0 : morador.getResidenciaId());
		morador.setPosicao((long) 1);
				
		if(morador.getNome().isEmpty())
			errors.getErros().add(new ErroRegistro("", TITULO, " O campo Nome é obrigatório"));
				
		if(morador.getCpf().isEmpty())
			errors.getErros().add(new ErroRegistro("", TITULO, " O campo CPF é obrigatório"));
				
		if(!ValidaCPF.isCPF(morador.getCpf()))
			errors.getErros().add(new ErroRegistro("", TITULO, " CPF " + morador.getCpf() + " inválido"));
				
		if(morador.getRg().isEmpty())
			errors.getErros().add(new ErroRegistro("", TITULO, " O campo RG é obrigatório"));
				
		if(morador.getEmail().isEmpty())
			errors.getErros().add(new ErroRegistro("" , TITULO, " O campo e-mail é obrigatório"));
				
		if(morador.getTelefone().isEmpty() && morador.getCelular().isEmpty())
			errors.getErros().add(new ErroRegistro("", TITULO, " Você deve informar um número de telefone ou celular"));
			
		this.moradorRepository.findByNome(morador.getNome())
			.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " Nome '" + morador.getNome() + "' já existe")));	

		this.moradorRepository.findByCpf(morador.getCpf())
			.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " CPF '" + morador.getCpf() + "' já existe")));
			
		this.moradorRepository.findByRg(morador.getRg())
			.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " RG '" + morador.getRg() + "' já existe")));	
		
		this.moradorRepository.findByEmail(morador.getEmail())
			.ifPresent(res -> errors.getErros().add(new ErroRegistro("", TITULO, " E-mail '" + morador.getEmail() + "' já existe")));
		
		if(!errors.getErros().isEmpty())
			throw errors;
		
	}

	@Override
	public void validarPut(List<AtualizaMoradorDto> listDto) throws RegistroException {
		// TODO Auto-generated method stub
		
	}

}
