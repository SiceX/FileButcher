/**
 * 
 */
package logic;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author nicola.ferrari
 *
 */
public final class Butcher {
	
	private static ConcurrentLinkedQueue<FBTask> tasks;
	
	public static void executeOrder66(ArrayList<FBTask> data) {
		tasks = new ConcurrentLinkedQueue<FBTask>(data);
	}
	
}
