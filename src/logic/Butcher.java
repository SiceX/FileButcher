package logic;

import java.util.ArrayList;

import logic.tasks.FBTask;


/**
 * Chiama in ordine gli FBTask e li assegna ognuno ad un Thread
 * @author nicola.ferrari
 */
public final class Butcher {

	/**
	 * Chiama in ordine gli FBTask e li assegna ognuno ad un Thread
	 * @param tasks l'ArrayList di FBTask passata dalla tabella
	 */
	public static void executeOrder66(ArrayList<FBTask> tasks) {
		for(int i=0; i<tasks.size(); i++) {
			Thread t = new Thread(tasks.get(i));
			t.start();
		}
	}
	
}
