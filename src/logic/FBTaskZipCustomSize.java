package logic;
import java.util.ArrayList;

public class FBTaskZipCustomSize extends FBTask {
	
	private final String fileExtension = ".zipar";
	private ArrayList<Integer> partSpecs;
	
	public FBTaskZipCustomSize(String path, String name, long fileSize, ArrayList<Integer> specs){
		super(path, name, TaskMode.ZIP_CUSTOM_SIZE, fileSize);
		partSpecs = specs;
	}
	
	//Default
	@SuppressWarnings("serial")
	public FBTaskZipCustomSize(String path, String name, long fileSize){
		this(path, name, fileSize, new ArrayList<Integer>() {});
	}
	
	@Override
	public void run() {
		//TODO
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
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
