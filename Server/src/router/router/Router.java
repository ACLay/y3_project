package router.router;

import router.Scenario;
import router.State;


public abstract class Router {
	
	protected long created;
	protected long stored;
	protected long explored;

	public abstract State route(Scenario scenario);
	
	public long getCreated(){
		return created;
	}
	
	public long getStored(){
		return stored;
	}
	
	public long getExplored(){
		return explored;
	}

	public void printStats(){
		System.out.println("States created:" + getCreated());
		System.out.println("States stored:" + getStored());
		System.out.println("States explored:" + getExplored());
	}
}
