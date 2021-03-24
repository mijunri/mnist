package frame;

import mealy.MealyAutomata;

public interface EquivalenceQuery{
    Word findCounterExample(MealyAutomata hypothesis);

    int getCount();
}
