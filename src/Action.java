import java.util.Map;
import org.json.simple.JSONObject;

public class Action {
	String action;
	Integer enabled;
	Map<String, String> parameters;
	Map<String, String> validate_content;
	public Action (Object item) {
		JSONObject json_item = (JSONObject)item;
		this.action = (String)json_item.get("action");
		this.enabled = ((Long)json_item.get("enabled")).intValue();
		this.validate_content = (Map<String, String>)json_item.get("validate_content");
		this.parameters = (Map<String, String>)json_item.get("parameters");
	};
}

