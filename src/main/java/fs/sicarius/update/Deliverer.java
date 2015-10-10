package fs.sicarius.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("Deployments")
public class Deliverer {

	public void deploy(Deploy deploy) {
		List<Env> envs = deploy.getEnvironments();
		envs.forEach(env -> {
			boolean execution = true;
			System.out.println("\nDeploying on [" + env.getAddress() + "] artifact " + deploy.getFile());
			SFTP sftp = new SFTP().connect(env);
			// execute previous operations
			List<String> pre = deploy.getPre();
			for (String cmd : pre) {
				execution &= executeCommand(cmd, null, deploy.getTimeout());
			}
			// deploy if previous ops executed ok
			if (execution) {
				execution &= sftp.upload(deploy.getRemote(), deploy.getLocal());
			}
			// execute post operations
			if (execution) {
				List<String> post = deploy.getPost();
				for (String cmd : post) {
					execution &= executeCommand(cmd, null, deploy.getTimeout());
				}
			}
			sftp.disconnect();
		});
	}

	/**
	 * Executes a shell command
	 * @param cmd
	 * @param watchText
	 * @param timeout
	 * @return
	 */
	private boolean executeCommand(String cmd, String watchText, Integer timeout) {
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
