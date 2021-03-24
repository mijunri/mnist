package frame;


import mealy.MealyAutomata;

public interface Teacher {
    String membership(Word word);
    Word equivalence(MealyAutomata hypothesis);
}
