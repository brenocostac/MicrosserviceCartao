package io.github.brenocunha.msavaliadorcredito.application.ex;

public class ErroSolicitacaoCartaoException extends RuntimeException{
    public ErroSolicitacaoCartaoException(String message){
        super(message);
    }
}
