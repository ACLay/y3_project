package XML;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import router.ManhattenScenario;
import Model.Charger;

public class RegistryLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		loadTest();
	}
	
	public static void loadTest(){
		XMLChargers xmlChargers = null;
		try {
			
			//The p: at the start of the ChargeDevices tags need removing for this to work
			File file = new File("./edited_registry.xml");


			JAXBContext jaxbContext = JAXBContext.newInstance(XMLChargers.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			xmlChargers = (XMLChargers) jaxbUnmarshaller.unmarshal(file);

			ArrayList<Charger> chargers = new ArrayList<Charger>();
			int errors = 0;
			for(XMLcharger xmlC : xmlChargers.chargers){
				try{
					chargers.add(xmlC.makeCharger());
				} catch (Exception e){
					errors++;
				}
			}
			
			System.out.println(chargers.size());
			System.out.println(errors + " error(s)");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveTest(){
		ManhattenScenario ms = new ManhattenScenario(3,3,1,0,0,2,2,null);
		
		XMLChargers chargers = new XMLChargers();
		List<XMLcharger> xmlChargeList = new ArrayList<XMLcharger>();
		for(Charger c : ms.getGraph().getNodes()){
			xmlChargeList.add(new XMLcharger(c));
		}
		chargers.setChargers(xmlChargeList);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLChargers.class);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			File f = new File("./testChargers.xml");
			
			jaxbMarshaller.marshal(chargers,f);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
