public class Upgrade {
  public float factor;              // how much the upgrade changes the specific stat
  public float price;               // cost of upgrade
  private float priceIncrease;      // The amount by which the price increases after each purchase
  private float factorChange;       // The amount by which the factor changes after each purchase
  public int timesBought;           // The number of times the upgrade has been bought
  private float timesMax;           // The maximum number of times the upgrade can be bought

  /**
   * Constructs a new Upgrade object
   *
   * @param factor        The initial factor of the upgrade
   * @param price         The initial price of the upgrade
   * @param priceIncrease The amount by which the price increases after each purchase
   * @param factorChange  The amount by which the factor changes after each purchase
   * @param timesBought   The initial number of times the upgrade has been bought
   * @param timesMax      The maximum number of times the upgrade can be bought
   */
  public Upgrade(float factor, float price, float priceIncrease, float factorChange, int timesBought, float timesMax) {
    this.factor = factor; this.price = price; this.priceIncrease = priceIncrease;
    this.factorChange = factorChange; this.timesBought = timesBought; this.timesMax = timesMax;
  }

  /**
   * Buys the upgrade and returns remaining money. Checks for whether or not the upgrade can be bought
   *
   * @param money The amount of money used for the purchase
   * @return The remaining money after the purchase
   */
  public float buy(float money) {
    //check if the upgrade can still be bought and if the player can afford it
    if (money >= price && timesBought < timesMax) {
      // returns how much new money should be
      factor += factorChange;               //change factor
      float remainingMoney = money - price; //calculate remaining money
      price += priceIncrease;               //increase price
      timesBought++;                        //increment timesBought

      // sets price to 0 to indicate to the user that the upgrade can't be bought anymore
      if (timesBought >= timesMax) {
        price = 0;
      }
      return remainingMoney;
    }
    //return original amount of money if the upgrade can't be bought, and do nothing
    return money;
  }
}