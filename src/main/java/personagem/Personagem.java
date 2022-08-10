package personagem;

import mapa.Mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Personagem {
    // atributos basicos
    private int vida;
    private final int ataque;
    private final int defesa;
    private final int velocidade;
    private final Equipe equipe;

    //atributos para calculos
    private final int raioDaMovimentacao;
    private final int vidaTotal;
    private int defesaComBonus;
    private int turnosDefesaExtra;
    private int posicaoAtual;
    private int posicaoAnterior;
    private final boolean ignoraObstaculos;

    protected Personagem(int vida, int ataque, int defesa, int velocidade, Equipe equipe, int raioDaMovimentacao, boolean ignoraObstaculos) {
        this.vida = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.velocidade = velocidade;
        this.equipe = equipe;
        this.raioDaMovimentacao = raioDaMovimentacao;
        this.ignoraObstaculos = ignoraObstaculos;

        this.vidaTotal = vida;
        this.defesaComBonus = defesa;
        this.turnosDefesaExtra = 0;
        this.posicaoAnterior = 0;
    }

    public abstract void atacar(Mapa mapa, int direcao);
    public abstract void defender();
    public void curar() {}
    public void curar(Mapa mapa, int uso) {}

    public void mover(Mapa mapa) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> posicoesPossiveis = mapa.mostrarMovimentacaoPossivel(this.raioDaMovimentacao, this.posicaoAtual, this.ignoraObstaculos);
        posicoesPossiveis.add(0);
        int posicao;
        boolean posicaoValida;

         do {
            System.out.println("Escolha a posicao para mover o seu personagem\nEscolha 0 caso não queira se mover");
            posicao = scanner.nextInt();
            posicaoValida = posicoesPossiveis.contains(posicao);

            if (!posicaoValida) System.out.println("Posicao invalida. Escolha uma das posicoes apresentadas");
        } while (!posicaoValida);

        if (posicao != 0){
            try {
                mapa.moverPersonagem(this, posicao - 1, this.ignoraObstaculos);
            } catch (RuntimeException posicaoIndisponivel){
                System.out.println("Algo deu errado. Por favor, tente novamente");
                mover(mapa);
            }
        }
    }

    protected List<Personagem> filtrarPersonagens(List<Personagem> personagens, int filtro){
        List<Personagem> personagensAliados = new ArrayList<>();
        List<Personagem> personagensInimigos = new ArrayList<>();

        for (Personagem personagem : personagens){
            if (this.getEquipe() == personagem.getEquipe()) personagensAliados.add(personagem);
            else personagensInimigos.add(personagem);
        }

        if (filtro == 1) return personagensAliados; // 1 => filtro para personagens aliados
        return personagensInimigos; // 2 => filtro para personagens inimigos
    }

    protected void calculaTurnosDeDefesaExtra(String nome) {
        this.turnosDefesaExtra--;

        if (this.turnosDefesaExtra == 0) {
            this.defesaComBonus = this.defesa;
            System.out.println("A defesa bonus do personagem " + nome + " acabou");
        }
    }

    public void receberDano(Mapa mapa, int dano, String nome) {
        int danoRecebido = (int) Math.round(dano * ((100.0 - this.defesaComBonus) / 100));
        setVida(Math.max(this.vida - danoRecebido, 0));
        System.out.println("O personagem " + nome + " recebeu " + danoRecebido + " de dano. Sua vida agora e " + this.vida);

        if (this.vida <= 0) {
            System.out.println("O personagem " + nome + " morreu");
            mapa.removerPersonagem(this);
        }
    }

    public void aumentarVida(double porcentagem, String nome) {
        int vidaMaxima = this.vidaTotal;
        int vidaAtual = this.vida;
        int vidaAposCura = Math.min((vidaAtual + (int) Math.round(vidaMaxima * porcentagem)), vidaMaxima);
        this.setVida(vidaAposCura);

        int cura = vidaAposCura - vidaAtual;
        System.out.println("O personagem " + nome + " foi curado em " +  cura + ". Sua vida agora e " + vidaAposCura);
    }

    public String toString() {
        if (this.getEquipe() == Equipe.EQUIPE1) return "1";
        return "2";
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getVelocidade() {
        return velocidade;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setDefesaComBonus(int defesaComBonus) {
        this.defesaComBonus = defesaComBonus;
    }

    public void setTurnosDefesaExtra(int turnosDefesaExtra) {
        this.turnosDefesaExtra = turnosDefesaExtra;
    }

    public int getPosicaoAtual() {
        return posicaoAtual;
    }

    public void setPosicaoAtual(int posicao) {
        this.posicaoAnterior = this.posicaoAtual;
        this.posicaoAtual = posicao;
    }

    public int getPosicaoAnterior() {
        return posicaoAnterior;
    }

    public boolean getIgnoraObstaculos() {
        return ignoraObstaculos;
    }
}
