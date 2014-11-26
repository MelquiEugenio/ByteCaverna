package sorting;

public class BooleanSort {
		
	private boolean[] booleanList;

	public void ordene(int[] lista){
		
		int maiorNum = 0;
		
		for(int i = lista.length - 1;  i >= 0; i--)
			if(maiorNum < lista[i])
				maiorNum = lista[i];
		
		booleanList = new boolean[maiorNum + 1];
		
		for(int i = lista.length - 1;  i >= 0; i--)
			booleanList[lista[i]] = true;
		
		int j = lista.length - 1;
		
		for(int i = maiorNum;  i >= 0; i--)
			if (booleanList[i])
				lista[j--] = i;
	}
}