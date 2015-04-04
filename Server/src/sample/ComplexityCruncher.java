package sample;

import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Manhatten2;
import router.State;
import router.comparator.StateTimeComparator;
import router.router.ListPrunedQueueRouter;
import router.router.Router;
import router.router.TimeOnlyRouter;
import Model.Car;
import Model.connectors.Connector;
import Model.connectors.Type;

public class ComplexityCruncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<Amount<Velocity>> speeds = new ArrayList<Amount<Velocity>>();
		speeds.add(Amount.valueOf(30, NonSI.MILES_PER_HOUR));
		speeds.add(Amount.valueOf(40, NonSI.MILES_PER_HOUR));
		speeds.add(Amount.valueOf(50, NonSI.MILES_PER_HOUR));
		speeds.add(Amount.valueOf(60, NonSI.MILES_PER_HOUR));
		
		Car petrol = new Car("petrol car", Amount.valueOf(Double.MAX_VALUE, SI.METER), Amount.valueOf(Double.MAX_VALUE, SI.JOULE));
		Car electric = new Car("EV", Amount.valueOf(30, SI.KILOMETER), Amount.valueOf(24*60*60,SI.KILO(SI.JOULE)));
		electric.addCompatibleConnector(new Connector(Type.IEC_TYPE_2, Amount.valueOf(Double.MAX_VALUE, SI.WATT), Amount.valueOf(Double.MAX_VALUE, SI.VOLT), Amount.valueOf(Double.MAX_VALUE,SI.AMPERE)));
		
		System.out.println("Grid size,EV states created,EV states stored,EV states explored,Petrol created,Petrol states stored,Petrol states explored");
		
		for(int i=20;i<21;i++){
		//for each size of network
			//build the network
			Manhatten2 ms = new Manhatten2(i, i, 0.6, 0, 0, i-1, i-1, Amount.valueOf(1,SI.KILOMETER), Amount.valueOf(40,SI.KILOMETER), speeds, null);
			//route it with a normal and electric vehicle
			/*ms.setCar(petrol);
			TimeOnlyRouter petrolRouter = new TimeOnlyRouter(new StateTimeComparator());
			State petrolResult = petrolRouter.route(ms);*/
			
			ms.setCar(electric);
			Router evRouter = new ListPrunedQueueRouter(new StateTimeComparator());
			State evResult = evRouter.route(ms);
			
			//output the data
			// i, ev created, ev stored, ev explored, p created, p stored, p explored
			System.out.println(i + "," +
					evRouter.getCreated() + "," +
					evRouter.getStored() + "," +
					evRouter.getExplored() + ","/* +
					petrolRouter.getCreated() + "," +
					petrolRouter.getStored() + "," +
					petrolRouter.getExplored()*/);

		}
	}

}
