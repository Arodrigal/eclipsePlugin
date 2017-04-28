package com.executeMaven.cesce.launch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;

import com.executeMaven.cesce.Activator;
import com.executeMaven.cesce.bean.BuildParameters;

public class LaunchMavenConfiguration {

	@SuppressWarnings("unchecked")
	public void executeLauncher(BuildParameters parameters) throws Exception {
		
		ILaunchConfiguration configuration = getLaunchConfiguration("lauchino");
		ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
		java.util.Map<String, Object> atributos = wc.getAttributes();
		
		for (Map.Entry<String, Object> entry : parameters.getAtributes().entrySet()) {
			Object mapValue = entry.getValue();
			String mapName = entry.getKey();
			if(Boolean.class.isInstance(entry.getValue())){
				wc.setAttribute(entry.getKey(), (Boolean) entry.getValue());
			}else if(Integer.class.isInstance(entry.getValue())){
				wc.setAttribute(entry.getKey(), (int) entry.getValue());
			}else if(mapValue instanceof String){
				wc.setAttribute(mapName, (String) mapValue);
			}else if(entry.getValue() instanceof List){
				wc.setAttribute(entry.getKey(), (List<String>) entry.getValue());
			}else if(entry.getValue() instanceof Map){
				wc.setAttribute(entry.getKey(), (Map<String, String>) entry.getValue());
			}else if(entry.getValue() instanceof Set){
				wc.setAttribute(entry.getKey(), (Set<String>) entry.getValue());
			}
		}	

		//DebugUIPlugin.launchInBackground(configuration, ILaunchManager.DEBUG_MODE);
		DebugUIPlugin.launchInForeground(wc, ILaunchManager.DEBUG_MODE);
		System.out.println("Finalizado procesamiento");
	}

	public org.eclipse.debug.core.ILaunchConfiguration getLaunchConfiguration(String nameConfiguration) throws CoreException{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations();
		ILaunchConfiguration launchConfiguration = null;
		
		if(launchConfigurations != null && nameConfiguration != null){
			for(int i=0; i<launchConfigurations.length; i++){
				if(nameConfiguration.equals(launchConfigurations[i].getName())){
					launchConfiguration = launchConfigurations[i];
				}
			}
		}
	    return launchConfiguration;
	}
}
