package mapa.excessoes;

public class PosicaoInvalidaException extends RuntimeException {
    public PosicaoInvalidaException(){
        throw new RuntimeException("A posicao escolhida ja esta ocupada. Por favor, selecione outra posicao.");
    }
}
