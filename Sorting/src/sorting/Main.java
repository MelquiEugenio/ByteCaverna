package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

	public static void main(String[] args) throws Exception {
		
//		tamanhoDasVariaveisPrimitivas();
//		BooleanSort booleanSort = new BooleanSort();
		BitSort bitSort = new BitSort();
		
		int qtde = 20000;
		
		while (qtde <= 200000){
			
			ArrayList<Integer> nums = new ArrayList<Integer>(qtde*2);
		
			for(int i = qtde*2 - 1; i >= 0; i--)
				nums.add(i);
	
			Collections.shuffle(nums);
		
			int[] listaOriginal = new int[qtde];
		
			for(int i = qtde - 1; i >= 0; i--)
				listaOriginal[i] = nums.get(i);
					
// Tempo de Processamento

			System.gc();
			System.gc();
			Thread.sleep(3000);

			long inicioDoProcesso = System.currentTimeMillis();
			for (int i = 499; i >= 0; i--) {
				int[] lista = Arrays.copyOf(listaOriginal, listaOriginal.length);
//				booleanSort.ordene(lista);
//				Arrays.sort(lista);
				bitSort.ordene(lista);
			}
			long fimDoProcesso = System.currentTimeMillis() - inicioDoProcesso;
			
			System.out.print("Process: " + fimDoProcesso * 2 / 10 + ", ");
			
// Memória Utilizada	
		
			listaOriginal = null;
			System.gc();
			System.gc();
			Thread.sleep(3000);
			System.out.println("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000);
			
			qtde += 20000;
		}
	}

	/*private static void tamanhoDasVariaveisPrimitivas() {
		//byte
		System.out.println(Byte.MIN_VALUE + " : " + Byte.MAX_VALUE);
		//char
		System.out.println((int)Character.MIN_VALUE + " : " + (int)Character.MAX_VALUE);
		//int
		System.out.println(Integer.MIN_VALUE + " : " + Integer.MAX_VALUE);
		//long
		System.out.println(Long.MIN_VALUE + " : " + Long.MAX_VALUE);
		//float
		System.out.println(Float.MIN_VALUE + " : " + Float.MAX_VALUE);
		//double
		System.out.println(Double.MIN_VALUE + " : " + Double.MAX_VALUE);
	}*/
} 