package melquieugenio.jogodosanimais;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {

	Jogador jogador = new Jogador(this);
	No raiz = new Animal("Macaco");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Thread executavel = new Thread(new Runnable(){ public void run(){
			
			while(true){
				jogador.considere("Pense em um animal.");
				raiz = raiz.aprendeCom(jogador);
			}
		}});

		executavel.start();
	}
}
