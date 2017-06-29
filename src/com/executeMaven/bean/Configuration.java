package com.executeMaven.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Structure of configuration xml
 * 
 * @author agustin.rodriguez
 *
 */
@XmlRootElement(name="Configuration")
public class Configuration {
    
    List<Module> modules = null;
    List<String> listModules = null;
    
	@XmlElement( name = "Module" )
	public void setModules( List<Module> modules ){
        this.modules = modules;
    }
	
	public List<Module> getModules(){
        return modules;
    }

	public List<String> getListModules(){
		if(listModules == null){
			listModules = new ArrayList<String>();
			for(Module mod : modules){
				listModules.add(mod.name);
			}
		}
		
		return listModules;
	}

	public Environment getEnvParameter(String moduleParam, String typeParam, String environmentParam) {
		Environment found = null;
		
		// API Stream of Java 8 
		if(modules != null){
			Optional<Module> moduleRet = modules.stream()
				.filter(s -> moduleParam.equalsIgnoreCase(s.getName()))
				.findFirst();
			
			if(moduleRet.isPresent()){
				Optional<Type> typeRet = moduleRet.get().getTypes().stream()
						.filter(s -> typeParam.equalsIgnoreCase(s.getName()))
						.findFirst();
				
				if(typeRet.isPresent()){
					Optional<Environment> envRet = typeRet.get().getEnvironments().stream()
							.filter(s -> environmentParam.equalsIgnoreCase(s.getName()))
							.findFirst();
					
					if(envRet.isPresent()){					
						found = envRet.get();
						
					}else{
						System.out.println("Environment NOT FOUND");
					}
				}else{
					System.out.println("Type NOT FOUND");
				}
			}else{
				System.out.println("Module NOT FOUND");
			}
		}

		return found;
	}

	public static class Module {
		
		@XmlElement(name="ParentID")
	    private String parentID;
		
	    @XmlAttribute
	    protected String name;
	    
		List<Type> types;
		List<String> listTypes = null;
				
		public String getName() {
			return name;
		}

		@XmlElement( name = "Type" )
		public void setTypes( List<Type> types ){
	        this.types = types;
	    }
		
		public List<Type> getTypes(){
	        return types;
	    }
	
		public List<String> getListTypes(){
			if(listTypes == null){
				listTypes = new ArrayList<String>();
				for(Type type : types){
					listTypes.add(type.name);
				}
			}
			
			return listTypes;
		}
	}
	
	public static class Type {
		
		@XmlElement(name="ParentID")
	    private String parentID;
		
	    @XmlAttribute
	    protected String name;
	    
		List<Environment> environments;
		List<String> listEnv = null;

		public String getName() {
			return name;
		}
		
		@XmlElement( name = "Environment" )
		public void setEnvironments( List<Environment> environments ){
	        this.environments = environments;
	    }
		
		public List<Environment> getEnvironments(){
	        return environments;
	    }
	
		public List<String> getListEnv(){
			if(listEnv == null){
				listEnv = new ArrayList<String>();
				for(Environment env : environments){
					listEnv.add(env.name);
				}
			}
			
			return listEnv;
		}
	}
	
	public static class Environment {
		
		@XmlElement(name="ParentID")
	    private String parentID;
		
	    @XmlAttribute
	    protected String name;
	    
		List<Properties> properties;
		List<Property> property;

		public String getName() {
			return name;
		}
		
		@XmlElement( name = "PropertyList" )
		public void setProperties( List<Properties> properties ){
	        this.properties = properties;
	    }
		
		public List<Properties> getProperties(){
	        return properties;
	    }
		
		@XmlElement( name = "Property" )
		public void setProperty( List<Property> property ){
	        this.property = property;
	    }
		
		public List<Property> getProperty(){
	        return property;
	    }
	}
	
	public static class Properties {
		
		@XmlAttribute
	    protected String name;
		
		List<String> property;
		
		@XmlElement( name = "Property" )
		public void setProperty( List<String> property ){
	        this.property = property;
	    }
		
		public List<String> getProperty(){
	        return property;
	    }
	    
	    public String getName(){
	    	return name;
	    }
	    
	    public Object getValue(){
	    	return property;
	    }
	}
	
	public static class Property {
		
	    @XmlAttribute
	    protected String name;
	    
	    @XmlAttribute
	    protected String value;
	    
	    public String getName(){
	    	return name;
	    }
	    
	    public String getValue(){
	    	return value;
	    }
	}
}