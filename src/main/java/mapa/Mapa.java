package mapa;

import mapa.excessoes.PosicaoInvalidaException;
import personagem.Personagem;

import java.security.SecureRandom;
import java.util.*;

public class Mapa {
    private int altura;
    private int largura;
    private final int tamanho;
    private final List<Personagem> personagens;
    private final Map<Integer, Personagem> posicoesPersonagens;
    private final List<Integer> obstaculos = new ArrayList<>();

    public Mapa(List<Personagem> personagens) {
        this.personagens = personagens;
        int quantidadePersonagens = personagens.size();
        posicoesPersonagens = new HashMap<>(quantidadePersonagens);
        SecureRandom aleatorio = new SecureRandom();

        /* selecao de um tamanho aleatorio para o mapa, que varia do dobro da quantidade de personagens (n * 2)
           ate o quadrado do dobro da quantidade de personagens ((n * 2) * (n * 2)), sem ultrapassar 10x10 */
        int quantidadeMaxima = Math.min((quantidadePersonagens * 2 + 1), 11);
        int quantidadeMinima = (int) Math.ceil(quantidadePersonagens / 2.0) + 1;

        do {
            this.altura = aleatorio.nextInt(quantidadeMaxima);
            this.largura = aleatorio.nextInt(quantidadeMaxima);
        } while (altura < quantidadeMinima || largura < quantidadeMinima);

        this.tamanho = this.altura * this.largura;

        /* adiciona obstaculos ao mapa */
        adicionarObstaculos();
    }

    private void adicionarObstaculos() {
        SecureRandom aleatorio = new SecureRandom();
        int quantidade = (int) Math.round((0.25 * this.tamanho) * (0.15 * ((double) this.tamanho / this.personagens.size())));
        int i;

        for (i = 0; i < quantidade; i++){
            int posicao = aleatorio.nextInt(this.tamanho);

            if (posicaoOcupada(posicao) == 0) this.obstaculos.add(posicao);
            else i--;
        }
    }

    public void posicionarPersonagemNoMapa(Personagem personagem, int posicao, boolean ignorar) throws PosicaoInvalidaException {
        // checa se a posicao ja esta ocupada por um outro personagem ou por um obstaculo
        if ((this.posicoesPersonagens.containsKey(posicao) && this.posicoesPersonagens.get(posicao) != personagem) ||
            (this.obstaculos.contains(posicao) && !ignorar)) // ignorar -> permitir posicionamento onde ha um obstaculo
        { throw new PosicaoInvalidaException(); }

        this.posicoesPersonagens.put(posicao, personagem);
        personagem.setPosicaoAtual(posicao);
    }

    public void moverPersonagem(Personagem personagem, int posicao, boolean ignorar) throws PosicaoInvalidaException {
        posicionarPersonagemNoMapa(personagem, posicao, ignorar);
        this.posicoesPersonagens.remove(personagem.getPosicaoAnterior());
    }

    public void removerPersonagem(Personagem personagem) {
        this.personagens.remove(personagem);
        this.posicoesPersonagens.remove(personagem.getPosicaoAtual());
    }

    public List<Personagem> personagensProximos(int raio, int posicaoAtual, boolean emPorcentagem) {
        List<Personagem> personagensProximos = new ArrayList<>();
        int linhaAtual = posicaoAtual / this.largura;
        int colunaAtual = posicaoAtual % this.largura;
        int alcanceX = raio;
        int alcanceY = raio;

        if (emPorcentagem){
            // aproximacoes para 25% da largura e da altura do mapa, sendo no minimo 1 e no maximo 3
            alcanceX = Math.max(((int) Math.round(this.largura * (raio / 100.0))), 1);
            alcanceY = Math.max(((int) Math.round(this.altura * (raio / 100.0))), 1);
        }

        for (int posicao : this.posicoesPersonagens.keySet()){
            /* se estiver a ate Y linhas e X colunas de distancia do personagem, esta no alcance do raio
               a divisao calcula a linha e o resto da divisao calcula a coluna */
            if (Math.abs(linhaAtual - (posicao / this.largura)) <= alcanceY &&
                Math.abs(colunaAtual - (posicao % this.largura)) <= alcanceX)
            { personagensProximos.add(this.posicoesPersonagens.get(posicao)); }
        }
        personagensProximos.remove(this.posicoesPersonagens.get(posicaoAtual));

        return personagensProximos;
    }

    public void mostrarMapa() {
        int indicePosicao = 0;
        int i;

        // pega apenas a posicao dos personagens e dos obstaculos e armazena em uma lista
        List<Integer> posicoes = new ArrayList<>(this.posicoesPersonagens.keySet());
        posicoes.addAll(this.obstaculos);
        // ordena a lista em ordem crescente e remove valores repetidos
        Collections.sort(posicoes);
        posicoes = posicoes.stream().distinct().toList();

        int quantidadePosicoes = posicoes.size();
        int posicaoAuxiliar = posicoes.get(indicePosicao);

        for (i = 0; i < this.tamanho; i++){
            if (i % this.largura == 0) System.out.print("\n|");

            String mensagem = "    |"; // caso nao haja nada na posicao

            if (i == posicaoAuxiliar){
                mensagem = " XX |"; // caso haja um obstaculo na posicao

                if (this.posicoesPersonagens.containsKey(posicaoAuxiliar)) { // caso haja um personagem na posicao
                    mensagem = " " + this.posicoesPersonagens.get(posicaoAuxiliar) + " |";
                }

                indicePosicao++;
                if (indicePosicao < quantidadePosicoes) posicaoAuxiliar = posicoes.get(indicePosicao);
            }

            System.out.print(mensagem);
        }

        System.out.println("");
    }

    private int posicaoOcupada(int posicao) {
        /*
         * 0 representa que a posição está livre
         * 1 representa que há um personagem naquela posição
         * 2 representa que há um obstáculo naquela posição
        */

        for (int chave : this.posicoesPersonagens.keySet()){
            if (posicao == chave) return 1;
        }

        for (int obstaculo : this.obstaculos) {
            if (obstaculo == posicao) return 2;
        }

        return 0;
    }

    public List<Integer> mostrarMovimentacaoPossivel(int raio, int posicaoAtual, boolean ignora) {
        List<Integer> posicoesPermitidas = new ArrayList<>();
        int linhaAtual = posicaoAtual / this.largura;
        int colunaAtual = posicaoAtual % this.largura;
        int i;

        for (i = 0; i < this.tamanho; i++){
            if (i % this.largura == 0) System.out.print("\n|");

            // se estiver a ate "raio" linhas e "raio" colunas de distancia do personagem, esta no alcance do raio
            if (Math.abs(linhaAtual - (i / this.largura)) <= raio &&
                Math.abs(colunaAtual - (i % this.largura)) <= raio){

                int restricao = posicaoOcupada(i);

                if (restricao == 0 || (restricao == 2 && ignora)){ // se for possivel se movimentar para a posicao
                    posicoesPermitidas.add(i + 1);
                    System.out.print(" " + (i < 9 ? "0" : "") + (i + 1) + " |");
                } else {
                    System.out.print("    |");
                }
            } else {
                System.out.print("    |");
            }
        }

        System.out.println("");
        return posicoesPermitidas;
    }

    public int getLargura() {
        return this.largura;
    }
}
