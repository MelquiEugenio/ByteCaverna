package jogoDosAnimais;

import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import javax.swing.JOptionPane;

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
}
