package logic;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import logic.tasks.FBTask;

@SuppressWarnings("serial")
@Deprecated
public class FBQueue extends ConcurrentLinkedQueue<FBTask>{
	
	public String data[][];
	
	public FBQueue() {
		super();
	}
	
	public String[][] getData() {
		int length = super.size();
		Iterator<FBTask> it = super.iterator();
		String data[][] = new String[length][2];
		
		int i=0;
		while(it.hasNext()) {
			data[i][0] = it.next().getFileName();
			data[i][1] = it.next().getModeDescription();
			i++;
		}
		
		return data;
	}
}
