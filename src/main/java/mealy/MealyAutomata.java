package mealy;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import frame.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealyAutomata {
    private String name;
    private List<MealyLocation> locations;
    private List<MealyTransition> transitions;

    public int size() {
        return locations.size();
    }

    private MealyLocation getInitLocation() {
        for (MealyLocation l : locations) {
            if (l.isInit()) {
                return l;
            }
        }
        return null;
    }


    //给定一个逻辑时间字，DOTA最多一个路径
    public MealyLocation reach(Word word) {

        MealyLocation location = getInitLocation();

        List<String> actions = word.getActions();

        flag:
        for (String action : actions) {
            List<MealyTransition> transitions = getTransitions(location);
            for (MealyTransition transition : transitions) {
                Guard guard = transition.getGuard();
                if (guard.isPass(action)) {
                    location = transition.getTargetLocation();
                    continue flag;
                }
            }
            return null;
        }
        return location;
    }

    public List<MealyTransition> getTransitions(MealyLocation location) {
        List<MealyTransition> transitions1 = new ArrayList<>();
        for (MealyTransition transition: transitions){
            if (transition.getSourceLocation() == location){
                transitions1.add(transition);
            }
        }
        return transitions1;
    }

    public List<MealyTransition> getTransitions(MealyLocation sourceLocation, MealyLocation targetLocation) {
        List<MealyTransition> transitions1 = new ArrayList<>();
        for (MealyTransition transition: transitions){
            if (transition.getSourceLocation() == sourceLocation
                    && transition.getTargetLocation() == targetLocation){
                transitions1.add(transition);
            }
        }
        return transitions1;
    }


    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mealy",this);
        return jsonObject.toString(SerializerFeature.PrettyFormat);
    }
}
