package io.github.brenocunha.mscartoes.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosSolicitacaoEmissaoCartao {
    private String idCartao;
    private String cpf;
    private String endereco;
    private BigDecimal limiteLiberado;
}
