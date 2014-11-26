package eugenio.jogodosanimais;

import android.app.AlertDialog;

public class Jogador {

	public void considere(String mensagem){	
		showMessageDialog(null, mensagem);
	}
	
	public boolean responda(String pergunta) {
		int result = showConfirmDialog(null, pergunta, "Responda", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (result == JOptionPane.CLOSED_OPTION) System.exit(0);
		return result == YES_OPTION;
	}
	
	public String insira(String pergunta){
		String result = showInputDialog(pergunta);
		if (result == null) System.exit(0);
		return result;
	}
	
	public void alert(String msg, CharSequence[] options, AlertDialog.Builder AlertDialog){
		
		runOnUi()
		
	}
	
	public CharSequence[] options(CharSequence... options){
		return options;
	}
}
