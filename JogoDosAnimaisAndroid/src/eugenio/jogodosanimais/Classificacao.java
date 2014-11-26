package eugenio.jogodosanimais;

public class Classificacao extends No {
	
	public No ladoNao, ladoSim;
	
	String caracteristica;
	
	public Classificacao(String caracteristica, No ladoSim, No ladoNao){
		this.caracteristica = caracteristica;
		this.ladoSim = ladoSim;
		this.ladoNao = ladoNao;
	}

	@Override
	public No aprendeCom(Jogador jogador) {
		if (jogador.responda("O animal que vc pensou " + caracteristica + "?"))
			ladoSim = ladoSim.aprendeCom(jogador);
		else
			ladoNao = ladoNao.aprendeCom(jogador);
		
		return this;
	}
}
