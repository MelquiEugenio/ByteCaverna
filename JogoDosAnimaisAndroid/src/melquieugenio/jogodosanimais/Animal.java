/*package melquieugenio.jogodosanimais;

public class Animal extends No {
	
	String nome;
	
	public Animal(String nome){
		this.nome = nome;
	}
	
	public No aprendeCom(Jogador jogador) {
		
		if(jogador.responda("O animal que vc pensou é o(a) " + nome + "?")) {
			jogador.considere("Acertei de novo!");
			return this;
		} else {
			String novoNome = jogador.insira("Em qual animal pensastes?");
			String caracteristica = jogador.insira("O(a) " + novoNome + " ______," + " mas o " + nome + " não.");
			Animal novoAnimal = new Animal(novoNome);
			return new Classificacao(caracteristica, novoAnimal, this);
		}
			
	}

}*/
