import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;

//job to walk to surface from bank.
//will only be activated once we have banked.. shouldn't run anywhere stupid.
public class WalkToSurface extends Node {

	public static Tile[] pathToSurface;

	@Override
	public boolean activate() {
		if (Main.state.equals("walking to surface")) {
			pathToSurface = generatePath();
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		System.out.println("walking to surface");
		Walking.newTilePath(pathToSurface).traverse();
		Task.sleep(1000, 2000);
		if (Calculations.distanceTo(Main.surface) < 10)
			Main.state = "mining surface";
	}

	// Generates a path chosen from certain tiles, path taken from walktoBank,
	public Tile[] generatePath() {
		Tile[] path = new Tile[7];
		int i = 0;
		Tile first = chooseRandom(2851, 2853, 2954, 2956);
		path[i++] = first;
		Tile second = chooseRandom(2850, 2853, 2958, 2960);
		path[i++] = second;
		Tile third = chooseRandom(2843, 2845, 2966, 2968);
		path[i++] = third;
		Tile fourth = chooseRandom(2832, 2835, 2968, 2968);
		path[i++] = fourth;
		Tile fifth = chooseRandom(2829, 2832, 2979, 2977);
		path[i++] = fifth;
		Tile sixth = chooseRandom(2826, 2829, 2986, 2989);
		path[i++] = sixth;
		Tile last = chooseRandom(2827, 2830, 2994, 2997);
		path[i++] = last;
		// possibly make first have more options.
		return path;
	}

	public Tile chooseRandom(int xMin, int xMax, int yMin, int yMax) {
		return new Tile(Random.nextInt(xMin, xMax + 1), Random.nextInt(yMin,
				yMax + 1), 0);
	}

	public static Tile[] generatePathCheck() {
		Tile[] path = new Tile[7];
		int i = 0;
		Tile first = chooseRandomCheck(2851, 2853, 2954, 2956);
		path[i++] = first;
		Tile second = chooseRandomCheck(2850, 2853, 2958, 2960);
		path[i++] = second;
		Tile third = chooseRandomCheck(2843, 2845, 2966, 2968);
		path[i++] = third;
		Tile fourth = chooseRandomCheck(2832, 2835, 2968, 2968);
		path[i++] = fourth;
		Tile fifth = chooseRandomCheck(2829, 2832, 2979, 2977);
		path[i++] = fifth;
		Tile sixth = chooseRandomCheck(2826, 2829, 2986, 2989);
		path[i++] = sixth;
		Tile last = chooseRandomCheck(2827, 2830, 2994, 2997);
		path[i++] = last;
		// possibly make first have more options.
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
					Walking.walk(checkPath[i]);
					Task.sleep(2500, 5000);
				}
			}
		}
		if (nearPath) {
			Main.getState(); // call this method to change the
			return true; // state if we are at surface
		}
		return false;
	}

}
