package eugenio.jogodosanimais;

import android.R;
import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {
	
	//Jogador jogador = new Jogador();
	//No raiz = new Animal("Macaco");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Thread executable = new Thread(new Runnable(){ public void run(){
			
			/*while(true) {
				jogador.considere("Pense em um animal.");
				raiz = raiz.aprendeCom(jogador);
			}*/
			
		}});
		
		executable.run();
	}
}
