package br.edu.estudiobdd.repository;

import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.Sugestao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    List<Sugestao> findByHistoriaIdOrderByIdAsc(Long historiaId);

    List<Sugestao> findByHistoriaIdAndDecisaoOrderByIdAsc(Long historiaId, DecisaoSugestao decisao);

    /** Linhas {historiaId, quantidade} das sugestões com a decisão informada, por história do projeto. */
    @Query("select h.id, count(s) from Sugestao s join s.historia h "
            + "where h.projeto.id = :projetoId and s.decisao = :decisao group by h.id")
    List<Object[]> contarPorHistoriaEDecisao(@Param("projetoId") Long projetoId,
                                             @Param("decisao") DecisaoSugestao decisao);

    /** Linhas {decisao, quantidade} das sugestões do projeto. */
    @Query("select s.decisao, count(s) from Sugestao s join s.historia h "
            + "where h.projeto.id = :projetoId group by s.decisao")
    List<Object[]> contarPorDecisao(@Param("projetoId") Long projetoId);

    /** Linhas {motivo, quantidade} das sugestões rejeitadas do projeto. */
    @Query("select s.motivoRejeicao, count(s) from Sugestao s join s.historia h "
            + "where h.projeto.id = :projetoId and s.decisao = :decisao and s.motivoRejeicao is not null "
            + "group by s.motivoRejeicao")
    List<Object[]> contarMotivosRejeicao(@Param("projetoId") Long projetoId,
                                         @Param("decisao") DecisaoSugestao decisao);
}
