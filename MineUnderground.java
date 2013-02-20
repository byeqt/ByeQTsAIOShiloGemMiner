import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;

//don't provide this 
public class MineUnderground extends Node implements MessageListener {

	public Tile center;
	public int gemsMined;
	public int numberOfRocks = 8;
	public final int[] LOWER_ROCK_IDS = { 11179, 11181, 11179, 11180 };
	public boolean under = false;

	@Override
	public boolean activate() {
		if (Main.state.equals("mining underground")) {
			under = false;
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		gemsMined = 0;
		while (gemsMined != numberOfRocks) {
			if (Main.inventorySpace()) { // no inventory space left
				// the case that we are underground and need to bank
				if (!(Main.castleWarsBank)
						&& !(Main.wickedHood)) {
					Main.climbUpLadder();
				}
				return;
			}
			if (!under) {
				Main.climbDownLadder(); // will make sure we are
														// downstairs
				under = true;
			}

			while (Camera.getPitch() > 25) { // sets pitch so we can access all
												// gems.
				Camera.setPitch((int) ((Camera.getPitch() - (5 + Math.random() * 8))));
			}
			mineGem();
		}
		Main.climbUpLadder();
		Main.state = "mining surface";

	}

	public void mineGem() {
		if (Players.getLocal().isMoving()) {
			return;
		}
		SceneObject lowerGems = SceneEntities.getNearest(LOWER_ROCK_IDS);
		if (lowerGems != null) {
			if (!lowerGems.isOnScreen()) {
				Camera.turnTo(lowerGems);
			}
			lowerGems.click(true);
			Main.sleepAnimation();

		} else {
			// gem node is null so walk to center and wait for gems
			if (center != null) {
				if (Calculations.distanceTo(center) < 5) {
					return;
				}
			}
			while (Players.getLocal().isMoving()) {
			}
			Walking.walk(getCenterTile());
			Task.sleep(1000, 3000);
		}
	}

	@Override
	public void messageReceived(MessageEvent msg) {
		String m = msg.getMessage();
		if (m.equals("You just mined an Opal!")) {
			gemsMined++;
		} else if (m.equals("You just mined a piece of Jade!")) {
			gemsMined++;
		} else if (m.equals("You just mined a Red Topaz!")) {
			gemsMined++;
		} else if (m.equals("You just mined a Sapphire!")) {
			gemsMined++;
		} else if (m.equals("You just mined an Emerald!")) {
			gemsMined++;
		} else if (m.equals("You just mined a Ruby!")) {
			gemsMined++;
		} else if (m.equals("You just mined a Diamond!")) {
			gemsMined++;
		}

	}

	public Tile getCenterTile() {
		return new Tile(Random.nextInt(2841, 2843), Random.nextInt(9386, 9388),
				0);
	}

}
