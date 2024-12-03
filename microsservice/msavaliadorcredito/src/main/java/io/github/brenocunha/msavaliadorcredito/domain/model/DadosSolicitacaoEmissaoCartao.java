package io.github.brenocunha.msavaliadorcredito.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosSolicitacaoEmissaoCartao {
    private String idCartao;
    private String cpf;
    private String endereco;
    private BigDecimal limiteLiberado;
}
