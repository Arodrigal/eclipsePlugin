package com.executeMaven.display;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import com.executeMaven.handlers.ExecutorMain;

/**
 * Window for request the parameters
 * 
 * @author agustin.rodriguez
 *
 */
public class RequestParamProject extends Dialog {

	Map<String, String> previousData = null;
	Button radioEnvironment1 = null;
	Button radioEnvironment2 = null;
	Button radioEnvironment3 = null;
	Text textMod = null;
	Text textType = null;
	String module = null;
	String type = null;
	String envDefault = null; 
	
	public RequestParamProject(Shell parent, Map<String, String> searchParamLauncher) {
		super(parent);
		previousData = searchParamLauncher;
		
		envDefault = previousData.get(ExecutorMain.ENVIRONMENT);
	}

	protected Control createDialogArea(Composite parent)
	{	
		// create dialog area composite as done by Dialog class
		Composite dlgAreaComposite = (Composite)super.createDialogArea(parent);
		
		// configure GridLayout installed with dlgAreaComposite 
		GridLayout layout = (GridLayout)dlgAreaComposite.getLayout();
		layout.numColumns = 3;
		
		GridData gridData = new GridData();
        gridData.verticalSpan = 2;
		Group groupEnvironment = new Group(dlgAreaComposite, SWT.SHADOW_ETCHED_IN);
		groupEnvironment.setText("Environment");
		groupEnvironment.setLayout(new RowLayout(SWT.VERTICAL));
		radioEnvironment1 = new Button(groupEnvironment, SWT.RADIO);
		radioEnvironment1.setText(ExecutorMain.ENV_TEST);
		radioEnvironment2 = new Button(groupEnvironment, SWT.RADIO);
		radioEnvironment2.setText(ExecutorMain.ENV_INTEGRACION);
		radioEnvironment3 = new Button(groupEnvironment, SWT.RADIO);
		radioEnvironment3.setText(ExecutorMain.ENV_DESARROLLO);
		groupEnvironment.setLayoutData(gridData);
		
		gridData = new GridData();
        Label labelMod = new Label(dlgAreaComposite, SWT.NULL);
        labelMod.setText("Module: ");
        labelMod.setLayoutData(gridData);
        
        textMod = new Text(dlgAreaComposite, SWT.SINGLE | SWT.BORDER);
        if(previousData.get(ExecutorMain.MODULE) != null){
        	textMod.setText(previousData.get(ExecutorMain.MODULE));
        	textMod.setEnabled(false);
        }
        textMod.setLayoutData(gridData);

        Label labelType = new Label(dlgAreaComposite, SWT.NULL);
        labelType.setText("Tipo: ");
        labelType.setLayoutData(gridData);

        textType = new Text(dlgAreaComposite, SWT.SINGLE | SWT.BORDER);
        if(previousData.get(ExecutorMain.TYPE) != null){
        	textType.setText(previousData.get(ExecutorMain.TYPE));
        	textType.setEnabled(false);
        }
        textType.setLayoutData(gridData);
		
		// Define environment by name project
		if(envDefault != null){
			if(envDefault.equals(ExecutorMain.ENV_TEST)) radioEnvironment1.setSelection(true);
			if(envDefault.equals(ExecutorMain.ENV_INTEGRACION)) radioEnvironment2.setSelection(true);
			if(envDefault.equals(ExecutorMain.ENV_DESARROLLO)) radioEnvironment3.setSelection(true);
		}
		
		return dlgAreaComposite;
	}
	
	private void saveInput() {
		
		if(radioEnvironment1.getSelection()){
			previousData.put(ExecutorMain.ENVIRONMENT, ExecutorMain.ENV_TEST);
		}else if(radioEnvironment2.getSelection()){
			previousData.put(ExecutorMain.ENVIRONMENT, ExecutorMain.ENV_INTEGRACION);
		}else if(radioEnvironment3.getSelection()){
			previousData.put(ExecutorMain.ENVIRONMENT, ExecutorMain.ENV_DESARROLLO);
		}
		if(textMod.getTextChars().length > 0)
			module = textMod.getText();
		if(textType.getTextChars().length > 0)
			type = textType.getText();
		
	}
	
	@Override
    protected void okPressed() {
		saveInput();
		super.okPressed();
    }
	
	public static String getText(Shell parent){
		InputDialog dialog = new InputDialog(parent, "Prueba", "inserta ruta", null, null);
		if (dialog.open() == Window.OK)
			return dialog.getValue();
		return null;
	}
}
