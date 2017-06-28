import java.util.ArrayList;

public class ActionTemplates extends ArrayList<ActionTemplate> {
	ActionTemplate find(String name){
		for (ActionTemplate a : this){
			if (name.equals(a.name)) {
				return a;
			}
		}
		return null;
	}
}
