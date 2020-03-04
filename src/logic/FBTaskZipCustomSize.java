package logic;
import java.util.ArrayList;

public class FBTaskZipCustomSize extends FBTask {
	
	private final String fileExtension = ".zipar";
	private ArrayList<Integer> partSpecs;
	
	public FBTaskZipCustomSize(String path, String name, ArrayList<Integer> specs){
		super(path, name, TaskMode.ZIP_CUSTOM_SIZE);
		partSpecs = specs;
	}
	
	//Default
	@SuppressWarnings("serial")
	public FBTaskZipCustomSize(String path, String name){
		this(path, name, new ArrayList<Integer>() {});
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	@Override
	public String getSpecs() {
		return partSpecs.size() + " parti";
		
	}

}
