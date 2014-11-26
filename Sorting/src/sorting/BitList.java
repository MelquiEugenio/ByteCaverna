package sorting;

public class BitList {
	
	private char[] charList;
	
	public BitList(int tamanho) {
		charList = new char[tamanho/16 + 1];
	}

	public void set(int position) {
		
		if (isSet(position))
			return;
		
		charList[position/16] += pow2(position%16);
	}

	public boolean isSet(int casa) {
		
		char charNum = charList[casa/16];
		
		for (int i = casa%16; i > 0; i--)
			charNum /= 2;
		
		return charNum%2 == 1;
	}
	
	private char pow2(int expoente) {
		
		char ret = 1;
		
		for(int i = expoente; i > 0; i--)
			ret *= 2;
		
		return ret;
	}
}