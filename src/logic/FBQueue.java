package logic;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class FBQueue extends ConcurrentLinkedQueue<FBTask>{
	
	public FBQueue() {
		super();
	}
	
	public String[] getNameList() {
		int length = super.size();
		Iterator<FBTask> it = super.iterator();
		String fileNames[] = new String[length];
		
		int i=0;
		while(it.hasNext()) {
			fileNames[i] = it.next().getName();
			i++;
		}
		
		return fileNames;
	}
}
