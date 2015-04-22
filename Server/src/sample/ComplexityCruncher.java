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
		
		Car ev = new Car("EV", Amount.valueOf(30, SI.KILOMETER), Amount.valueOf(24*60*60,SI.KILO(SI.JOULE)));
		ev.addCompatibleConnector(new Connector(Type.IEC_TYPE_2, Amount.valueOf(Double.MAX_VALUE, SI.WATT), Amount.valueOf(Double.MAX_VALUE, SI.VOLT), Amount.valueOf(Double.MAX_VALUE,SI.AMPERE)));
		
		System.out.println("Grid size,EV states created,EV states stored,EV states explored,EV time(ms),Petrol created,Petrol states stored,Petrol states explored,Petrol time(ms)");
		
		for(int i=2;i<=25;i++){
			for(int j=1; j<=i;j++){
				//for each size of network
				//build the network
				Manhatten2 ms = new Manhatten2(i, j, 1, 0, 0, i-1, j-1, Amount.valueOf(1,SI.KILOMETER), Amount.valueOf(10,SI.KILOMETER), speeds, ev);
				//route it with a normal and electric vehicle
				TimeOnlyRouter petrolRouter = new TimeOnlyRouter(new StateTimeComparator());
				long startTime = System.currentTimeMillis();
				State petrolResult = petrolRouter.route(ms);
				long endTime = System.currentTimeMillis();
				long petrolTime = endTime - startTime;

				Router evRouter = new ListPrunedQueueRouter(new StateTimeComparator());
				startTime = System.currentTimeMillis();
				State evResult = evRouter.route(ms);
				endTime = System.currentTimeMillis();
				long evTime = endTime - startTime;

				//output the data
				// i, ev created, ev stored, ev explored, ev time, p created, p stored, p explored, p time
				System.out.println((i*j) + "," +
						evRouter.getCreated() + "," +
						evRouter.getStored() + "," +
						evRouter.getExplored() + "," +
						evTime + "," +
						petrolRouter.getCreated() + "," +
						petrolRouter.getStored() + "," +
						petrolRouter.getExplored() + "," +
						petrolTime);
				
				System.gc();
				//System.out.println( ((double)evRouter.getStored()) / ((double)petrolRouter.getStored()) );
			}
		}
	}

}
