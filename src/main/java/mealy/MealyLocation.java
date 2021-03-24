package mealy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealyLocation {
    private String id;
    private String name;
    private boolean init;
    private String output;

    @Override
    public String toString() {
        return "{" +
                 id+":" +output+
                '}';
    }
}
