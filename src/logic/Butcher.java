/**
 * 
 */
package logic;

import java.util.ArrayList;

/**
 * @author nicola.ferrari
 *
 */
public final class Butcher {
	
	public static void executeOrder66(ArrayList<FBTask> tasks) {
		for(int i=0; i<tasks.size(); i++) {
			tasks.get(i).start();
		}
	}
	
}
