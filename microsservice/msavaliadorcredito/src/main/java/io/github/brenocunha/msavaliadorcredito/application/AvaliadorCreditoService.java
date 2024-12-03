package io.github.brenocunha.msavaliadorcredito.application;

import feign.FeignException;
import io.github.brenocunha.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import io.github.brenocunha.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import io.github.brenocunha.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import io.github.brenocunha.msavaliadorcredito.domain.model.*;
import io.github.brenocunha.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.brenocunha.msavaliadorcredito.infra.clients.ClienteResourceClient;
import io.github.brenocunha.msavaliadorcredito.infra.mqueue.SolicitaeEmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    private final CartoesResourceClient cartoesResourceClient;
    private final SolicitaeEmissaoCartaoPublisher solicitaeEmissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> responseEntity = clienteResourceClient.dadosDoCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartaoClienteResponseEntity = cartoesResourceClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(responseEntity.getBody())
                    .cartoes(cartaoClienteResponseEntity.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf , Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> dadosDoClienteResponse = clienteResourceClient.dadosDoCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesByRenda(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            List<CartaoAprovado> listCartoesAprovados = cartoes.stream().map(cartao -> {
                DadosCliente dadosCliente = dadosDoClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado cartaoAprovado = new CartaoAprovado();
                cartaoAprovado.setBandeira(cartao.getBandeira());
                cartaoAprovado.setLimiteAprovado(limiteAprovado);
                cartaoAprovado.setCartao(cartao.getNome());

                return cartaoAprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listCartoesAprovados);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try {
            solicitaeEmissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);

        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
