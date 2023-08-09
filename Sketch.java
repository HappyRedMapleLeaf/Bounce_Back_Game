import processing.core.PApplet;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import processing.core.PImage;

/**
 * This game will be played from the point of view of a city resident struggling to stay nourished. The player 
 * will bounce around the city from a top-down view, collecting food and income in an unforgiving environment 
 * that mirrors reality. The player will have the opportunity to buy upgrades that simulate real ways that a 
 * city could improve security, and the end goal is to generate an income stable enough to stay nourished 
 * permanently. The in-game upgrades and prompts will provide further information about food security, its 
 * impacts, and how it can be achieved. This game will give the player a deeper knowledge of the issue of food 
 * security in cities — essentially, it will serve as a tool to educate the user about urban food security.
 * 
 * @author Evan Li
 * @since 2023-06-19
 */
public class Sketch extends PApplet {
  // Gameplay variables
  private final float WIN_INCOME = 20.00f;  // how much income required for winning
  private String state;                     // which menu page the game is on
  private float $;                          // player money
  private float inc;                        // player income
  private float food;                       // player food points

  // Menu and upgrade variables
  private Button[] playButtons = new Button[4];     // buttons during gameplay
  private Button[] upgradeButtons = new Button[6];  // buttons on upgrade screen
  private Button confirmButton;   // button for confirming new game
  private PImage aboutPage;       // about page image
  private PImage upgradesPage;    // upgrade page background
  private PImage confirmPage;     // confirm page background
  private PImage winPage;         // win page image with text
  private PImage losePage;        // lose page image with text

  // Mechanics
  private Random r = new Random();    // for random seed generation later on (and other things)
  private World w;                               // world
  private Ball p = new Ball(this, 0.1f);  //p layer
  private float zoom = 128;           // pixels per tile
  private final int width = 400;      // dimensions of screen
  private final int height = 400;

  /**
   * Called at the beginning of the program before the window opens
   */
  public void settings() {
    // sets size of window
    size(width, height);
  }

  /**
   * Called at the beginning of the program 
   */
  public void setup() {
    // initialize menus and load world from file
    initMenus();
    load();
    // set state to nothing - no menus open
    state = "";
  }

  /**
   * Called periodically, as fast as possible up to 60 frames per second
   */
  public void draw() {
    // show win screen if the player isn't moving (not mid-turn), there's no menu open, and the income is high enough
    if (inc > WIN_INCOME && p.v.magnitude() == 0 && state.equals("")) {
      // set state to win and skip the rest of the frame
      state = "win";
      return;
    }

    // show lose screen if the player isn't moving (not mid-turn), there's no menu open, and food and money is low enough
    if (food < 1 && $ < 1 && p.v.magnitude() == 0 && state.equals("")) {
      // set state to lose and skip the rest of the frame
      state = "lose";
      return;
    }

    background(0, 0, 0);                              // clear screen
    w.loadChunks(p.p.x, p.p.y, width, height, zoom);  // load required chunks
    w.draw(p.p.x, p.p.y, width, height, zoom);        // draw world
    p.updatePosition();                               // update player position
    p.slowDown();                                     // slow down player
    p.draw(width, height, zoom);                      // draw player
    drawStats();                                      // draw money income and food

    // positions to start the search for collision detection (a 3x3 area around the player)
    int startSearchX = (int) Math.floor(p.p.x - 1);
    int startSearchY = (int) Math.floor(p.p.y - 1);

    // loop through 3x3 area
    for (int i = startSearchX; i < startSearchX + 3; i++) {
      for (int j = startSearchY; j < startSearchY + 3; j++) {
        // get the specified tile
        Tile tile = w.getTile(i, j);

        if (tile != Tile.AIR) {
          // perform collision if the tile isn't air
          // find nearest point on the tile, used for detection
          Vector near = p.getNear(new Vector(i, j));
          // if the player is colliding:
          if (p.isColliding(near)) {
            // update the player's position and velocity
            p.bounce(near);
            // update the chunk since the tile is now used
            w.updateChunk(i, j);

            // depending on the tile, change the player stats
            switch (tile) {
              case MONEY:
                // increase money by the amount determined by the 1st upgrade
                $ += ((UpgradeButton) upgradeButtons[1]).upgrade.factor;
                break;
              case INCOME:
                // increase income by the amount determined by the 1st upgrade
                inc += ((UpgradeButton) upgradeButtons[1]).upgrade.factor;
                break;
              case FOOD:
                // increase food by the amount determined by the 2nd upgrade
                food += ((UpgradeButton) upgradeButtons[2]).upgrade.factor;
                break;
              case BAD:
                // multiply all stats by the amount determined by the 3rd upgrade
                $ *= ((UpgradeButton) upgradeButtons[3]).upgrade.factor;
                inc *= ((UpgradeButton) upgradeButtons[3]).upgrade.factor;
                food *= ((UpgradeButton) upgradeButtons[3]).upgrade.factor;
                break;
              default:
                break;
            }
          }
        }
      }
    }

    // drawing menus
    switch (state) {
      case "":
        // draw all buttons for the playing screen (save, new game, etc.)
        for (Button b : playButtons) {
          b.draw();
        }
        // draw a line from the center of the screen to the mouse cursor when the player isn't moving
        // to help the player aim
        if (p.v.magnitude() == 0.0f) {
          stroke(color(255, 100, 100));
          strokeWeight(3);
          line(width / 2, height / 2, mouseX, mouseY);
          strokeWeight(1);
        }
        break;
      case "upgrades":
        // draw the upgrades background and upgrade buttons
        image(upgradesPage, 0, 0);
        for (Button b : upgradeButtons) {
          b.draw();
        }
        // draw the player stats over everything again so that the player can read them when buying upgrades
        drawStats();
        break;
      case "confirm":
        // draw confirm new game background and button
        image(confirmPage, 0, 0);
        confirmButton.draw();
        break;
      // draw backgrounds for the about, win, and lose pages
      case "about":
        image(aboutPage, 0, 0);
        break;
      case "win":
        image(winPage, 0, 0);
        break;
      case "lose":
        image(losePage, 0, 0);
        break;
    }
  }

  /**
   * Runs when the player presses a mosue button
   */
  public void mousePressed() {
    switch (state) {
      case "":
        // during gameplay, check for button clicks
        String action = "";
        for (Button b : playButtons) {
          // test click all buttons and see which action ends up being clicked
          if (action.equals("")) {
            action = b.click(mouseX, mouseY);
          }
        }

        switch (action) {
          case "":
            // if no button is pressed, launch the player (if it isn't moving and there's more than 1 food)
            if (p.v.magnitude() == 0 && food >= 1) {
              // normalized vector based on the difference between the center of the screen and the mouse position
              p.v = new Vector(mouseX - width / 2, mouseY - height / 2).norm().multScalar(0.09f);
              if (r.nextFloat() <= ((UpgradeButton) upgradeButtons[0]).upgrade.factor / 100.0f) {
                // depletes food by chance; if the random number is greater than the percentage determined
                // by the 0th upgrade
                food -= 1;
              }
              // add money based on income and reset zoom while the player is moving
              $ += inc;
              zoom = 128;
            }
            break;
          case "save":
            // save if save button is pressed
            save();
            break;
          // change states based on action
          case "new":
            state = "confirm";
            break;
          case "about":
            state = "about";
            break;
          case "upgrades":
            state = "upgrades";
            break;
          default:
            // print error if the action isn't handled properly
            print("NO STATE UNKNOWN ACTION: " + action);
        }
        break;
      case "upgrades":
        // loop through all upgrade buttons
        for (Button b : upgradeButtons) {
          // click the button and see what the result is
          switch (b.click(mouseX, mouseY)) {
            case "upgrade":
              // buy the upgrade if the button is an upgrade button
              Upgrade u = ((UpgradeButton) b).upgrade;
              // casts to UpgradeButton and buys the upgrade
              $ = u.buy($);
              break;
            case "back":
              // resets state if back button is pressed
              state = "";
              break;
            case "food":
              // buys food if there's enough money
              if ($ >= 1) {
                $ -= 1;
                food += 1;
              }
              break;
          }          
        }
        break;
      case "confirm":
        // load new game if confirm button is clicked
        if (!confirmButton.click(mouseX, mouseY).equals("")) {
          newGame();
        }
        state = ""; // either way, close the confirm screen
        break;
      case "about":
        //close the about screen upon click
        state = "";
        break;
      case "win":
      case "lose":
        //start a new game if the player wins or loses
        newGame();
        state = "";
        break;
    }
  }

  /**
   * Called when a key is pressed on the keyboard
   */
  public void keyPressed() {
    // the zoom level changes between 128 and 25 when the user presses z
    // but only when the player isn't moving (getting ready to move)
    if (key == 'z' && p.v.magnitude() == 0) {
      // toggle between zoom 128 and 25 upon pressing z
      if (zoom == 25) {
        zoom = 128;
      } else {
        zoom = 25;
      }
    }
  }

  /**
   * Initialize menus, buttons, and images for each menu
   */
  public void initMenus() {
    // buttons for buttons during gameplay
    playButtons[0] = new Button(10, 365, 50, 25, loadImage("graphics/save.png"), this, "save");
    playButtons[1] = new Button(70, 365, 75, 25, loadImage("graphics/new.png"), this, "new");
    playButtons[2] = new Button(155, 365, 50, 25, loadImage("graphics/aboutButton.png"), this, "about");
    playButtons[3] = new Button(215, 365, 75, 25, loadImage("graphics/upgradesButton.png"), this, "upgrades");

    // the four upgrades that will be available to be bought
    Upgrade[] upgrades = new Upgrade[4];
    upgrades[0] = new Upgrade(100f, 1.0f, 0.1f, -5f, 0, 15); //food depletion chance
    upgrades[1] = new Upgrade(1.0f, 1.5f, 0.15f, 0.1f, 0, 100); //money and income per hit
    upgrades[2] = new Upgrade(0.5f, 1.0f, 0.1f, 0.2f, 0, 100); //food per hit
    upgrades[3] = new Upgrade(0.6f, 1.5f, 0.15f, 0.02f, 0, 15); //multiplier when hit red

    // the upgrade buttons, including the upgrades themselves, the buy food button, and the back button
    upgradeButtons[0] = new UpgradeButton(100, 0, 300, 100, loadImage("graphics/upgrade0.png"), this, upgrades[0], 230, 84, 120, 84, 12);
    upgradeButtons[1] = new UpgradeButton(100, 100, 300, 100, loadImage("graphics/upgrade1.png"), this, upgrades[1], 215, 84, 110, 84, 12);
    upgradeButtons[2] = new UpgradeButton(100, 200, 300, 100, loadImage("graphics/upgrade2.png"), this, upgrades[2], 215, 84, 110, 84, 12);
    upgradeButtons[3] = new UpgradeButton(100, 300, 300, 100, loadImage("graphics/upgrade3.png"), this, upgrades[3], 230, 84, 120, 84, 12);
    upgradeButtons[4] = new Button(25, 160, 50, 25, loadImage("graphics/back.png"), this, "back");
    upgradeButtons[5] = new Button(0, 100, 100, 50, loadImage("graphics/foodButton.png"), this, "food");

    // button to confirm starting a new game
    confirmButton = new Button(100, 80, 200, 100, loadImage("graphics/continue.png"), this, "confirm");
    
    // images for menu backgrounds: about, upgrades, confirm, win and lose menus
    aboutPage = loadImage("graphics/about.png");
    upgradesPage = loadImage("graphics/upgrades.png");
    confirmPage = loadImage("graphics/confirm.png");
    winPage = loadImage("graphics/win.png");
    losePage = loadImage("graphics/lose.png");
  }

  /**
   * Saves the game state to a file
   * The world's state, player's position, upgrade button times bought, and resources are saved
   */
  public void save() {
    // first saves all the currently loaded chunks in the world
    w.save();
    try {
      // opens printwriter for the data file (not chunks)
      PrintWriter pw = new PrintWriter("world/_data.txt");
      // prints data on new lines: the world seed, player position, upgrade stats, and other stats
      pw.printf("%d\n%f\n%f\n%d\n%d\n%d\n%d\n%f\n%f\n%f",
                w.seed, p.p.x, p.p.y,
                ((UpgradeButton) upgradeButtons[0]).upgrade.timesBought,
                ((UpgradeButton) upgradeButtons[1]).upgrade.timesBought,
                ((UpgradeButton) upgradeButtons[2]).upgrade.timesBought,
                ((UpgradeButton) upgradeButtons[3]).upgrade.timesBought,
                $, inc, food);
      // saves file
      pw.close();
    } catch (IOException e) {
      // print an error if it occurs
      print(e);
    }
  }

  /**
   * Loads the game state from a file
   * The world's state, player's position, upgrade button times bought, and resources are loaded
   */
  public void load() {
    try {
      // new input stream from data file
      Scanner s = new Scanner(new File("world/_data.txt"));
      // world seed from first line
      w = new World(this, s.nextInt());
      // player position from next 2 lines
      p.p.x = s.nextFloat();
      p.p.y = s.nextFloat();
      // reset velocity
      p.v = new Vector(0, 0);
      for (int j = 0; j < 4; j++) {
        // loop through each upgrade and buy the upgrade the number of times
        // as specified in the data file, given infintie money
        int c = s.nextInt();
        for (int i = 0; i < c; i++) {
          ((UpgradeButton) upgradeButtons[j]).upgrade.buy(99999);
        }
      }
      // set money, income, and food based on the last 3 lines
      $ = s.nextFloat();
      inc = s.nextFloat();
      food = s.nextFloat();
      // close file
      s.close();
    } catch (IOException e) {
      // print an error if it occurs
      print(e);
    }
  }

  /**
   * Starts a new game and saves it
   * The world, resources, player's position, and upgrade buttons are reset
   */
  public void newGame() {
    // generate new seed and create new world
    w = new World(this, r.nextInt());
    // resets stats
    $ = 0;
    inc = 0;
    food = 3;
    // reset player position and velocity
    p.p = new Vector(0, 0);
    p.v = new Vector(0, 0);
    // reinitialize menus (to reset upgrades)
    initMenus();
    // get world save directory
    File dir = new File("world");
    // loop through every file in the directory
    for (File f : dir.listFiles()) {
      if (f.getName() != "_data.txt") {
        // delete file if it isn't the data file (deletes chunk data)
        f.delete();
      }
    }
    // save new world
    save();
  }

  /**
   * Draws player stats on the screen
   * Displays the current amount of money, income, and food
   */
  public void drawStats() {
    // set text size and colour
    fill(255);
    textSize(15);
    // use format string to draw 3 lines of text
    text(String.format("Money: $%.2f\nIncome: $%.2f\nFood: %.2f", $, inc, food), 5, 20);
  }
}