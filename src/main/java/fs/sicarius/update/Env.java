package fs.sicarius.update;

import com.google.gson.Gson;

public class Env {
	private String address;
	private Integer port;
	private String user;
	private String pass;
	
	public Env(String address, Integer port, String user, String pass) {
		super();
		this.address = address;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
