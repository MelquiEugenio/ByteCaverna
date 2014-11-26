package jogoDosAnimais;

public class Main {

	public static void main(String[] args) {
		
		Jogador jogador = new Jogador();
		No raiz = new Animal("Macaco");
				
		while(true) {
			jogador.considere("Pense em um animal.");
			raiz = raiz.aprendeCom(jogador);
		}
	}
}