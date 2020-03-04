package logic;

public class FBTaskCustomNumber extends FBTask {
	
	private final String fileExtension = ".parn";
	private int numberOfParts;

	public FBTaskCustomNumber(String path, String name, int nParts){
		super(path, name, TaskMode.CUSTOM_NUMBER);
		numberOfParts = nParts;
	}
	
	//Default
	public FBTaskCustomNumber(String path, String name){
		this(path, name, 2);
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	@Override
	public String getSpecs() {
		return numberOfParts + " parti";
	}
}
