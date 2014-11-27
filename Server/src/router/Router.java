package router;


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

}
