package fs.sicarius.update;

import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SFTP {
	private JSch jsch;
	private ChannelSftp sftp;
	private Session session;

	public SFTP connect(Env env) {
		try {
			jsch = new JSch();
			session = jsch.getSession(env.getUser(), env.getAddress(), env.getPort());
			session.setPassword(env.getPass());
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.connect();
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

		} catch (JSchException e) {}
		return this;
	}

	public SFTP disconnect() {
		try {
			if (sftp!=null)
				this.sftp.disconnect();
			this.jsch = null;
			this.session.disconnect();
		} catch (Exception e) {}
		return this;
	}

	public boolean test() {
		try {
			this.sftp.pwd();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Método que crea un fichero en el sftp
	 * @return boolean
	 * @exception IOException
	 */
	public boolean upload(String remoteFile, String localFile){
		try {
			this.sftp.put(localFile, remoteFile);
			return true;
		} catch (Exception e) {
			System.err.println("Error uploading file to server");
		}
		return false;
	}
}
