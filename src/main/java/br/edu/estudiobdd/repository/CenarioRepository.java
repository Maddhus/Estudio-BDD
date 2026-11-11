package br.edu.estudiobdd.repository;

import br.edu.estudiobdd.model.Cenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CenarioRepository extends JpaRepository<Cenario, Long> {

    boolean existsByHistoriaIdAndTituloIgnoreCase(Long historiaId, String titulo);

    boolean existsByHistoriaIdAndTituloIgnoreCaseAndIdNot(Long historiaId, String titulo, Long id);

    /** Linhas {historiaId, quantidade} dos cenários de cada história do projeto. */
    @Query("select h.id, count(c) from Cenario c join c.historia h "
            + "where h.projeto.id = :projetoId group by h.id")
    List<Object[]> contarPorHistoria(@Param("projetoId") Long projetoId);

    /** Linhas {origem, quantidade} dos cenários do projeto. */
    @Query("select c.origem, count(c) from Cenario c join c.historia h "
            + "where h.projeto.id = :projetoId group by c.origem")
    List<Object[]> contarPorOrigem(@Param("projetoId") Long projetoId);
}
