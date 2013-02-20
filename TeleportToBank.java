import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

//have 1 slot in inventory for ring of dueling, need wealth for ring slot. 
//must go in first inventory slot
//RoD IDS (2552), (2554), (2556), (2558), (2560), (2562), (2564), (2566) 
//validate chatBox after rubbing ring of dueling before sending through key.
//this will only be in used in conjunction with karajama gloves.
public class TeleportToBank extends Node {

	public int currentRingId;
	public Tile castleWars = new Tile(2442, 3087, 0);

	@Override
	public boolean activate() {
		if (Main.state.equals("teleporting to castle wars"))
			return true;
		return false;
	}

	@Override
	public void execute() {
		// teleport to the bank
		// going to have to iterate over array to see if we have a ring
		if (Calculations.distanceTo(castleWars) > 15) { // if we are not at
														// castlewars
			while (!Tabs.INVENTORY.isOpen())
				// opens up inventory
				Tabs.INVENTORY.open();

			Item ring = null;
			if (Inventory.getItemAt(0) != null) { // makes sure ring exists
				ring = Inventory.getItemAt(0);
				ring.getWidgetChild().interact("Rub");
			} else {
				Main.castleWarsBank = false;
				Main.getState();
				return;
			}

			// options that pop up when rubbed ring of dueling
			WidgetChild options = Widgets.get(1188, 13);
			int prevRingId = ring.getId();
			Task.sleep(100,200); //wait for screen to pop up
			while (Calculations.distanceTo(castleWars) > 15) {
				if (options.isOnScreen()) {
					options.interact("Continue");
					Task.sleep(3500, 5000); // gives time to teleport.
				} else { // case that options isn't on screen, so rub ring
					ring.getWidgetChild().interact("Rub");
					Task.sleep(250, 500);
				}
			}
			if (Inventory.getItemAt(0) != null) { // check to see if we have a
													// ring.
				ring = Inventory.getItemAt(0);
				currentRingId = ring.getId(); // gets ID after teleport so we
												// can
												// withdraw
				while (prevRingId == currentRingId) {
					currentRingId = ring.getId();
				}
			} else {
				currentRingId = -1;
			}
		}
		// we are confirmed to be at castlewars.
		// deposit all of our gems
		SceneObject bank = SceneEntities.getNearest(4483);
		Camera.turnTo(bank); // needed to turn camera toward bank.

		while (!Bank.isOpen()) {
			Bank.open();
			Task.sleep(100, 250);
		}
		while (Inventory.getCount() > 0) {
			Bank.depositInventory();
			Task.sleep(150, 300);
		}
		while (Inventory.getItemAt(0) == null) {
			if (currentRingId != 2566) {
				Bank.withdraw(currentRingId, 1); // withdraws the ring we just
													// deposited
			} else {
				Bank.withdraw(2552, 1);
			}
			Task.sleep(200, 500);
		}
		while (Bank.isOpen()) {
			Bank.close();
		}

		Main.teleportToMine();
		Main.state = "mining underground";

	}

}
