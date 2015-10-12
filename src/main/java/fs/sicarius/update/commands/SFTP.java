package fs.sicarius.update.commands;

import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

import fs.sicarius.update.Env;

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
	 * SFTP file Upload
	 * @return boolean
	 * @exception IOException
	 */
	public boolean upload(String remoteFile, String localFile){
		try {
			this.sftp.put(localFile, remoteFile, new SftpMonitor());
			return true;
		} catch (Exception e) {
			System.err.println("Error uploading file to server");
		}
		return false;
	}

	/**
	 * Transfer monitor for sftp file uploads/downloads
	 * @author alonso
	 *
	 */
	class SftpMonitor implements SftpProgressMonitor {
		private double count;
		private double max;
		private String src;
		private int percent;
		private int lastDisplayedPercent; 

		SftpMonitor() {
			count = 0;
			max = 0;
			percent = 0;
			lastDisplayedPercent = 0;
		}

		public void init(int op, String src, String dest, long max) {
			this.max = max;
			this.src = src;
			count = 0;
			percent = 0;
			lastDisplayedPercent = 0;
			status();
		}
		
		public boolean count(long count) {
			this.count += count;
			percent = (int) ((this.count / max) * 100.0);
			status();
			return true;
		}
		
		public void end() {
			percent = (int) ((count / max) * 100.0);
			status();
		}
		
		private void status() {
			if (lastDisplayedPercent <= percent - 10) {
				System.out.println(src + ": " + percent + "% " + ((long) count) + "/" + ((long) max));
				lastDisplayedPercent = percent;
			}
		}
	}
}
