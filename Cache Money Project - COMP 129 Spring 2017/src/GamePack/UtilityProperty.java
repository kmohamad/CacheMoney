package GamePack;

import ScreenPack.DicePanel;

public class UtilityProperty extends Property {
	
	public UtilityProperty(int cost, String name) {
		super(cost, name);
		init();
	}
	
	@Override
	protected void init()
	{

		//This space charges the non-owners with 4*the dice amount rolled to land on this space
		//If both Utilities are owned, it becomes 10* the dice amount rolled to land on this space
		rentMultiplier = 0;
		rentValues.add(4);
		rentValues.add(10);
	}
}
