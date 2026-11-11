package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.UsuarioNaoIdentificadoException;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("nome"));
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado."));
    }

    /**
     * Identidade simulada: o id recebido no cabeçalho X-Usuario-Id vira o "usuário atual".
     * Não é autenticação (qualquer um pode informar qualquer id).
     */
    public Usuario exigirIdentificado(Long usuarioId) {
        if (usuarioId == null) {
            throw new UsuarioNaoIdentificadoException(
                    "Informe o cabeçalho X-Usuario-Id com o id do usuário atual.");
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoIdentificadoException(
                        "O usuário " + usuarioId + " informado em X-Usuario-Id não existe."));
    }

    @Transactional
    public Usuario criar(Usuario dados) {
        String email = dados.getEmail().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Já existe um usuário com o e-mail " + email + ".");
        }
        return usuarioRepository.save(new Usuario(dados.getNome().trim(), email));
    }
}
