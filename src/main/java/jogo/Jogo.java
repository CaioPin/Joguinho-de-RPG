package jogo;

import mapa.Mapa;
import personagem.Equipe;
import personagem.Personagem;
import personagem.classes.*;

import java.util.*;

public class Jogo {
    private static List<Personagem> personagensEquipe1;
    private static List<Personagem> personagensEquipe2;
    private static List<Personagem> ordemDosPersonagens = new ArrayList<>();
    private static Mapa mapa;

    private static List<Personagem> selecionarPersonagens(Equipe equipe) {
        Scanner scanner = new Scanner(System.in);
        Map<Integer, Personagem> listaDePersonagens = new HashMap<>();
        listaDePersonagens.put(1, new Arqueiro(equipe));
        listaDePersonagens.put(2, new Defensor(equipe));
        listaDePersonagens.put(3, new Guerreiro(equipe));
        listaDePersonagens.put(4, new Mago(equipe));
        listaDePersonagens.put(5, new Suporte(equipe));

        int classe;
        List<Integer> classesEmUso = new ArrayList<>();
        List<Personagem> personagens = new ArrayList<>();

        do {
            System.out.println("\nEscolha um personagem para o seu time:");
            System.out.println("1 - Arqueiro\n2 - Defensor\n3 - Guerreiro\n4 - Mago\n5 - Suporte\n0 - Finalizar a selecao");
            classe = scanner.nextInt();

            if ((classe >= 1 && classe <= 5) && !classesEmUso.contains(classe)) {
                classesEmUso.add(classe);
                personagens.add(listaDePersonagens.get(classe));
            } else if (classe == 0){
                if (personagens.isEmpty()) System.out.println("Voce nao pode finalizar a selecao sem escolher um personagem");
            } else {
                System.out.println("Personagem invalido");
            }
        } while (classe != 0 || personagens.isEmpty());

        System.out.println("Selecao finalizada com sucesso");
        return personagens;
    }

    private static List<Integer> turnos(List<Integer> tempos, Personagem personagem, int tempoDecorrido) {
        List<Personagem> personagens = new ArrayList<>(Jogo.ordemDosPersonagens);
        List<Integer> temposAtualizados = new ArrayList<>(tempos);
        int turnoPersonagem = (int) (Math.round(1000.0 / personagem.getVelocidade())) + tempoDecorrido;

        while (temposAtualizados.contains(turnoPersonagem)){
            turnoPersonagem++;
        }

        temposAtualizados.add(turnoPersonagem);
        Collections.sort(temposAtualizados);
        personagens.add(temposAtualizados.indexOf(turnoPersonagem), personagem);

        setOrdemDosPersonagens(personagens);
        return temposAtualizados;
    }

    private static List<Integer> preparacaoParaIniciar() {
        Scanner scanner = new Scanner(System.in);
        List<Integer> tempos = new ArrayList<>();

        System.out.println("Hora de decidir quem participara da batalha\nEquipe 1, voce comeca");
        setPersonagensEquipe1(selecionarPersonagens(Equipe.EQUIPE1));
        System.out.println("\n\nEquipe 2, sua vez");
        setPersonagensEquipe2(selecionarPersonagens(Equipe.EQUIPE2));

        List<Personagem> personagens = new ArrayList<>();
        personagens.addAll(personagensEquipe1);
        personagens.addAll(personagensEquipe2);
        Jogo.mapa = new Mapa(personagens);

        for (Personagem personagem : personagens){
            int posicao;
            List<Integer> possiveisPosicoes = Jogo.mapa.mostrarMovimentacaoPossivel(10, 0, personagem.getIgnoraObstaculos());

            do {
                System.out.println("\nEm qual posicao voce deseja colocar o personagem " + personagem + "?");
                posicao = scanner.nextInt();
            } while (!possiveisPosicoes.contains(posicao));

            Jogo.mapa.posicionarPersonagemNoMapa(personagem, (posicao - 1), personagem.getIgnoraObstaculos());
            tempos = turnos(tempos, personagem, 0);
        }

        return tempos;
    }

    private static List<Integer> atualizarTurnos(List<Integer> tempos) {
        List<Integer> temposAuxiliares = new ArrayList<>(tempos);
        int tempoAtual = temposAuxiliares.get(0);
        temposAuxiliares.remove(0);

        List<Personagem> personagensAuxiliar = new ArrayList<>(Jogo.ordemDosPersonagens);
        Personagem personagemAtual = personagensAuxiliar.get(0);
        personagensAuxiliar.remove(personagemAtual);

        Jogo.setOrdemDosPersonagens(personagensAuxiliar);
        return turnos(temposAuxiliares, personagemAtual, tempoAtual);
    }

    private static void ataques(Personagem personagem) {
        Scanner scanner = new Scanner(System.in);
        int uso = 0;
        boolean usoCorreto;

        if (personagem instanceof Arqueiro){
            do {
                System.out.println("\nEscolha uma direcao para " + personagem + " atacar:");
                System.out.println("1 - norte\n2 - sul\n3 - oeste\n4 - leste");
                System.out.println("5 - noroeste\n6 - nordeste\n7 - sudoeste\n8 - sudeste");
                uso = scanner.nextInt();
                usoCorreto = uso > 0 && uso < 9;

                if (!usoCorreto) System.out.println("Opcao invalida");
            } while (!usoCorreto);
        }

        if (personagem instanceof Suporte){
            do {
                System.out.println("\nEscolha um sentido para " + personagem + " atacar:");
                System.out.println("1 - linhas ao redor\n2 - colunas ao redor");
                uso = scanner.nextInt();
                usoCorreto = uso == 1 || uso == 2;

                if (!usoCorreto) System.out.println("Opcao invalida");
            } while (!usoCorreto);
        }

        personagem.atacar(Jogo.mapa, uso);
    }

    private static void curas(Personagem personagem) {
        Scanner scanner = new Scanner(System.in);

        if (personagem instanceof Suporte){
            int uso;
            boolean usoCorreto;

            do {
                System.out.println("Voce deseja curar este personagem ou seus aliados proximos?\n1 - Suporte\n2 - Aliados");
                uso = scanner.nextInt();
                usoCorreto = uso == 1 || uso == 2;

                if (!usoCorreto) System.out.println("Opcao invalida");
            } while (!usoCorreto);

            personagem.curar(Jogo.mapa, uso);
        } else {
            personagem.curar();
        }
    }

    private static void acaoDoPersonagem(Personagem personagem) {
        Scanner scanner = new Scanner(System.in);
        boolean opcaoValida;

        do {
            System.out.println("O que o personagem deve fazer?");
            System.out.println("1 - Atacar\n2 - Defender\n3 - Curar");
            int opcao = scanner.nextInt();
            opcaoValida = opcao == 1 || opcao == 2 || opcao == 3;

            switch (opcao) {
                case 1 -> Jogo.ataques(personagem);
                case 2 -> personagem.defender();
                case 3 -> Jogo.curas(personagem);
                default -> System.out.println("Opcao invalida");
            }

        } while (!opcaoValida);
    }

    public static List<Integer> removerPersonagem(List<Integer> tempos) {
        List<Integer> temposAuxiliares = new ArrayList<>(tempos);
        temposAuxiliares.remove(0);

        List<Personagem> personagensAuxiliar = new ArrayList<>(Jogo.ordemDosPersonagens);
        personagensAuxiliar.remove(0);

        Jogo.setOrdemDosPersonagens(personagensAuxiliar);
        return temposAuxiliares;
    }

    public static boolean fimDeJogo(List<Personagem> equipe, int numeroEquipe) {
        int personagensVivos = equipe.stream()
                .reduce(0, (quantidade, personagem) -> personagem.getVida() > 0 ? (quantidade + 1) : quantidade, Integer::sum);
        if (personagensVivos == 0){
            System.out.println("Vitoria da equipe " + numeroEquipe);
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        List<Integer> tempos = preparacaoParaIniciar();
        boolean haEquipeMorta = false;
        Jogo.mapa.mostrarMapa();

        while (!haEquipeMorta){
            Personagem personagemAtual = Jogo.ordemDosPersonagens.get(0);

            if (personagemAtual.getVida() == 0) {
                tempos = Jogo.removerPersonagem(tempos);
            } else {
                System.out.println("\n\nVez de " + personagemAtual);
                personagemAtual.mover(Jogo.mapa);
                Jogo.mapa.mostrarMapa();
                Jogo.acaoDoPersonagem(personagemAtual);

                tempos = atualizarTurnos(tempos);
                haEquipeMorta = Jogo.fimDeJogo(Jogo.personagensEquipe1, 2) ||
                               Jogo.fimDeJogo(Jogo.personagensEquipe2, 1);
            }
        }
    }

    private static void setPersonagensEquipe1(List<Personagem> personagens){
        Jogo.personagensEquipe1 = personagens;
    }

    private static void setPersonagensEquipe2(List<Personagem> personagens){
        Jogo.personagensEquipe2 = personagens;
    }

    private static void setOrdemDosPersonagens(List<Personagem> ordem) {
        Jogo.ordemDosPersonagens = ordem;
    }
}
