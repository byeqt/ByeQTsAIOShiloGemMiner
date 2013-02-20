import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/*Make a better paint
 *% of level progress
 *gem bag support
 *Takes too long inbetween each rock mine!
 *REALLY NEED TO SPEED UP MINING!
 * 
 *GEM IDS:
 *OPAL = 1625
 *JADE = 1627
 *TOPAZ = 1629
 *Sapphire = 1623
 *Emerald = 1621
 *ruby = 1619
 *Diamond = 1617
 * paint : http://tinypic.com/view.php?pic=2vlqw5x&s=6
 */

/*HOW TO RUN:
 * CHOOSE OPTIONS
 * If dueling ring is selected, have in first slot in inventory.
 * if wickedhood is selected, make sure it is equipped.
 * if karajama gloves are being used, make sure they are equipped
 */
@Manifest(authors = { "Byeqt" }, name = "Byeqt's AIOShiloGemMiner", description = "Mines gems at shilo. Underground support. Banking using dueling. Banking using Wicked Hood.", version = 1.0)
public class Main extends ActiveScript implements PaintListener,
		MouseListener {

	public static String state = "";
	public static Tile surface = new Tile(2823, 2998, 0);
	public static Tile bank = new Tile(2852, 2955, 0); // bank tile
	public static Tile below = new Tile(2840, 9396, 0); // lower level
	public static Tile bankChest = new Tile(3109, 3160, 3); // wicked Hood
	public static Tile castleWarsTile = new Tile(2442, 3087, 0);
	public final static int LADDER_ID_TOP = 23586;
	public static final int LADDER_ID_BOTTOM = 23584;

	public static Map<Integer, Boolean> gemsToDrop = new HashMap<Integer, Boolean>();
	public static int[] gemIds = { (1625), (1627), (1629), (1623), (1621),
			(1619), (1617) };
	public static int[] gemDropList;
	public static int totalTypes;

	// paint details
	public WidgetChild paint = Widgets.get(137, 0);
	public int paintWidth = paint.getWidth(); // width will be 506
	public int paintHeight = paint.getHeight(); // height will be 130
	public int paintX = paint.getAbsoluteX();
	public int paintY = paint.getAbsoluteY();
	public int checkBoxSize = 10;
	public int opalX = 50;
	public int opalY = 410;
	public int jadeX = 50;
	public int jadeY = 450;
	public int topazX = 50;
	public int topazY = 490;
	public int sapphX = 105;
	public int sapphY = 410;
	public int emeraldX = 105;
	public int emeraldY = 450;
	public int rubyX = 105;
	public int rubyY = 490;
	public int diamondX = 160;
	public int diamondY = 410;
	public int duelingX = 160;
	public int duelingY = 450;
	public int gloveX = 160;
	public int gloveY = 490;
	public int hoodX = 215;
	public int hoodY = 450;
	public int startX = 385;
	public int startY = 465;
	public int endStartX = 505;
	public int endStartY = 520;

	public Image background = getImage("http://i50.tinypic.com/2vlqw5x.jpg");

	// need to add this stuff in so that it counts it all.
	public static int totalGems, numOpals, numJades, numTopaz, numEmerald,
			numRuby, numDiamond;

	// conditions for script, give user option whether to keep or drop
	public static boolean karajamaGloves, castleWarsBank, gemBag, wickedHood,
			opal, jade, topaz, sapphire, emerald, ruby, diamond, hide, start,
			lost;

	private final List<Node> jobsCollection = Collections
			.synchronizedList(new ArrayList<Node>());
	private Tree jobContainer = null;

	public synchronized final void provide(final Node... jobs) {
		for (final Node job : jobs) {
			if (!jobsCollection.contains(job)) {
				jobsCollection.add(job);
			}
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection
				.size()]));
	}

	public synchronized final void revoke(final Node... jobs) {
		for (final Node job : jobs) {
			if (jobsCollection.contains(job)) {
				jobsCollection.remove(job);
			}
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection
				.size()]));
	}

	public final void submit(final Job... jobs) {
		for (final Job job : jobs) {
			getContainer().submit(job);
		}
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onRepaint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		if (Game.getClientState() != 11)
			return;

		if (!start) {
			g.drawImage(background, paintX, paintY, null);
			drawBoxes(g);
			return;
		}
		if (!hide) {
			// paint once started with xp bar in here
		}

		if (lost) {
			g.drawString(state + "! Please restart script", 200, 200);
		}

		g.setColor(Color.black);
		g.drawString(state, 200, 200);

	}

	public void drawBoxes(Graphics g) {
		// if gem is false then we are keeping if true we are dropping
		// yes this is hardcoded.... cbf
		if (opal) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.green);
		}
		g.fillRect(opalX, opalY, checkBoxSize, checkBoxSize);
		if (jade) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(jadeX, jadeY, checkBoxSize, checkBoxSize);

		if (topaz) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(topazX, topazY, checkBoxSize, checkBoxSize);

		if (sapphire) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(sapphX, sapphY, checkBoxSize, checkBoxSize);

		if (emerald) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(emeraldX, emeraldY, checkBoxSize, checkBoxSize);

		if (ruby) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(rubyX, rubyY, checkBoxSize, checkBoxSize);

		if (diamond) {
			g.setColor(Color.red);

		} else {
			g.setColor(Color.green);

		}
		g.fillRect(diamondX, diamondY, checkBoxSize, checkBoxSize);

		if (castleWarsBank) {
			g.setColor(Color.green);
		} else {
			g.setColor(Color.red);

		}
		g.fillRect(duelingX, duelingY, checkBoxSize, checkBoxSize);

		if (karajamaGloves) {
			g.setColor(Color.green);
		} else {
			g.setColor(Color.red);

		}
		g.fillRect(gloveX, gloveY, checkBoxSize, checkBoxSize);

		if (wickedHood) {
			g.setColor(Color.green);
		} else {
			g.setColor(Color.red);
		}
		g.fillRect(hoodX, hoodY, checkBoxSize, checkBoxSize);
	}

	// method to get image for paint
	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public int loop() {
		if (lost) {
			return Random.nextInt(1000, 3000);
		}
		if (!start)
			return Random.nextInt(1000, 2000);

		if (jobContainer != null) {
			final Node job = jobContainer.state();
			if (job != null) {
				jobContainer.set(job);
				getContainer().submit(job);
				job.join();
			}
		}
		return Random.nextInt(50, 250);
	}

	// used when started and to determined what we will do next.
	public static void getState() {
		if (inventorySpace()) { // check for full inventory
			return; // if full return, it will go to bank and then get the state
		}
		if (Calculations.distanceTo(surface) < 20) { // case that script started
														// at surface
			state = "mining surface";
		} else if (Calculations.distanceTo(bank) < 20) { // case that script
															// started at bank
			if (karajamaGloves) {
				teleportToMine();
			} else {
				state = "walking to surface";
			}
		} else if (Calculations.distanceTo(bankChest) < 20) {
			teleportToMine(); // teleport to mine cuz inventory empty. teleport
								// method gets state
		} else if (Calculations.distanceTo(below) < 20) {
			state = "mining underground";
		} else if (Calculations.distanceTo(castleWarsTile) < 20) {
			teleportToMine();
		} else if (karajamaGloves) {
			// case that we are lost and equipped karajama gloves
			teleportToMine();
		} else {
			/*
			 * Player is not in any obvious location so we need to check if
			 * player is along a path to or from bank
			 */
			if (!(WalkToSurface.checkIfNearPath())) {
				// we are lost
				lost = true;
				state = "lost";// set state to nothing because we are lost...
				// needs to be manually reset
			}

			// method will check if near path and if it is it will walk to
			// surface
			// if this returns false we are lost and should turn off script
		}
	}

	public static void climbUpLadder() {
		while (!(surface).validate()) {
			SceneObject ladder = SceneEntities.getNearest(LADDER_ID_BOTTOM);
			if (ladder != null) {
				Camera.turnTo(ladder);
				while (Players.getLocal().isMoving())
					sleep(100, 200);
				ladder.click(true);
				sleep(750, 1500);
			}
		}
	}

	public static void climbDownLadder() {
		while (!(below).validate()) {
			SceneObject ladder = SceneEntities
					.getNearest(LADDER_ID_TOP);
			if (ladder != null) {
				while (Players.getLocal().isMoving())
					sleep(100, 200);
				ladder.click(true);
				sleep(500, 1000);
			} else { // case that we are out of range of ladder so teleport.
				teleportToMine();
			}
		}
	}

	public static void teleportToMine() {
		while (!below.validate()) {
			while (!Tabs.EQUIPMENT.open())
				Tabs.EQUIPMENT.open();
			WidgetChild gloves = Widgets.get(387, 26);
			if (gloves == null) {
				karajamaGloves = false;
				getState();
				return;
			}
			gloves.interact("Teleport");
			Task.sleep(4000, 6000);
		}
		getState(); // after we teleport to mine we need to get the state

	}

	public static boolean inventorySpace() {
		if (Inventory.getCount() == 28) {
			if (dropGems())
				return false;
			if (wickedHood) {
				state = "wicked hood banking";
			} else if (castleWarsBank) {
				state = "teleporting to castle wars";
			} else {
				// we should validate that we are near the path or break the
				// script.
				if (Calculations.distanceTo(surface) < 30) {
					state = "walking to bank";
				}
				if (!WalkToBank.checkIfNearPath()) {
					state = "walking to bank";
				}
			}
			return true;
		}
		return false;
	}
	
	public void prepareDropGems(){
		totalTypes = 0;
		for (Integer i : gemsToDrop.keySet()) {
			if (gemsToDrop.get(i)) {
				totalTypes++;
			}
		}
		//if totaltypes == 0 then we dont have any gems to drop.
		
		gemDropList = new int[totalTypes];
		int v = 0;
		for (Integer i : gemsToDrop.keySet()) {
			if (gemsToDrop.get(i)) {
				gemDropList[v++] = i;
			}
		}
	}

	public static boolean dropGems() { // this should work fine...
		if(totalTypes == 0){
			return false;
		}
		boolean dropped = false;

		//wow harder to drop stuff then i thought.
		while (Inventory.contains(gemDropList)) {
			for (int i = 0; i < gemIds.length; i++) {
				if (gemsToDrop.get(gemIds[i])) {
					for (int j = 0; j < 28; j++) {
						Item current = Inventory.getItemAt(j);
						if (current != null) {
							if (current.getId() == gemIds[i]) {
								current.getWidgetChild().interact("Drop");
								Task.sleep(200, 350);
								System.out.println("dropping a gem");
								dropped = true;
							}
						}
					}
				}
			}
		}
		if (dropped)
			return true;
		System.out.println("didn't drop any gems");
		return false;
	}

	public static void sleepAnimation() {
		Task.sleep(500, 750); // sleep after click so run moving gets time to
								// register
		if (Players.getLocal().getAnimation() != -1) {
			while (Players.getLocal().getAnimation() != -1) {
			}
		} else {
			Task.sleep(1500, 2500);
		}
	}

	public void supplyJobs() {
		System.out.println("in supply jobs method");
		if (karajamaGloves && castleWarsBank) {
			provide(new MineSurface(), new MineUnderground(),
					new TeleportToBank());
		} else if (karajamaGloves && wickedHood) {
			provide(new MineSurface(), new MineUnderground(),
					new WickedHoodBank());
		} else if (karajamaGloves) {
			provide(new WalkToBank(), new MineSurface(), new MineUnderground());
		} else {
			provide(new WalkToBank(), new MineSurface(), new WalkToSurface());
		}
		getState();
		System.out.println("leaving supply jobs method");
	}

	public void populateGemsToDrop() {
		gemsToDrop.put(1625, opal);
		gemsToDrop.put(1627, jade);
		gemsToDrop.put(1629, topaz);
		gemsToDrop.put(1623, sapphire);
		gemsToDrop.put(1621, emerald);
		gemsToDrop.put(1619, ruby);
		gemsToDrop.put(1617, diamond);
	}
	

	@Override
	public void mouseClicked(MouseEvent m) {
		// set all the different variables

		/*
		 * OPALX=50OPALY=410JADEX= 50JADEY= 450TOPAZX=50TOPAZY= 490SAPPHIREX=
		 * 105 SAPPHIREY= 410EMERALDX= 105EMERALDY= 450RUBYX= 105RUBYY=
		 * 490DIAMONDX= 160 DIAMONDY= 410DUELINGX= 160DUELINGY= 450GLOVEX=
		 * 160GLOVEY= 490HOODX= 215 HOODY= 450STARTX= 385STARTY=
		 * 465STARTENDX=505STARTENDY= 520
		 */
		if (start) {
			return;
		}
		System.out.println("pressing buttons");
		int x = m.getX();
		int y = m.getY();
		if (x >= 385 && x <= 465) {
			if (y >= 465 && y <= 520) {
				populateGemsToDrop();
				start = true;
				prepareDropGems();
				supplyJobs();
				return;
			}
		}

		// clicked opal checkbox
		if (x >= 50 && x <= 60) {
			if (y >= 410 && y <= 420) {
				if (opal) {
					opal = false;
				} else {
					opal = true;
				}
				return;
			}
		}
		// clicked jade checkbox
		if (x >= 50 && x <= 60) {
			if (y >= 450 && y <= 460) {
				if (jade) {
					jade = false;
				} else {
					jade = true;
				}
				return;
			}
		}
		// topaz
		if (x >= 50 && x <= 60) {
			if (y >= 490 && y <= 500) {
				if (topaz) {
					topaz = false;
				} else {
					topaz = true;
				}
				return;
			}
		}
		// sapph
		if (x >= 105 && x <= 115) {
			if (y >= 410 && y <= 420) {
				if (sapphire) {
					sapphire = false;
				} else {
					sapphire = true;
				}
				return;
			}
		}
		// emerald
		if (x >= 105 && x <= 115) {
			if (y >= 450 && y <= 460) {
				if (emerald) {
					emerald = false;
				} else {
					emerald = true;
				}
				return;
			}

		}
		// ruby
		if (x >= 105 && x <= 115) {
			if (y >= 490 && y <= 500) {
				if (ruby) {
					ruby = false;
				} else {
					ruby = true;
				}
				return;
			}

		}
		// diamond
		if (x >= 160 && x <= 170) {
			if (y >= 410 && y <= 420) {
				if (diamond) {
					diamond = false;
				} else {
					diamond = true;
				}
				return;

			}

		}
		// dueling
		if (x >= 160 && x <= 170) {
			if (y >= 450 && y <= 460) {
				if (castleWarsBank) {
					castleWarsBank = false;
				} else {
					if (wickedHood) {
						wickedHood = false; // cant have both
					}
					castleWarsBank = true;
				}
				return;

			}

		}
		// gloves
		if (x >= 160 && x <= 170) {
			if (y >= 490 && y <= 500) {
				if (karajamaGloves) {
					karajamaGloves = false;
				} else {
					karajamaGloves = true;
				}
				return;

			}

		}
		// hood
		if (x >= 215 && x <= 225) {
			if (y >= 450 && y <= 460) {
				if (wickedHood) {
					wickedHood = false;
				} else {
					if (castleWarsBank) { // we cant have both
						castleWarsBank = false;
					}
					wickedHood = true;
				}
			}
			return;

		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
