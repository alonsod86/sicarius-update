package fs.sicarius.update.commands;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import fs.sicarius.update.Env;

public class Shell {
	private JSch jsch;
	private Session session;
	private ChannelExec channel;

	/**
	 * Creates an active connection to the target environment
	 * @param env
	 * @return
	 */
	public Shell connect(Env env) {
		try {
			jsch = new JSch();
			session = jsch.getSession(env.getUser(), env.getAddress(), env.getPort());
			session.setPassword(env.getPass());
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.connect();
		} catch (Exception e) {e.printStackTrace();}
		return this;
	}

	/**
	 * Opens a channel for command execution
	 * @param command
	 * @return
	 */
	public String execute(String command) {
		String output = "";
		try {
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setPty(true);
			channel.connect();
			final InputStream input=channel.getInputStream();
			final Reader reader=new InputStreamReader(input);
			final BufferedReader buffered=new BufferedReader(reader);
			while (true) {
				final String line=buffered.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
				output.concat(line);
			}
			input.close();
			int count=50;
			final int delay=100;
			while (true) {
				if (channel.isClosed()) {
					break;
				}
				if (count-- < 0) {
					break;
				}
				Thread.sleep(delay);
			}
		} catch (Exception e) {e.printStackTrace();}
		channel.disconnect();
		return output;
	}

	/**
	 * Disconnects from the session
	 * @return
	 */
	public Shell disconnect() {
		try {
			this.jsch = null;
			this.session.disconnect();
		} catch (Exception e) {}
		return this;
	}
}
