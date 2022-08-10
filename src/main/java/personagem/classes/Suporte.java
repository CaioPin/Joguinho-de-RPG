package personagem.classes;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.interfaces.MostrarNome;

import java.util.List;

public class Suporte extends Personagem implements MostrarNome {
    // Suporte: 105 HP, 16 AT, 29 DF, 21 VEL, 3 MOV (ignora obstaculos)
    private double porcentagemCura;

    public Suporte(Equipe equipe) {
        super(105, 16, 29, 21, equipe, 3, true);
        this.porcentagemCura = 0.25;
    }

    @Override
    public void mover(Mapa mapa) {
        super.mover(mapa);
        calculaTurnosDeDefesaExtra(toString());
    }

    @Override
    public void atacar(Mapa mapa, int direcao) {
        int raio = 1;
        int posicaoAtual = super.getPosicaoAtual();
        int largura = mapa.getLargura();

        List<Personagem> personagensProximos = mapa.personagensProximos(raio, posicaoAtual, false);
        List<Personagem> inimigosProximos = super.filtrarPersonagens(personagensProximos, 2);

        /* Calcula todos os personagens em linhas (1) ou colunas (2) diferentes */
        for (Personagem personagem : inimigosProximos){
            int posicaoPersonagem = personagem.getPosicaoAtual();

            if ((direcao == 1 && posicaoAtual / largura != posicaoPersonagem / largura) || // linhas diferentes
                (direcao == 2 && posicaoAtual % largura != posicaoPersonagem % largura)) // colunas diferentes
            { personagem.receberDano(mapa, super.getAtaque(), personagem.toString()); }
        }

        calculaCura(false);
    }

    @Override
    public void defender() {
        super.setDefesaComBonus((int) Math.round(super.getDefesa() * 1.2));
        super.setTurnosDefesaExtra(2);
        calculaCura(false);
    }

    @Override
    public void curar(Mapa mapa, int uso) {
        if (uso == 1){ // curar o proprio personagem
            double cura = 0.3;
            super.aumentarVida(cura, toString());
        } else { // curar os aliados proximos
            int raio = 2;
            List<Personagem> personagensProximos = mapa.personagensProximos(raio, super.getPosicaoAtual(), false);
            List<Personagem> aliadosProximos = super.filtrarPersonagens(personagensProximos, 1);

            aliadosProximos.forEach(personagem -> personagem.aumentarVida(this.porcentagemCura, personagem.toString()));

            calculaCura(true);
        }
    }

    @Override
    public String toString() {
        return "S" + super.toString();
    }

    private void calculaCura(boolean curouAliadosNoTurnoAtual) {
        if (curouAliadosNoTurnoAtual && this.porcentagemCura > 0.1) this.porcentagemCura -= 0.03;
        else if (!curouAliadosNoTurnoAtual && this.porcentagemCura < 0.25) this.porcentagemCura += 0.03;
    }
}
