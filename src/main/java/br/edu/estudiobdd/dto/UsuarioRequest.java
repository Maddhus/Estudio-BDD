package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 120) String nome,
        @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "E-mail inválido.") @Size(max = 180) String email) {

    public Usuario paraEntidade() {
        return new Usuario(nome, email);
    }
}
