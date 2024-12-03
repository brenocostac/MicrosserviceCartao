package io.github.brenocunha.msclientes.application.representation;

import io.github.brenocunha.msclientes.domain.Cliente;
import lombok.Data;
@Data
public class ClientSaveRequest {
    private String cpf;
    private String nome;
    private Integer idade;

    public Cliente toModel(){
        return new Cliente(this.nome,this.cpf,this.idade);
    }
}
