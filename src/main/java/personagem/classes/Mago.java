package personagem.classes;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.interfaces.MostrarNome;

import java.util.List;

public class Mago extends Personagem implements MostrarNome {
    // Mago: 50 HP, 40 AT, 20 DF, 16 VEL, 1 MOV

    public Mago(Equipe equipe) {
        super(50, 40, 20, 16, equipe, 1, false);
    }

    @Override
    public void atacar(Mapa mapa, int direcao) {
        int raio = 25;
        List<Personagem> personagensProximos = mapa.personagensProximos(raio, super.getPosicaoAtual(), true);
        List<Personagem> inimigosProximos = super.filtrarPersonagens(personagensProximos, 2);
        inimigosProximos.forEach(personagem -> personagem.receberDano(mapa, super.getAtaque(), personagem.toString()));
    }

    @Override
    public void defender() {
        super.setDefesaComBonus((int) Math.round(super.getDefesa() * 1.8));
        super.setTurnosDefesaExtra(1);
    }

    @Override
    public void curar() {
        double cura = 0.3;
        super.aumentarVida(cura, toString());
    }

    @Override
    public void receberDano(Mapa mapa, int dano, String nome) {
        super.receberDano(mapa, dano, nome);
        calculaTurnosDeDefesaExtra(nome);
    }

    @Override
    public String toString() {
        return "M" + super.toString();
    }
}
