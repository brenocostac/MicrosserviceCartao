package io.github.brenocunha.msclientes.application;

import io.github.brenocunha.msclientes.application.representation.ClientSaveRequest;
import io.github.brenocunha.msclientes.domain.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientResource {

    private final ClientService service;

    @GetMapping
    public String status(){
        log.info("Obtendo o status do microsservice de clientes");
        return "OK";
    }

    @PostMapping
    public ResponseEntity save (@RequestBody ClientSaveRequest request){
        Cliente cliente = request.toModel();
        service.save(cliente);
        URI headerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("cpf=${cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();

        return ResponseEntity.created(headerLocation).build();

    }

    @GetMapping(params = "cpf")
    public ResponseEntity dadosDoCliente(@RequestParam("cpf") String cpf){
        var cliente = service.getByCpf(cpf);
        if(cliente.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cliente);
    }
}


