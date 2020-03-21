package logic;

public class FBTaskCustomNumber extends FBTask {
	
	private int numberOfParts;

	public FBTaskCustomNumber(String path, String name, long fileSize, int nParts){
		super(path, name, TaskMode.CUSTOM_NUMBER, fileSize);
		numberOfParts = nParts;
	}
	
	//Default
	public FBTaskCustomNumber(String path, String name, long fileSize){
		this(path, name, fileSize, 2);
	}
	
	@Override
	public void run() {
		//TODO
	}

	@Override
	public String getParameters() {
		return Integer.toString(numberOfParts);
	}
	
	@Override
	public void setParameters(Object param) {
		if(param.getClass() == Integer.class) {
			numberOfParts = (int)param;
		}
	}
}
