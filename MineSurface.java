import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;


public class MineSurface extends Node implements MessageListener {

	public int gemsMined;
	public int numberOfRocks = 7;
	public final int[] UPPER_ROCK_IDS = { 11364, 11195, 11194 };
	public Tile[] toSurface = { new Tile(2849, 2966, 0),
			new Tile(2839, 2970, 0), new Tile(2831, 2977, 0),
			new Tile(2827, 2988, 0), Main.surface };

	@Override
	public boolean activate() {
		if (Main.state.equals("mining surface")) {
			return true;
		}
		return false;
	}

	@Override
	public void execute() {
		
		if(Main.karajamaGloves)
		atLocation(); //need to make sure if we are using these that we do not misclick ladder
		
		gemsMined = 0;
		while (gemsMined != numberOfRocks) {
			if (Main.inventorySpace()) {
				return;
			}
			while(Camera.getPitch() < 80){ //sets pitch so we can access all gems.
				 Camera.setPitch((int)((Camera.getPitch()+(5+Math.random()*8))));
				}
			
			mineGem();
		}
		if(Main.karajamaGloves){ 
		Main.state = "mining underground";
		//need to check if we go underground aswell.
		}
		else {
			gemsMined = 0;
		}
	}

	public void mineGem() {
		if (Players.getLocal().isMoving()) {
			return;
		}
		SceneObject surfaceGems = SceneEntities.getNearest(UPPER_ROCK_IDS);
		if (surfaceGems != null) {
			if (!surfaceGems.isOnScreen())
				Camera.turnTo(surfaceGems);
			
			surfaceGems.click(true);
			Main.sleepAnimation();
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
	
	
	public void atLocation(){ //occasionally got stuck when clicked ladder.
		if(!Main.surface.validate()){
			if(Main.below.validate()){ //case that we are underground, need to click ladder
				Main.climbUpLadder();
			}
			else { //not sure where we are so teleport underground and then climb ladder
				Main.teleportToMine();
				Main.climbUpLadder();
			}
		}
	}
}
