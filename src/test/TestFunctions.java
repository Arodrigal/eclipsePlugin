package test;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.executeMaven.bean.Configuration;
import com.executeMaven.bean.Configuration.*;

/**
 * Test functions
 * 
 * @author agustin.rodriguez
 *
 */
public class TestFunctions {

	public static void main(String[] args) {

		//testApiSteam();
		//@NotNull String[] arr;

	}

	private static void testApiSteam() {
		List<Module> modules = buildObject();
		String mod = "REASEGURO", type = "BATCH", envir = "test";
		
		if(modules != null){
			Optional<Module> moduleRet = modules.stream()
				.filter(s -> mod.equalsIgnoreCase(s.getName()))
				.findFirst();
			
			if(moduleRet.isPresent()){
				Optional<Type> typeRet = moduleRet.get().getTypes().stream()
						.filter(s -> type.equalsIgnoreCase(s.getName()))
						.findFirst();
				
				if(typeRet.isPresent()){
					Optional<Environment> envRet = typeRet.get().getEnvironments().stream()
							.filter(s -> envir.equalsIgnoreCase(s.getName()))
							.findFirst();
					
					if(envRet.isPresent()){					
						System.out.println("FOUND:" + envRet.get());
						
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
	}

	private static List<Module> buildObject() {
		try {
			String path = "properties/config.xml";
			java.io.File f = new java.io.File(path);

			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			@SuppressWarnings("deprecation")
			Configuration api = (Configuration) unmarshaller.unmarshal(f.toURL());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			//marshaller.marshal(api, System.out);
			System.out.println("End process of xml to bean");
			
			return api.getModules();
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage() + "\nLocalization::" + e.getLocalizedMessage());
		} 

		return null;
	}

}
