package io.github.brenocunha.msavaliadorcredito.infra.clients;

import io.github.brenocunha.msavaliadorcredito.domain.model.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msclientes", path = "/clientes")
public interface ClienteResourceClient {

    @GetMapping(params = "cpf")
    public ResponseEntity<DadosCliente> dadosDoCliente(@RequestParam("cpf") String cpf);
}
