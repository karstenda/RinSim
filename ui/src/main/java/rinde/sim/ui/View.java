/**
 * 
 */
package rinde.sim.ui;

import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import rinde.sim.core.Simulator;
import rinde.sim.ui.renderers.Renderer;
import rinde.sim.ui.utils.Sleak;

/**
 * @author Rinde van Lon (rinde.vanlon@cs.kuleuven.be)
 * @author Bartosz Michalik <bartosz.michalik@cs.kuleuven.be>
 * @since 2.0
 */
public class View {

	protected static boolean testingMode = false;

	private View() {};

	/**
	 * Define the SWT handles tracing mode. Disabled by default
	 * @param testingMode
	 */
	public static void setTestingMode(boolean testingMode) {
		View.testingMode = testingMode;
	}

	public static void startGui(final Simulator simulator, final int speedup, Renderer... renderers) {
		Display.setAppName("RinSim");
		final Display display;
		if (testingMode) {
			DeviceData data = new DeviceData();
			data.tracking = true;
			display = new Display(data);
			Sleak sleak = new Sleak();
			sleak.open();
		} else {
			display = new Display();
		}

		final Shell shell = new Shell(display);
		shell.setText("RinSim - Simulator");
		shell.setSize(new org.eclipse.swt.graphics.Point(800, 800));

		// simulator viewer is run in here
		new SimulationViewer(shell, simulator, speedup, renderers);

		shell.setMaximized(true);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (shell.isDisposed()) {
			simulator.stop();
		}
	}
}
