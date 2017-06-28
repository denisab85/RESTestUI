import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class ActionTemplate {
    	String name;
    	String type;
    	String method;
    	String description;
    	String uri;
    	ArrayList<ParameterTemplate> parameters = new ArrayList<ParameterTemplate>();
    	Integer validate_status;
    	Map<String, String> validate_content;
    	ArrayList<String> output;
    	public ActionTemplate (Object item) {
    		JSONObject json_item = (JSONObject)item;
    		this.name = (String)json_item.get("name");
    		this.type = (String)json_item.get("type");
    		this.method = (String)json_item.get("method");
    		this.description = (String)json_item.get("description");
    		this.uri = (String)json_item.get("uri");
    		this.validate_status = ((Long)json_item.get("validate_status")).intValue();
    		this.output = (ArrayList<String>)json_item.get("output");
    		this.validate_content = (Map<String, String>)json_item.get("validate_content");
    		JSONArray params = (JSONArray)json_item.get("parameters");

    		for (Object par : params) {
    			ParameterTemplate parameter = new ParameterTemplate(par);
    			this.parameters.add(parameter);
    		}
    	};
    }