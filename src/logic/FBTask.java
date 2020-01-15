package logic;

//import java.io.File;

public class FBTask {
	//private File file;
	private String pathname;
	private String name;
	private TaskType type;
	
	public FBTask(String path, String n, TaskType t) {
		setPathname(path);
		setName(n);
		setType(t);
	}
	
	public FBTask(String path, String n){
		this(path, n, TaskType.SAME_SIZE);
	}
	
	public FBTask(){
		this(null, null, TaskType.SAME_SIZE);
	}
	
//	public FBTask(File f, TaskType t) {
//		setFile(f);
//		setType(t);
//	}
//	
//	public FBTask(File f){
//		this(f, TaskType.SAME_SIZE);
//	}
//	
//	public FBTask(){
//		this(null, TaskType.SAME_SIZE);
//	}
	
//	public File getFile() {
//		return file;
//	}
//	public void setFile(File file) {
//		this.file = file;
//	}
	public TaskType getType() {
		return type;
	}
	
	public String getTypeDescription() {
		switch(type) {
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
	
	public void setType(TaskType type) {
		this.type = type;
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
