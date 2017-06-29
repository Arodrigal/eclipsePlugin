package com.executeMaven.handlers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.executeMaven.Activator;
import com.executeMaven.bean.BuildParameters;
import com.executeMaven.bean.Configuration;
import com.executeMaven.bean.Configuration.Environment;
import com.executeMaven.bean.Configuration.Properties;
import com.executeMaven.bean.Configuration.Property;
import com.executeMaven.display.RequestParamProject;
import com.executeMaven.launch.LaunchMavenConfiguration;

/**
 * Our class handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExecutorMain extends AbstractHandler {

	public final static String MODULE = "MODULE";
	public final static String TYPE = "TYPE";
	public final static String ENVIRONMENT = "ENVIRONMENT";

	public final static String ENV_DESARROLLO = "des";
	public final static String ENV_INTEGRACION = "int";
	public final static String ENV_TEST = "test";

	private final static String TYPE_BATCH = "BATCH";
	private final static String TYPE_ONLINE = "ONLINE";
	private final static String TYPE_WS = "WS";
	private final static String TYPE_DEFAULT = "DEFAULT";

	ILog log = null;

	Shell shell;
	Map<String, String> searchParamLauncher = null;
	Configuration configFile = null;

	public ExecutorMain(){
		if(Activator.getDefault() != null)
			log = Activator.getDefault().getLog();
			
	}
	
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Initial variables
		BuildParameters parameters = null;
		shell = HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell();
		searchParamLauncher = new HashMap<String, String>();

		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Inicio de ejecucion del plugin"));

		if (configFile == null) {
			try {
				configFile = fileToBean();
			} catch (Exception e) {
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error en la lectura del fichero de configuracion", e));
				displayError("Error in configuration file", e);
				return null;
			}
		}

		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Cargada la información del fichero de configuracion."));

		// Obtener el proyecto que sobre el que se va a ejecutar
		String nameProject = nameProject();
		Boolean continueProcess = false;

		if (isEmpty(nameProject)) {
			continueProcess = false;
		} else {

			fillParameters(nameProject);

			RequestParamProject dialog = new RequestParamProject(shell, searchParamLauncher);
			if (dialog.open() == Window.OK) {
				continueProcess = true;
			}
		}

		if (continueProcess) {
			parameters = getConfigMaven();

			if (parameters == null)
				continueProcess = false;
		}

		if (continueProcess) {
			LaunchMavenConfiguration launch = new LaunchMavenConfiguration();

			try {
				launch.executeLauncher(parameters);
			} catch (Exception e) {
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error en la ejecucion", e));
			}
		}

		System.out.println("End execution.");
		return null;
	}

	private BuildParameters getConfigMaven() {
		BuildParameters parameters = null;

		try {

			parameters = getConfigToExecute(searchParamLauncher.get(MODULE), searchParamLauncher.get(TYPE),
					searchParamLauncher.get(ENVIRONMENT));

		} catch (Exception e) {
			log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error obteniendo la parametrización Maven. ", e));
			displayError("Error obteniendo la parametrización Maven", e);
			return null;
		}

		return parameters;
	}

	private BuildParameters getConfigToExecute(String module, String type, String environment) {
		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				"Obtener parametrizacion de (" + module + "," + type + "," + environment + ")."));
		BuildParameters parameters = new BuildParameters();

		Environment env = configFile.getEnvParameter(module, type, environment);

		if (env == null) {
			// Not found configuration.
			log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error en la ejecucion, No se ha encontrado la configuración para los siguientes parámetros. \nMODULE:"
							+ module + " \nTYPE:" + type + " \nENV:" + environment + " "));
			return null;
		} else {
			if (env.getProperty() != null) {
				for (Property prop : env.getProperty()) {
					parameters.addAtribute(prop.getName(), prop.getValue());
				}
			}
			if (env.getProperties() != null) {
				for (Properties prop : env.getProperties()) {
					parameters.addAtribute(prop.getName(), prop.getValue());
				}
			}

			return parameters;
		}
	}

	public Configuration fileToBean() throws Exception {
		String path = "./dropins/properties/config.xml";
		URL pluginURL = null;

		pluginURL = FileLocator.resolve(new URL(new URL("file:"), path));

		JAXBContext jc = JAXBContext.newInstance(Configuration.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Configuration api = (Configuration) unmarshaller.unmarshal(pluginURL);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		System.out.println("End process of xml to bean");
		return api;
	}

	private void fillParameters(String nameProject) {
		List<String> modulesConfig = configFile.getListModules();

		for (String moduleName : modulesConfig) {
			if (Pattern.compile(Pattern.quote(moduleName), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
				searchParamLauncher.put(MODULE, moduleName);
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "ASIGNACION MODULE " + moduleName + ""));
			}
		}
		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				"Buscando el MODULE " + nameProject + ". Resultado: " + searchParamLauncher.get(MODULE)));

		if (Pattern.compile(Pattern.quote(TYPE_BATCH), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
			searchParamLauncher.put(TYPE, TYPE_BATCH);
		} else if (Pattern.compile(Pattern.quote(TYPE_ONLINE), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
			searchParamLauncher.put(TYPE, TYPE_ONLINE);
		} else if (Pattern.compile(Pattern.quote(TYPE_WS), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
			searchParamLauncher.put(TYPE, TYPE_WS);
		} else {
			searchParamLauncher.put(TYPE, TYPE_DEFAULT);
		}

		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				"Buscando el TYPE " + nameProject + ". Resultado: " + searchParamLauncher.get(TYPE)));

		if (Pattern.compile(Pattern.quote(ENV_TEST), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
			searchParamLauncher.put(ENVIRONMENT, ENV_TEST);
		} else
			if (Pattern.compile(Pattern.quote(ENV_INTEGRACION), Pattern.CASE_INSENSITIVE).matcher(nameProject).find()) {
			searchParamLauncher.put(ENVIRONMENT, ENV_INTEGRACION);
		} else if (Pattern.compile(Pattern.quote(ENV_DESARROLLO), Pattern.CASE_INSENSITIVE).matcher(nameProject)
				.find()) {
			searchParamLauncher.put(ENVIRONMENT, ENV_DESARROLLO);
		}
		log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				"Buscando el ENVIRONMENT " + nameProject + ". Resultado: " + searchParamLauncher.get(ENVIRONMENT)));
	}

	// Method to get name of selected project
	public String nameProject() {
		// !MESSAGE Variable references empty selection: ${project_loc}
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		IDynamicVariable varProject = manager.getDynamicVariable("project_name");

		try {
			return varProject.getValue(null);
		} catch (CoreException e) {
			e.printStackTrace();
			displayError("Error obtained name of selected project", "Have you selected project?" + e.getCause());
		}
		return null;
	}

	public boolean isEmpty(String parameter) {
		return (parameter == null || parameter.isEmpty());
	}

	public void displayError(String title, String message) {
		MessageDialog.openError(shell, title, message);
	}

	public void displayError(String title, Exception e) {
		// MessageDialog.openError(shell, title, message);
		MultiStatus status = createMultiStatus(e.getLocalizedMessage(), e);
		ErrorDialog.openError(shell, "Error", "This is an error", status);
	}

	private static MultiStatus createMultiStatus(String msg, Throwable t) {

		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, stackTrace.toString());
			childStatuses.add(status);
		}

		MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				t.toString(), t);
		return ms;
	}
}
