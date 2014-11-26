package melquieugenio.jogodosanimais;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Jogador extends Activity {
	
	Activity activity;
	
	public Jogador(Activity activity){
		this.activity = activity;
	}

	public void considere(String mensagem){
		
		alert(mensagem, options("Ok"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				acorda();
		}});
		
		dorme();
	}
	
	/*public boolean responda(String pergunta) {
		int result = showConfirmDialog(null, pergunta, "Responda", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		return result == YES_OPTION;
	}
	
	public String insira(String pergunta){
		String result = showInputDialog(pergunta);
		if (result == null) System.exit(0);
		return result;
	}*/
	
	protected void alert(final String title, final CharSequence[] options, final DialogInterface.OnClickListener OnClickListener){
		
		runOnUiThread(new Runnable(){ public void run(){
			new AlertDialog.Builder(activity)
			.setTitle(title)
			.setItems(options, OnClickListener)
			.show();
		}});
	}
	
	private CharSequence[] options(CharSequence... options){
		return options;
	}
	
	synchronized
	private void dorme(){
		try {
			wait();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	synchronized
	private void acorda(){
			notify();
	}
}

