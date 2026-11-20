package br.edu.ifto.pwebII.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DefinirSenhaForm {

    @NotBlank(message = "O link para definição de senha é inválido.")
    private String token;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres.")
    private String senha;

    @NotBlank(message = "A confirmação da senha é obrigatória.")
    private String confirmacaoSenha;

    @AssertTrue(message = "A confirmação de senha não corresponde à senha informada.")
    public boolean isSenhaConfirmada() {
        return senha != null && senha.equals(confirmacaoSenha);
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getConfirmacaoSenha() { return confirmacaoSenha; }
    public void setConfirmacaoSenha(String confirmacaoSenha) { this.confirmacaoSenha = confirmacaoSenha; }
}
