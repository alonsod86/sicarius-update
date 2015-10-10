package fs.sicarius.update;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses and stores information on available environments for deployment
 * @author alonso
 *
 */
@Component("Deployments")
@DependsOn("Environments")
public class Deployments {
	private Pattern pattern=null;
	private Matcher matcher=null;
	
	@Autowired
	private Environment env;

	@Autowired
	private Environments environments;
	
	private HashMap<String, Deploy> deployments;

	@PostConstruct
	public void initialize() throws Exception {
		pattern=Pattern.compile("\\$\\{([a-zA-Z]+(\\.[a-zA-Z]+)*)\\}");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		String path = getClass().getClassLoader().getResource("deployments.xml").getFile();
		if (env.containsProperty("deployments.path") && !env.getProperty("deployments.path").isEmpty()) {
			path = env.getProperty("deployments.path") + "/deployments.xml";
		}
		File fXmlFile = new File(path);
		Document doc = dBuilder.parse(fXmlFile);

		deployments = new HashMap<>();

		// Get all environments
		NodeList nList = doc.getElementsByTagName("deploy");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element nNode = (Element) nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String id = eElement.getAttribute("id");
				
				// fetch environments
				Element pParam = (Element) eElement.getElementsByTagName("environments").item(0);
				List<Env> envs = new ArrayList<>();
				NodeList lstParams = pParam.getElementsByTagName("env");
				for (int index=0; index<lstParams.getLength(); index++) {
					String envId = lstParams.item(index).getTextContent();
					envs.add(environments.getEnvironment(envId));
				}
				
				// fetch safety
				String watchText = null;
				String rollbackText = null;
				String logfile = null;
				Integer timeout = null;
				if (nNode.getElementsByTagName("watch").getLength()>0) {
					Element update = (Element) eElement.getElementsByTagName("update").item(0);
					Element watch = (Element) eElement.getElementsByTagName("watch").item(0);
					Element rollback = (Element) eElement.getElementsByTagName("rollback").item(0);
					logfile = resolveVariables(update.getAttribute("log"));
					if (update.hasAttribute("timeout")) {
						timeout = new Integer(resolveVariables(update.getAttribute("timeout")));
					}
					watchText = resolveVariables(watch.getTextContent());
					rollbackText = resolveVariables(rollback.getTextContent());
				}
				
				// fetch pre
				pParam = (Element) eElement.getElementsByTagName("pre").item(0);
				List<String> pre = new ArrayList<>();
				lstParams = pParam.getElementsByTagName("cmd");
				for (int index=0; index<lstParams.getLength(); index++) {
					String value = lstParams.item(index).getTextContent();
					pre.add(resolveVariables(value));
				}
				
				// fetch post
				pParam = (Element) eElement.getElementsByTagName("post").item(0);
				List<String> post = new ArrayList<>();
				lstParams = pParam.getElementsByTagName("cmd");
				for (int index=0; index<lstParams.getLength(); index++) {
					String value = lstParams.item(index).getTextContent();
					post.add(resolveVariables(value));
				}
				
				String local = resolveVariables(nNode.getElementsByTagName("local").item(0).getTextContent());
				String remote = resolveVariables(nNode.getElementsByTagName("remote").item(0).getTextContent());
				String file = resolveVariables(nNode.getElementsByTagName("filter").item(0).getTextContent());
				deployments.put(id, new Deploy(envs, logfile, timeout, watchText, rollbackText, local, remote, file, pre, post));
			}
		}
	}
	
	/**
	 * Replaces ${env.variables} in the given text using the environment properties
	 * @param text
	 * @return
	 */
	private String resolveVariables(String text) {
		if (text==null) return null;
		matcher=pattern.matcher(text);
		if (matcher.find()) {
			// get the property
			String replace = env.getProperty(matcher.group(1));
			return text.replace(matcher.group(0), replace);
		}
		return text;
	}
	
	public HashMap<String, Deploy> getDeployments() {
		return deployments;
	}
}
