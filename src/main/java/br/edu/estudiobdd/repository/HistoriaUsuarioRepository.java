package br.edu.estudiobdd.repository;

import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Prioridade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {

    boolean existsByProjetoIdAndTituloIgnoreCase(Long projetoId, String titulo);

    boolean existsByProjetoIdAndTituloIgnoreCaseAndIdNot(Long projetoId, String titulo, Long id);

    List<HistoriaUsuario> findByProjetoIdOrderByIdAsc(Long projetoId);

    List<HistoriaUsuario> findByProjetoIdAndPrioridadeOrderByIdAsc(Long projetoId, Prioridade prioridade);

    long countByProjetoId(Long projetoId);
}
