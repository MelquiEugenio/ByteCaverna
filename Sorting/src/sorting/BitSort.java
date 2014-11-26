package sorting;

public class BitSort {
		
	private BitList bitList;

	public void ordene(int[] lista){
		
		int maiorNum = 0;
		
		for(int i = lista.length - 1;  i >= 0; i--)
			if(maiorNum < lista[i])
				maiorNum = lista[i];
		
		bitList = new BitList(maiorNum);
		
		for(int i = lista.length - 1;  i >= 0; i--)
			bitList.set(lista[i]);
		
		int j = lista.length - 1;
		
		for(int i = maiorNum; i >= 0; i--)
			if (bitList.isSet(i))
				lista[j--] = i;
	}
}