import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


//bank deposit chest deposit all: widget 11, widget child 19
//wicked hood location: widget 387, widgetchild 5
//bank ID: 70512
//location to check distance
//bank close widget: 11, 15

public class WickedHoodBank extends Node {
	
	public int bankId = 79215;
	public Tile depositBox = new Tile(3109, 3160, 3); //wicked Hood depositBox

	
	@Override
	public boolean activate() {
		if (Main.state.equals("wicked hood banking"))
			return true;
		return false;
	}

	@Override
	public void execute() {
		WidgetChild hood = Widgets.get(387, 5); // get the hood
		if (hood == null) {
			Main.wickedHood = false;
			Main.getState(); // get the state
			return;
		}
		// interact with hood to teleport
		while (!(Calculations.distanceTo(depositBox) < 15)) {
			while (!Tabs.EQUIPMENT.open())
				Tabs.EQUIPMENT.open();
			hood.interact("Teleport");
			Task.sleep(2000, 5000); // sleep while waiting
		}
		// deposit inventory into deposit box

		SceneObject bank = SceneEntities.getNearest(bankId);
		if (bank == null) {
			System.out.println("bank was null");
			return;
		}
		System.out.println("turning to bank");
		while (!bank.isOnScreen())
			Camera.turnTo(bank);

		WidgetChild depositAll = Widgets.get(11, 19);
		System.out.println("about to deposit items");
		while (Inventory.getCount() > 0) {
			while (!depositAll.isOnScreen()) {
				bank.interact("Deposit");
				Task.sleep(1000,2000);
			}
			depositAll.click(true);
			Task.sleep(250, 500);
		}

		WidgetChild bankClose = Widgets.get(11, 15);
		while(bankClose.isOnScreen()){
			bankClose.interact("Close");
			Task.sleep(250,500);
		}
		
		// teleport to mine after depositing items
		Main.teleportToMine();
	}
}
