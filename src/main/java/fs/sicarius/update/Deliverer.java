package fs.sicarius.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("Deployments")
public class Deliverer {

	/**
	 * Deploys an artifact to the required environments
	 * @param deploy
	 */
	public void deploy(Deploy deploy) {
		List<Env> envs = deploy.getEnvironments();
		envs.forEach(env -> {
			boolean execution = true;
			System.out.println("\n--- Deploying on [" + env.getAddress() + "] artifact " + deploy.getArtifact() + " ---");
			SFTP sftp = new SFTP().connect(env);
			// execute previous operations
			List<String> pre = deploy.getPre();
			for (String cmd : pre) {
				System.out.println("$ " + cmd);
				execution &= executeCommand(cmd, null, null, deploy.getTimeout());
			}
			// deploy if previous ops executed ok
			if (execution) {
				System.out.println("$ Uploading file");
				execution &= sftp.upload(deploy.getRemote(), deploy.getLocal());
			}
			// execute post operations
			if (execution) {
				List<String> post = deploy.getPost();
				for (String cmd : post) {
					System.out.println("$ " + cmd);
					execution &= executeCommand(cmd, null, null, deploy.getTimeout());
				}
			}
			// execute watch and rollback monitors
			if (deploy.getWatchText()!=null) {
				execution &= executeCommand(deploy.getLogfile(), deploy.getWatchText(), deploy.getRollbackText(), deploy.getTimeout());
			}
			// print status and wait for confirmation
			if (execution) {
				System.out.println("Artifact deployed successfuly");
			} else {
				System.err.println("Error delpoying artifact "+deploy.getArtifact());
			}
			sftp.disconnect();
			System.out.println("\n--- Press ENTER to continue ---");
			try {
	            System.in.read();
	        } catch(Exception e) {}  
		});
	}

	/**
	 * Executes a shell command
	 * @param cmd
	 * @param watchText
	 * @param timeout
	 * @return
	 */
	private boolean executeCommand(String cmd, String watchText, String rollbackText, Integer timeout) {
		boolean executed = false;
		Process p;

		// if no watch text is defined, the only reason this can fail is because of a timeout
		if (watchText==null) {
			executed = true;
		}
		
		long start = System.currentTimeMillis();
		try {
			p = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine())!= null) {
				System.out.println(line);
				// watch string found
				if (watchText!=null && line.contains(watchText)) {
					executed = true;
					break;
				} else if (rollbackText!=null && line.contains(rollbackText)) {
					executed = false;
					break;
				}
				// timeout control
				if (timeout!=null && (System.currentTimeMillis() - start) / 1000 >= timeout) { 
					System.out.println("Timeout for " + cmd);
					executed = false;
					break;
				}
			}
			// kill command
			p.destroy();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return executed;
	}
}
