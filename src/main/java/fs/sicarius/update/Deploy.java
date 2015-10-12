package fs.sicarius.update;

import java.util.List;

import com.google.gson.Gson;

public class Deploy {
	private String artifact;
	private List<Env> environments;
	private String logfile;
	private String watchText;
	private String rollbackText;
	private String local;
	private String remote;
	private List<String> pre;
	private List<String> post;
	private List<Artifact> build;
	
	public Deploy(String artifact, List<Env> environments, String logfile, String watchText, String rollbackText,
			String local, String remote, List<String> pre, List<String> post, List<Artifact> build) {
		super();
		this.artifact = artifact;
		this.environments = environments;
		this.logfile = logfile;
		this.watchText = watchText;
		this.rollbackText = rollbackText;
		this.local = local;
		this.remote = remote;
		this.pre = pre;
		this.post = post;
		this.build = build;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
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

	public List<Artifact> getBuild() {
		return build;
	}

	public void setBuild(List<Artifact> build) {
		this.build = build;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
