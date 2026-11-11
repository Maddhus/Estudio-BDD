package br.edu.estudiobdd.arquitetura;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Regras da arquitetura em cinco camadas. Rodam junto com os demais testes em "mvn test".
 * Os pacotes são escritos por extenso para não casar, por engano, com pacotes de bibliotecas
 * (por exemplo, org.springframework.data.jpa.repository).
 */
@AnalyzeClasses(packages = "br.edu.estudiobdd", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

    /** Regra exigida no enunciado: o controller fala com o serviço, nunca direto com o repositório. */
    @ArchTest
    static final ArchRule controllersNaoAcessamRepositorios =
            noClasses().that().resideInAPackage("br.edu.estudiobdd.controller..")
                    .should().dependOnClassesThat().resideInAPackage("br.edu.estudiobdd.repository..");

    @ArchTest
    static final ArchRule servicosNaoDependemDeControllers =
            noClasses().that().resideInAPackage("br.edu.estudiobdd.service..")
                    .should().dependOnClassesThat().resideInAPackage("br.edu.estudiobdd.controller..");

    @ArchTest
    static final ArchRule repositoriosNaoDependemDeCamadasSuperiores =
            noClasses().that().resideInAPackage("br.edu.estudiobdd.repository..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "br.edu.estudiobdd.service..", "br.edu.estudiobdd.controller..");

    @ArchTest
    static final ArchRule dominioNaoDependeDeOutrasCamadas =
            noClasses().that().resideInAPackage("br.edu.estudiobdd.model..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "br.edu.estudiobdd.service..", "br.edu.estudiobdd.controller..",
                            "br.edu.estudiobdd.repository..", "br.edu.estudiobdd.dto..");
}
