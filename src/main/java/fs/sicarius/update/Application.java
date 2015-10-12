package fs.sicarius.update;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import fs.sicarius.update.commands.SFTP;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	// create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);
        
    	SpringApplication app = new SpringApplication(Application.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext ctx = app.run(args);
		
		// show commands help
		//displayHelp();
		updateDeploys(ctx);
		
		// main loop
		boolean exit = true;
		while (!exit) {
			System.out.print("> ");
			String command = scanner.next();
			switch (command) {
			case "help": displayHelp(); break;
			case "exit": exit=true; break;
			case "envs": displayEnvs(ctx); break;
			case "deploys": displayDeploys(ctx); break;
			case "update": updateDeploys(ctx); break;
			default: System.out.println("Command '"+command+"' not recognized");
			}
		}
		
		scanner.close();
    }
    
    /**
     * Update every deploy
     * @param ctx
     */
    private static void updateDeploys(ConfigurableApplicationContext ctx) {
		Deliverer deliverer = ctx.getBean(Deliverer.class);
		Deployments deploys = ctx.getBean(Deployments.class);
		
		deploys.getDeployments().values().forEach(deploy -> {
			deliverer.deploy(deploy);
		});
	}

	/**
     * Displays available deploys as json
     * @param ctx
     */
    private static void displayDeploys(ConfigurableApplicationContext ctx) {
		Deployments deploys = ctx.getBean(Deployments.class);
		deploys.getDeployments().entrySet().forEach(dep -> {
			System.out.println(dep.getKey() + " -> " + dep.getValue().toString());
		});
	}

	/**
     * Displays environments and checks connectivity
     * @param bean
     */
    private static void displayEnvs(ApplicationContext ctx) {
    	Environments bean = ctx.getBean(Environments.class);
    	bean.getEnvironments().entrySet().forEach(env -> {
    		SFTP sftp = new SFTP().connect(env.getValue());
    		if (sftp.test()) {
    			System.out.println(env.getKey() + "@" + env.getValue().getAddress() + " -> " + "alive");
    		} else {
    			System.err.println(env.getKey() + "@" + env.getValue().getAddress() + " -> " + "not responding");
    		}
    		sftp.disconnect();
    	});
    }

	/**
     * Display commands help
     */
    private static void displayHelp() {
    	System.out.println("");
		System.out.println("/!\\ Sicarius cluster update /!\\");
		System.out.println("Available options:");
		System.out.println(" help: display this help");
		System.out.println(" envs: displays a list of available environments");
		System.out.println(" deploys: displays a list of available deployments");
		System.out.println(" update: updates every available deployment");
		System.out.println(" exit: exit updater");
		System.out.println("");
    }
}
