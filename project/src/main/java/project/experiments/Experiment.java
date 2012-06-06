package project.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import project.common.controller.AbstractController;
import project.common.listeners.Report;
import rinde.sim.scenario.ConfigurationException;
import rinde.sim.scenario.Scenario;

public abstract class Experiment {

    private static final int STATIC_SEED = 123;
    protected List<Report> reports = new ArrayList<Report>();

    protected abstract AbstractController createController(Scenario scenario);

    protected abstract Scenario createScenario();

    protected FileWriter writer;
    private int runs;
    private Random random = new Random(97);
    private String reportFile;
    private String testName = "";

    private Semaphore sem = new Semaphore(1);

    public Experiment(String reportUri) throws IOException {
	this.reportFile = reportUri;

	clearFile(reportUri);
    }

    private void clearFile(String reportUri) throws IOException {
	FileWriter lwriter = new FileWriter(new File(reportUri));
	lwriter.write("");
	lwriter.flush();
	lwriter.close();
    }

    public void receiveReport(Report report) {
	reports.add(report);
	runs--;

	if (runs == 0) {
	    try {
		showResults();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public void showResults() throws IOException {
	writer = new FileWriter(new File(reportFile), true);
	writeTestName();
	writeHeader();
	int id = 0;
	for (Report report : reports) {
	    writeLine(id++, report);
	}
	writeTotalAverage();
	reports.clear();

	writer.flush();
	writer.close();

	sem.release();
    }

    private void writeTotalAverage() throws IOException {
	writer.write("Total Average");
	writer.write(";");

	double avgDeliveries = 0;
	double avgPULateness = 0;
	double avgDLLateness = 0;
	double avgCompTime = 0;
	double avgDistance = 0;
	for (Report report : reports) {
	    avgDeliveries += report.getDeliveredPackages();
	    avgPULateness += report.getAvgPickupLateness();
	    avgDLLateness += report.getAvgDeliveryLateness();
	    avgCompTime += report.getAvgCompletionTime();
	    avgDistance += report.getAvgDistance();
	}
	writer.write(avgDeliveries / reports.size() + "");
	writer.write(";");
	writer.write(avgPULateness / reports.size() + "");
	writer.write(";");
	writer.write(avgDLLateness / reports.size() + "");
	writer.write(";");
	writer.write(avgCompTime / reports.size() + "");
	writer.write(";");
	writer.write(avgDistance / reports.size() + "");
	writer.write("\n");
    }

    private void writeLine(int runId, Report report) throws IOException {
	writer.write((runId + 1) + "");
	writer.write(";");
	writer.write(report.getDeliveredPackages() + "");
	writer.write(";");
	writer.write(report.getAvgPickupLateness() + "");
	writer.write(";");
	writer.write(report.getAvgDeliveryLateness() + "");
	writer.write(";");
	writer.write(report.getAvgCompletionTime() + "");
	writer.write(";");
	writer.write(report.getAvgDistance() + "");
	writer.write("\n");
    }

    private void writeHeader() throws IOException {
	writer.write("Run");
	writer.write(";");
	writer.write("Delivered Packages");
	writer.write(";");
	writer.write("AVG Pickup Lateness");
	writer.write(";");
	writer.write("AVG Delivery Lateness");
	writer.write(";");
	writer.write("AVG Completion Time");
	writer.write(";");
	writer.write("AVG Distance For Delivery");
	writer.write("\n");
    }

    protected void writeTestName() throws IOException {
	if (!testName.trim().equals("")) {
	    writer.write("\n");
	    writer.write(testName);
	    writer.write("\n");
	}
	testName = "";
    }

    public void run(boolean randomSeed, boolean ui) {
	run(0, randomSeed, ui);
    }

    public void run(int runId, boolean randomSeed, boolean ui) {
	AbstractController controller = createController(createScenario());
	preRun(runId);

	int seed = randomSeed ? random.nextInt() : STATIC_SEED;

	try {
	    if (ui) {
		controller.startUi(seed);
	    } else {
		controller.start(seed);
	    }
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	}

	postRun(runId);
    }

    public void runMultiple(int times, boolean randomSeed, boolean ui, String name) {
	if (times < 1) {
	    throw new IllegalArgumentException("You have to run it at least one time.");
	}

	try {
	    sem.acquire();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	this.runs = times;
	this.testName = name;

	for (int run = 0; run < times; run++) {
	    run(randomSeed, ui);
	}
    }

    protected void preRun(int run) {

    }

    protected void postRun(int run) {

    }
}