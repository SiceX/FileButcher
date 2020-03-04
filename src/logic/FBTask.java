package logic;

//import java.io.File;

public abstract class FBTask {
	//private File file;
	private String pathname;
	private String name;
	private TaskMode mode;
	
	public FBTask(String path, String name, TaskMode tMode) {
		setPathname(path);
		setName(name);
		setMode(tMode);
	}
	
	public FBTask(String path, String name){
		this(path, name, TaskMode.SAME_SIZE);
	}
	
	public FBTask(){
		this(null, null, TaskMode.SAME_SIZE);
	}
	
	public TaskMode getMode() {
		return mode;
	}
	
	public String getModeDescription() {
		switch(mode) {
		case SAME_SIZE:
			return "Divisione in parti di dimensioni uguali";
		case CRYPT_SAME_SIZE:
			return "Divisione in parti di dimensioni uguali con crittografia";
		case ZIP_CUSTOM_SIZE:
			return "Divisione e compresione in parti di dimensione specificata";
		case CUSTOM_NUMBER:
			return "Divisione in N parti";
		default:
			throw new NullPointerException();
		}
	}
	
	public void setMode(TaskMode type) {
		this.mode = type;
	}

	public String getPathname() {
		return pathname;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSpecs() {
		return null;
	}
}
