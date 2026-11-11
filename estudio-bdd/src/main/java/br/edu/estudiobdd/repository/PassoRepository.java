package br.edu.estudiobdd.repository;

import br.edu.estudiobdd.model.Passo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassoRepository extends JpaRepository<Passo, Long> {
}
