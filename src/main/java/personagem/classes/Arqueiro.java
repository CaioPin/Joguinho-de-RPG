package personagem.classes;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.interfaces.MostrarNome;

import java.util.List;

public class Arqueiro extends Personagem implements MostrarNome {
    // Arqueiro: 120 HP, 22 AT, 26 DF, 25 VEL, 3 MOV

    public Arqueiro(Equipe equipe) {
        super(120, 22, 26, 25, equipe, 3, false);
    }

    private boolean atacarNasDirecoesBasicas(int direcao, int posicaoAtual, int linhaAtual,
                                            int posicaoPersonagem, int linhaPersonagem, int variacaoLinha, int i) {

                 // se estiver na mesma coluna (implicito)
        return ((direcao == 1 && posicaoPersonagem == posicaoAtual - variacaoLinha) || // norte
                (direcao == 2 && posicaoPersonagem == posicaoAtual + variacaoLinha) || // sul
                (linhaAtual == linhaPersonagem && // se estiver na mesma linha
                 (direcao == 3 && posicaoPersonagem == posicaoAtual - i) || // oeste
                 (direcao == 4 && posicaoPersonagem == posicaoAtual + i))); // leste
    }

    private boolean atacarEmDiagonal(int direcao, int posicaoAtual, int linhaAtual, int posicaoPersonagem,
                                 int linhaPersonagem, int variacaoLinha, int i) {

        return ((linhaPersonagem == linhaAtual - i && // se estiver ao norte
                 ((direcao == 5 && posicaoPersonagem == (posicaoAtual - i - variacaoLinha)) || // noroeste
                  (direcao == 6 && posicaoPersonagem == (posicaoAtual + i - variacaoLinha)))) || // nordeste
                (linhaPersonagem == linhaAtual + i && // se estiver ao sul
                 ((direcao == 7 && posicaoPersonagem == (posicaoAtual - i + variacaoLinha)) || // sudoeste
                  (direcao == 8 && posicaoPersonagem == (posicaoAtual + i + variacaoLinha))))); // sudeste
    }

    @Override
    public void atacar(Mapa mapa, int direcao) {
        int raio = 3;
        int largura = mapa.getLargura();
        int posicaoAtual = super.getPosicaoAtual();
        int linhaAtual = posicaoAtual / largura;
        boolean noAlcance;
        int i;

        List<Personagem> personagensProximos = mapa.personagensProximos(raio, super.getPosicaoAtual(), false);
        List<Personagem> inimigosProximos = super.filtrarPersonagens(personagensProximos, 2);

        for (Personagem personagem : inimigosProximos){
            int posicaoPersonagem = personagem.getPosicaoAtual();
            int linhaPersonagem = posicaoPersonagem / largura;

            for (i = 1; i <= raio; i++){
                int variacaoLinha = i * largura;


                if (direcao <= 4){ // casos primarios
                    noAlcance = atacarNasDirecoesBasicas(direcao, posicaoAtual, linhaAtual,
                                                         posicaoPersonagem, linhaPersonagem, variacaoLinha, i);
                } else { // casos de diagonais
                    noAlcance = atacarEmDiagonal(direcao, posicaoAtual, linhaAtual,
                                                 posicaoPersonagem, linhaPersonagem, variacaoLinha, i);
                }


                if (noAlcance) {
                    personagem.receberDano(mapa, (int) (super.getAtaque() * (1 + (0.2 * i))), personagem.toString());
                }
            }
        }
    }

    @Override
    public void defender() {
        super.setDefesaComBonus((int) Math.round(super.getDefesa() * 1.2));
        super.setTurnosDefesaExtra(3);
    }

    @Override
    public void curar() {
        double cura = 0.1;
        super.aumentarVida(cura, toString());
    }

    @Override
    public void receberDano(Mapa mapa, int dano, String nome) {
        super.receberDano(mapa, dano, nome);
        calculaTurnosDeDefesaExtra(nome);
    }

    @Override
    public String toString() {
        return "A" + super.toString();
    }
}
