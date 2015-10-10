package fs.sicarius.update;

import java.util.List;

import com.google.gson.Gson;

public class Deploy {
	
	private List<Env> environments;
	private String logfile;
	private Integer timeout;
	private String watchText;
	private String rollbackText;
	private String local;
	private String remote;
	private String file;
	private List<String> pre;
	private List<String> post;
	
	public Deploy(List<Env> environments, String logfile, Integer timeout, String watchText, String rollbackText,
			String local, String remote, String file, List<String> pre, List<String> post) {
		super();
		this.environments = environments;
		this.logfile = logfile;
		this.timeout = timeout;
		this.watchText = watchText;
		this.rollbackText = rollbackText;
		this.local = local;
		this.remote = remote;
		this.file = file;
		this.pre = pre;
		this.post = post;
	}

	public List<Env> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<Env> environments) {
		this.environments = environments;
	}

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getRemote() {
		return remote;
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public List<String> getPre() {
		return pre;
	}

	public void setPre(List<String> pre) {
		this.pre = pre;
	}

	public List<String> getPost() {
		return post;
	}

	public void setPost(List<String> post) {
		this.post = post;
	}

	public String getWatchText() {
		return watchText;
	}

	public void setWatchText(String watchText) {
		this.watchText = watchText;
	}

	public String getRollbackText() {
		return rollbackText;
	}

	public void setRollbackText(String rollbackText) {
		this.rollbackText = rollbackText;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
