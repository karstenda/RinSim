package project.strategies.delegatemas;

import project.common.controller.AbstractController;
import project.common.model.CommunicationModel;
import project.common.packages.Package;
import project.common.trucks.Truck;
import project.experiments.Experiment;
import project.strategies.delegatemas.colony.PackageAgent;
import project.strategies.delegatemas.colony.PackageDestination;
import project.strategies.delegatemas.colony.TruckAgent;
import project.strategies.delegatemas.colony.renderers.PackageAgentRenderer;
import project.strategies.delegatemas.colony.renderers.TruckAgentRenderer;
import rinde.sim.core.Simulator;
import rinde.sim.event.Event;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;
import rinde.sim.ui.View;

public class DelegateMasController extends AbstractController {

    private CommunicationModel communicationModel;

    public DelegateMasController(Scenario scen, int numberOfTicks, String map) throws ConfigurationException {
	this(null, scen, numberOfTicks, map);
    }

    public DelegateMasController(Experiment experiment, Scenario scen, int numberOfTicks, String map)
	    throws ConfigurationException {
	super(experiment, scen, numberOfTicks, map);
    }

    @Override
    protected Simulator createSimulator() throws Exception {
	Simulator simulator = super.createSimulator();

	communicationModel = new CommunicationModel();
	simulator.register(communicationModel);

	return simulator;
    }

    @Override
    protected boolean createUserInterface() {
	View.startGui(getSimulator(), 3, new PackageAgentRenderer(getSimulator()), new TruckAgentRenderer(
		getSimulator()));
	return true;
    }

    @Override
    protected boolean handleAddTruck(Event e) {
	Truck truck = new Truck(graph.getRandomNode(getSimulator().getRandomGenerator()));
	getSimulator().register(truck);

	TruckAgent agent = new TruckAgent(truck);
	getSimulator().register(agent);

	return true;
    }

    @Override
    protected boolean handleAddPackage(Event e) {
	Package pkg = createPackage();
	getSimulator().register(pkg);

	PackageAgent agent = new PackageAgent(pkg);
	getSimulator().register(agent);

	PackageDestination destination = agent.getDestination();
	getSimulator().register(destination);

	return true;
    }
}
