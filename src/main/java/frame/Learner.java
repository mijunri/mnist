package frame;

import mealy.MealyAutomata;

public interface Learner {
    //生命周期方法

    //初始化
    void init();

    //学习
    void learn();


    boolean check(Word counterExample);

    //对反例进行处理
    void refine(Word counterExample);

    //构造假设自动机
    MealyAutomata buildHypothesis();

    //获取最终结果自动机
    MealyAutomata getFinalHypothesis();

}
