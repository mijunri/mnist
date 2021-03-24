package experiment;

import classificationTree.ClassificationTree;
import frame.Teacher;
import frame.Word;
import mealy.MealyAutomata;
import mealy.MealyLocation;
import mnist.MnistRead;
import java.io.IOException;
import java.util.Map;

public class ClassificationTreeExperiment {

    public static void main(String[] args) throws IOException {

        Teacher teacher = MnistRead.buildTeacher();

        ClassificationTree classificationTree = new ClassificationTree("mnist", teacher);

        long start = System.currentTimeMillis();
        //自定义学习流程
        //1、观察表初始化
        classificationTree.init();

        //2、开始学习
        classificationTree.learn();

        //3、生成假设
        MealyAutomata hypothesis = classificationTree.buildHypothesis();
        System.out.println(hypothesis);
        //4、等价判断
        Word ce = null;
        while (null != (ce = teacher.equivalence(hypothesis))) {
//            System.out.println("反例是："+ce);
            classificationTree.refine(ce);
            hypothesis = classificationTree.buildHypothesis();
            System.out.println("迁移数" + hypothesis.getTransitions().size());

//            while (classificationTree.check(ce) == true) {
//                classificationTree.refine(ce);
//                hypothesis = classificationTree.buildHypothesis();
//                System.out.println("迁移数" + hypothesis.getTransitions().size());
//                System.out.println("reuse");
//            }

        }

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + " ms");

        System.out.println("学习结束");
//        System.out.println("最终自动机："+hypothesis);

        Map<Word,String> map = MnistRead.buildTestSet();

        int size = map.size();
        int correct = 0;
        for(Word word: map.keySet()){
            MealyLocation location = hypothesis.reach(word);
            if (location.getOutput().equals(map.get(word))){
                correct++;
            }
        }
        System.out.println("正确"+correct+"个");
        System.out.println("正确率:"+correct*1.0/size);


    }
}
