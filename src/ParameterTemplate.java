import org.json.simple.JSONObject;


public class ParameterTemplate {
	String name;
    Integer required;
    String type;
    public ParameterTemplate(Object par) {
    	JSONObject json_item = (JSONObject)par;
    	this.name = json_item.get("name").toString();
    	this.type = json_item.get("type").toString();
    	this.required = ((Long)json_item.get("required")).intValue();
    }
}