package personagem.classes;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.interfaces.MostrarNome;

import java.util.List;

public class Guerreiro extends Personagem implements MostrarNome {
    // Guerreiro: 70 HP, 32 AT, 33 DF, 19 VEL, 2 MOV

    public Guerreiro(Equipe equipe) {
        super(70, 32, 33, 19, equipe, 2, false);
    }

    @Override
    public void mover(Mapa mapa) {
        super.mover(mapa);
        calculaTurnosDeDefesaExtra(toString());
    }

    @Override
    public void atacar(Mapa mapa, int direcao) {
        int raio = 1;
        List<Personagem> personagensProximos = mapa.personagensProximos(raio, super.getPosicaoAtual(), false);
        List<Personagem> inimigosProximos = super.filtrarPersonagens(personagensProximos, 2);
        inimigosProximos.forEach(personagem -> personagem.receberDano(mapa, super.getAtaque(), personagem.toString()));
    }

    @Override
    public void defender() {
        super.setDefesaComBonus((int) Math.round(super.getDefesa() * 1.3));
        super.setTurnosDefesaExtra(1);
    }

    @Override
    public void curar() {
        double cura = 0.1;
        super.aumentarVida(cura, toString());
    }

    @Override
    public String toString() {
        return "G" + super.toString();
    }
}
