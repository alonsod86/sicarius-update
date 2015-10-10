package fs.sicarius.update;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
@Component("Environments")
public class Environments {
	private Pattern pattern=null;
	private Matcher matcher=null;
	
	@Autowired
	private Environment env;

	private HashMap<String, Env> environments;

	@PostConstruct
	public void initialize() throws Exception {
		pattern=Pattern.compile("\\$\\{([a-zA-Z]+(\\.[a-zA-Z]+)*)\\}");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		String path = getClass().getClassLoader().getResource("environments.xml").getFile();
		if (env.containsProperty("environments.path") && !env.getProperty("environments.path").isEmpty()) {
			path = env.getProperty("environments.path") + "/environments.xml";
		}
		File fXmlFile = new File(path);
		Document doc = dBuilder.parse(fXmlFile);
		environments = new HashMap<>();

		// Get all environments
		NodeList nList = doc.getElementsByTagName("env");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element nNode = (Element) nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String id = eElement.getAttribute("id");
				String address = resolveVariables(nNode.getElementsByTagName("address").item(0).getTextContent());
				String port = resolveVariables(nNode.getElementsByTagName("port").item(0).getTextContent());
				String user = resolveVariables(nNode.getElementsByTagName("user").item(0).getTextContent());
				String pass = resolveVariables(nNode.getElementsByTagName("pass").item(0).getTextContent());
				Env env = new Env(address, new Integer(port), user, pass);
				environments.put(id, env);
			}
		}
	}
	
	/**
	 * Replaces ${env.variables} in the given text using the environment properties
	 * @param text
	 * @return
	 */
	private String resolveVariables(String text) {
		matcher=pattern.matcher(text);
		if (matcher.find()) {
			// get the property
			String replace = env.getProperty(matcher.group(1));
			return text.replace(matcher.group(0), replace);
		}
		return text;
	}
	
	public Env getEnvironment(String id) {
		return environments.get(id);
	}
	
	public HashMap<String, Env> getEnvironments() {
		return environments;
	}
}
