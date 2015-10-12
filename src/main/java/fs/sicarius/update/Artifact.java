package fs.sicarius.update;

public class Artifact {
	private String id;
	private String path;
	private String command;
	
	public Artifact(String id, String path, String command) {
		super();
		this.id = id;
		this.path = path;
		this.command = command;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
}
