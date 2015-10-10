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

	public SFTP connect(Env env) {
		try {
			jsch = new JSch();
			// Connect to an SFTP server on port 22
			Session session = jsch.getSession(env.getUser(), env.getAddress(), env.getPort());
			session.setPassword(env.getPass());
			// El protocolo SFTP requiere un intercambio de claves
			// al asignarle esta propiedad le decimos que acepte la clave
			// sin pedir confirmación
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.connect();
			// Abrimos el canal de sftp y conectamos
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

		} catch (JSchException e) {}
		return this;
	}

	public SFTP disconnect() {
		if (sftp!=null)
			this.sftp.disconnect();
		this.jsch = null;
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
