package io.github.brenocunha.mscartoes.application;

import io.github.brenocunha.mscartoes.domain.ClienteCartao;
import io.github.brenocunha.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
    private final ClienteCartaoRepository clienteCartaoRepository;

    public List<ClienteCartao> listCartaoClienteByCpf(String cpf) {
        return clienteCartaoRepository.findByCpf(cpf);
    }
}
