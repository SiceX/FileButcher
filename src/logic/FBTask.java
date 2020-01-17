package logic;

//import java.io.File;

public class FBTask {
	//private File file;
	private String pathname;
	private String name;
	private TaskMode mode;
	
	public FBTask(String path, String n, TaskMode t) {
		setPathname(path);
		setName(n);
		setMode(t);
	}
	
	public FBTask(String path, String n){
		this(path, n, TaskMode.SAME_SIZE);
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
}
