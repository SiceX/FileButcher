package logic.tasks;
import java.util.ArrayList;

public class FBTaskButcherZipCustomSize extends FBTask {
	
	private ArrayList<Integer> partSpecs;
	
	public FBTaskButcherZipCustomSize(String path, String name, long fileSize, ArrayList<Integer> specs){
		super(path, name, TaskMode.BUTCHER_ZIP_CUSTOM_SIZE, fileSize);
		partSpecs = specs;
	}
	
	//Default
	@SuppressWarnings("serial")
	public FBTaskButcherZipCustomSize(String path, String name, long fileSize){
		this(path, name, fileSize, new ArrayList<Integer>() {});
	}
	
	@Override
	public void run() {
		//TODO
	}

	@Override
	public String getParameters() {
		return partSpecs.size() + " parti";
	}
	
	@Override
	public void setParameters(Object param) {
		//TODO
	}

	@Override
	public double getProcessedPercentage() {
		double progress = ((double)getProcessed() / getFileSize()) * 100;
		return progress;
	}

}
