package project.common.packages;

import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.RoadModel;
import rinde.sim.core.model.RoadUser;

public class Package implements SimulatorUser, RoadUser{
	public final String packageID;
	private Point pickupLocation;
	private DeliveryLocation deliveryLocation;
	private boolean pickedUp;
	private boolean delivered;
	private SimulatorAPI simulator;
	private RoadModel roadModel;

	public Package(String packageID, Point pickupLocation, DeliveryLocation deliveryLocation ) {
		this.packageID = packageID;
		this.pickupLocation = pickupLocation;
		this.deliveryLocation = deliveryLocation;
		this.pickedUp = false;
		this.delivered = false;
	}
	
	public boolean needsPickUp(){
		return !pickedUp;
	}

	public boolean delivered(){
		return delivered;
	}
	
	public void pickup(){
		this.pickedUp = true;
		this.simulator.unregister(this);
	}
	
	public void drop(Point point) {
		this.pickedUp = false;
		this.pickupLocation = point;
		this.simulator.register(this);
	}
	
	public void deliver(){
		this.delivered = true;
		this.simulator.unregister(deliveryLocation);
	}
	
	public String getPackageID(){
		return packageID;
	}
	
	@Override
	public String toString() {
		return packageID;
	}

	public Point getPickupLocation(){
		return pickupLocation;
	}
	
	public Point getDeliveryLocation(){
		return deliveryLocation.getPosition();
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		this.simulator = api;
	}

	@Override
	public void initRoadUser(RoadModel model) {
		this.roadModel = model;
		model.addObjectAt(this, pickupLocation);
	}
	
	public RoadModel getRoadModel() {
		return roadModel;
	}

}
