package personagem.classes;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.interfaces.MostrarNome;

import java.util.List;

public class Defensor extends Personagem implements MostrarNome {
    // Defensor: 80 HP, 24 AT, 40 DF, 12 VEL, 1 MOV

    public Defensor(Equipe equipe) {
        super(80, 24, 40, 12, equipe, 1, false);
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
        super.setDefesaComBonus((int) Math.round(super.getDefesa() * 1.4));
        super.setTurnosDefesaExtra(1);
    }

    @Override
    public void curar() {
        double cura = 0.15;
        super.aumentarVida(cura, toString());
    }

    @Override
    public String toString() {
        return "D" + super.toString();
    }
}
