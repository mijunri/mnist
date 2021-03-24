package mnist;

import frame.Teacher;
import frame.Word;
import lombok.AllArgsConstructor;
import lombok.Data;
import mealy.MealyAutomata;
import mealy.MealyLocation;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@Data
@AllArgsConstructor
public class MnistTeacher implements Teacher {

    private Map<Word, String> language;


    @Override
    public String membership(Word word) {
        if (language.containsKey(word)){
            return language.get(word);
        }
        return "non";
    }

    @Override
    public Word equivalence(MealyAutomata hypothesis) {
        for(Word word: language.keySet()){
            MealyLocation location = hypothesis.reach(word);
//            System.out.println(word);
            if (location == null){
                System.out.println();
            }
            if (!StringUtils.equals(location.getOutput(), language.get(word))){
                return word;
            }
        }
        return null;
    }


}
