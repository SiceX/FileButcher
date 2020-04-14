package logic.tasks;
import java.util.ArrayList;

public class FBTaskZipCustomSize extends FBTask {
	
	private ArrayList<Integer> partSpecs;
	
	public FBTaskZipCustomSize(String path, String name, boolean doRebuild, long fileSize, ArrayList<Integer> specs){
		super(path, name, TaskMode.ZIP_CUSTOM_SIZE, doRebuild, fileSize);
		partSpecs = specs;
	}
	
	//Default
	@SuppressWarnings("serial")
	public FBTaskZipCustomSize(String path, String name, long fileSize){
		this(path, name, false, fileSize, new ArrayList<Integer>() {});
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

}
