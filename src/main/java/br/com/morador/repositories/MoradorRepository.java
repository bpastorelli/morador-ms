package br.com.morador.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.morador.entities.Morador;
import br.com.morador.filter.MoradorFilter;

@Repository
@Transactional(readOnly = true)
public interface MoradorRepository extends JpaRepository<Morador, Long> {
	
	Optional<Morador> findByNome(String nome);
	
	Optional<Morador> findByNomeAndCpf(String nome, String cpf);
	
	Optional<Morador> findByCpf(String cpf);
	
	List<Morador> findByCpfIn(List<String> cpfs);
	
	Optional<Morador> findByRg(String rg);
	
	Optional<Morador> findByEmail(String email);
	
	Optional<Morador> findByEmailAndPosicao(String email, Long posicao);
	
	Optional<Morador> findByGuide(String guide);
	
	Optional<Morador> findByEmailAndSenha(String username, String senha);
	
	@Transactional(readOnly = true)
	Page<Morador> findByIdOrCpfOrRgOrNomeContainsOrEmailOrPosicao(Long id, String cpf, String rg, String nome, String email, Long posicao, Pageable pageable);
	
	Page<Morador> findByPosicao(Long posicao, Pageable pageable);
	
	
	@Query(value = "select * "
			+ " from morador m "
			+ " inner join vinculo_residencia v"
			+ " on v.morador_id = m.id"
			+ " inner join residencia r"
			+ " on v.residencia_id = r.id"
			+ " where (r.id = :residenciaId) ", nativeQuery = true)
	public List<Morador> findByResidenciaId(@Param("residenciaId") Long residenciaId);
	
	@Query(value = "select * "
			+ " from morador m "
			+ " inner join vinculo_residencia v"
			+ " on v.morador_id = m.id"
			+ " inner join residencia r"
			+ " on v.residencia_id = r.id"
			+ " where (m.id = :#{#filter.id} OR :#{#filter.id} IS NULL) "
			+ " and (m.nome like %:#{#filter.nome}% OR :#{#filter.nome} IS NULL) "
			+ " and (m.cpf = :#{#filter.cpf} OR :#{#filter.cpf} IS NULL) "
			+ " and (m.rg = :#{#filter.rg} OR :#{#filter.rg} IS NULL) "
			+ " and (m.email = :#{#filter.email} OR :#{#filter.email} IS NULL) "
			+ " and (m.posicao = :#{#filter.posicao} OR :#{#filter.posicao} IS NULL) "
			, nativeQuery = true)
	public List<Morador> findMoradorBy(@Param("filter") MoradorFilter filter);
	
	@Query(value = "select *"
			+ " from morador m "
			+ " where (m.id = :#{#filter.id} OR :#{#filter.id} IS NULL) "
			+ " and (m.nome like %:#{#filter.nome}% OR :#{#filter.nome} IS NULL) "
			+ " and (m.cpf = :#{#filter.cpf} OR :#{#filter.cpf} IS NULL) "
			+ " and (m.rg = :#{#filter.rg} OR :#{#filter.rg} IS NULL) "
			+ " and (m.email = :#{#filter.email} OR :#{#filter.email} IS NULL) "
			+ " and (m.posicao = :#{#filter.posicao} OR :#{#filter.posicao} IS NULL) "
			, nativeQuery = true)
	public List<Morador> findMoradorBy(@Param("filter") MoradorFilter filter, Pageable pageable);
	
	@Query(value = "select count(*)"
			+ " from morador m "
			+ " where (m.id = :#{#filter.id} OR :#{#filter.id} IS NULL) "
			+ " and (m.nome like %:#{#filter.nome}% OR :#{#filter.nome} IS NULL) "
			+ " and (m.cpf = :#{#filter.cpf} OR :#{#filter.cpf} IS NULL) "
			+ " and (m.rg = :#{#filter.rg} OR :#{#filter.rg} IS NULL) "
			+ " and (m.email = :#{#filter.email} OR :#{#filter.email} IS NULL) "
			+ " and (m.posicao = :#{#filter.posicao} OR :#{#filter.posicao} IS NULL)"
			, nativeQuery = true)
	public Long totalRegistros(@Param("filter") MoradorFilter filter);
	
}
