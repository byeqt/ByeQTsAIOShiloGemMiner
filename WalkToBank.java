import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;

//job to provide if ring of dueling banking is not selected.
public class WalkToBank extends Node {

	public static Tile[] pathToBank;

	@Override
	public boolean activate() {
		if (Main.state.equals("walking to bank")) {
			pathToBank = generatePath();
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		Walking.newTilePath(pathToBank).traverse();
		Task.sleep(1000, 2000); // make it sleep so it doesn't spam click
		if (Calculations.distanceTo(Main.bank) < 4) {
			while (!Bank.isOpen()) {
				Bank.open();
				Task.sleep(100, 250);
			}
			while (Inventory.getCount() > 0) {
				Bank.depositInventory();
				Task.sleep(150, 300);
			}
			while (Bank.isOpen()) {
				Bank.close();
			}
			if (Main.karajamaGloves) { // see if we should teleport
													// to mine
				Main.teleportToMine();

			}
			Main.getState(); // gets the state after finishing
											// banking.

		}
	}

	// Generates a path chosen from certain tiles
	public Tile[] generatePath() {
		Tile[] path = new Tile[7];
		int i = 0;
		Tile first = chooseRandom(2827, 2830, 2994, 2997);
		path[i++] = first;
		Tile second = chooseRandom(2826, 2829, 2986, 2989);
		path[i++] = second;
		Tile third = chooseRandom(2829, 2832, 2979, 2977);
		path[i++] = third;
		Tile fourth = chooseRandom(2832, 2835, 2968, 2968);
		path[i++] = fourth;
		Tile fifth = chooseRandom(2843, 2845, 2966, 2968);
		path[i++] = fifth;
		Tile sixth = chooseRandom(2850, 2853, 2958, 2960);
		path[i++] = sixth;
		Tile last = chooseRandom(2851, 2853, 2954, 2956);
		path[i++] = last;
		return path;
	}

	public Tile chooseRandom(int xMin, int xMax, int yMin, int yMax) {
		return new Tile(Random.nextInt(xMin, xMax + 1), Random.nextInt(yMin,
				yMax + 1), 0);
	}

	public static Tile[] generatePathCheck() {
		Tile[] path = new Tile[7];
		int i = 0;
		Tile first = chooseRandomCheck(2827, 2830, 2994, 2997);
		path[i++] = first;
		Tile second = chooseRandomCheck(2826, 2829, 2986, 2989);
		path[i++] = second;
		Tile third = chooseRandomCheck(2829, 2832, 2979, 2977);
		path[i++] = third;
		Tile fourth = chooseRandomCheck(2832, 2835, 2968, 2968);
		path[i++] = fourth;
		Tile fifth = chooseRandomCheck(2843, 2845, 2966, 2968);
		path[i++] = fifth;
		Tile sixth = chooseRandomCheck(2850, 2853, 2958, 2960);
		path[i++] = sixth;
		Tile last = chooseRandomCheck(2851, 2853, 2954, 2956);
		path[i++] = last;
		return path;
	}

	public static Tile chooseRandomCheck(int xMin, int xMax, int yMin, int yMax) {
		return new Tile(Random.nextInt(xMin, xMax + 1), Random.nextInt(yMin,
				yMax + 1), 0);
	}

	public static boolean checkIfNearPath() {
		Tile[] checkPath = generatePathCheck();
		boolean nearPath = false;
		for (int i = 0; i < checkPath.length; i++) {
			if (Calculations.distanceTo(checkPath[i]) < 25) {
				nearPath = true;
			}
			if (nearPath) {
				while (Calculations.distanceTo(checkPath[i]) > 8) {
					//will keep walking to same location if we are more than 8 away
					Walking.walk(checkPath[i]);
					Task.sleep(2500, 5000);
				}
				// give it time to sleep inbetween walking
			}
		}
		if (nearPath) {
			Main.getState(); // call this method to change the
			return true; // state if we are at surface
		}
		return false;
	}

}
