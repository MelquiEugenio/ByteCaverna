package melquieugenio.jogodosanimais;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

public class Jogador {
	
	Activity activity;
	String resposta;
	int ret;
	
	public Jogador(Activity activity){
		this.activity = activity;
	}

	public void considere(String mensagem){
		
		alert(mensagem, options("OK"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				acorda();
		}});
		
		dorme();
	}
	
	public boolean responda(String pergunta) {
	 
		alert(pergunta, options("Sim", "Não"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ret = which;
				acorda();
		}});
		
		dorme();
		return ret == 1;
	}
	
	public String insira(final String pergunta){
		
		final EditText input = new EditText(activity);
		
		activity.runOnUiThread(new Runnable(){ public void run(){
			
			new AlertDialog.Builder(activity)
			.setTitle(pergunta)
			.setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
            		resposta = input.getText().toString();
            		acorda();
        	}})
			.show();
		}});
		
		dorme();
		return resposta;
	}
	
	public void alert(final String title, final CharSequence[] options, final DialogInterface.OnClickListener OnClickListener){
		
		activity.runOnUiThread(new Runnable(){ public void run(){
			
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

