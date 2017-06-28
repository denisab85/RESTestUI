import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.graphics.Rectangle;

public class Main {

	protected Shell shlRestest;
	private Table scenarioTable;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlRestest.open();
		shlRestest.layout();
		while (!shlRestest.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void openProject(String filePath, ArrayList<Action> actions, Shell shell) {
		JSONParser parser = new JSONParser();
		String content = "";
		actions.clear();
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			try {
				Object obj = parser.parse(content);
				JSONObject root = (JSONObject) obj;
				JSONObject testInfo = (JSONObject) root.get("info");
				String testName = testInfo.get("name").toString();
				String testDescription = testInfo.get("description").toString();
				shell.setText("RESTest - " + testName + " (" + testDescription + ") - [" + filePath + "]");
				JSONArray testScenario = (JSONArray) root.get("scenario");
				for (Object item : testScenario) {
					Action action = new Action(item);
					actions.add(action);
				}
			} catch (ParseException pe) {
				System.out.println("position: " + pe.getPosition());
				System.out.println(pe);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	void actionTemplatesToTree(ActionTemplates actionTemplates, Tree tree) {
		for (ActionTemplate actionTemplate : actionTemplates) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(new String[] { actionTemplate.name, actionTemplate.type });
		}
	}

	TreeItem subItemToTreeItem(TreeItem treeItem, String name, Object object) {
		if (object != null) {
			TreeItem subItem = new TreeItem(treeItem, SWT.NONE);
			String value = (object instanceof String) ? (String) object : "";
			subItem.setText(new String[] { name, value });
			return subItem;
		}
		return null;
	}

	void actionParametersToTree(ActionTemplates actionTemplates, Action action, Tree tree) {
		tree.removeAll();
		String actionName = action.action;
		ActionTemplate actionTemplate = actionTemplates.find(actionName);
		TreeItem itemInfo = new TreeItem(tree, SWT.NONE);

		// Info
		itemInfo.setText("Info");
		subItemToTreeItem(itemInfo, "Name", actionTemplate.name);
		subItemToTreeItem(itemInfo, "Description", actionTemplate.description);
		subItemToTreeItem(itemInfo, "Type", actionTemplate.type);
		subItemToTreeItem(itemInfo, "Method", actionTemplate.method);
		subItemToTreeItem(itemInfo, "URI", actionTemplate.uri);
		subItemToTreeItem(itemInfo, "Status validation", actionTemplate.validate_status.toString());
		TreeItem contentSubItem = subItemToTreeItem(itemInfo, "Content validation", actionTemplate.validate_content);
		if (contentSubItem != null) {
			for (String key : actionTemplate.validate_content.keySet()) {
				subItemToTreeItem(contentSubItem, key, actionTemplate.validate_content.get(key));
			}
			contentSubItem.setExpanded(true);
		}
		itemInfo.setExpanded(true);

		// Parameters
		if (actionTemplate.parameters != null) {
			TreeItem itemParameters = new TreeItem(tree, SWT.NONE);
			itemParameters.setText("Parameters");
			for (ParameterTemplate parameter : actionTemplate.parameters) {
				String value = "";
				subItemToTreeItem(itemParameters, parameter.name, value);
			}
			itemParameters.setExpanded(true);
		}

		// Output
		if (actionTemplate.output != null) {
			TreeItem itemOutput = new TreeItem(tree, SWT.NONE);
			itemOutput.setText("Output");
			for (String item : actionTemplate.output) {
				String value = "";
				subItemToTreeItem(itemOutput, item, value);
			}
			itemOutput.setExpanded(true);
		}
	}

	void scenarioToTable(ArrayList<Action> scenario, ActionTemplates actionTemplates, Table scenarioTable) {
		Integer n = 1;
		scenarioTable.removeAll();
		for (Action action : scenario) {
			TableItem item = new TableItem(scenarioTable, SWT.NONE);
			ActionTemplate actionTemplate = actionTemplates.find(action.action);
			String type = actionTemplate.type;
			item.setText(new String[] { "", n.toString(), type, action.action });
			if (action.enabled == 1) {
				item.setChecked(true);
			}
			;
			n++;
		}
	}

	Integer cellFromMouseDown(Table table, Event event) {
		Rectangle clientArea = table.getClientArea();
		Point pt = new Point(event.x, event.y);
		int index = table.getTopIndex();
		while (index < table.getItemCount()) {
			boolean visible = false;
			TableItem item = table.getItem(index);
			for (int i = 0; i < table.getColumnCount(); i++) {
				Rectangle rect = item.getBounds(i);
				if (rect.contains(pt)) {
					return index;
				}
				if (!visible && rect.intersects(clientArea)) {
					visible = true;
				}
			}
			if (!visible)
				return -1;
			index++;
		}
		return 0;
	}

	/**
	 * Create contents of the window.
	 * 
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 */
	protected void createContents() {
		shlRestest = new Shell();
		shlRestest.setMinimumSize(new Point(610, 585));
		shlRestest.setSize(613, 585);
		shlRestest.setText("RESTest");
		shlRestest.setLayout(null);

		Tree actionEditTree = new Tree(shlRestest, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		actionEditTree.setBounds(254, 10, 337, 309);
		actionEditTree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(actionEditTree, SWT.LEFT);
		column1.setText("Name");
		column1.setWidth(148);
		TreeColumn column2 = new TreeColumn(actionEditTree, SWT.LEFT);
		column2.setText("Value");
		column2.setWidth(185);

		ArrayList<Action> scenario = new ArrayList<Action>();
		ActionTemplates actionTemplates = new ActionTemplates();

		scenarioTable = new Table(shlRestest, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		scenarioTable.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Integer cellClicked = cellFromMouseDown(scenarioTable, event);
				System.out.println(cellClicked);
				actionParametersToTree(actionTemplates, scenario.get(cellClicked), actionEditTree);
			}
		});
		scenarioTable.setLinesVisible(true);
		scenarioTable.setHeaderVisible(true);
		scenarioTable.setBounds(10, 10, 226, 504);
		TableColumn scenarioTableColumn1 = new TableColumn(scenarioTable, SWT.CENTER);
		TableColumn scenarioTableColumn2 = new TableColumn(scenarioTable, SWT.RIGHT);
		TableColumn scenarioTableColumn3 = new TableColumn(scenarioTable, SWT.CENTER);
		TableColumn scenarioTableColumn4 = new TableColumn(scenarioTable, SWT.LEFT);
		scenarioTableColumn1.setResizable(false);
		scenarioTableColumn1.setText("v");
		scenarioTableColumn2.setText("#");
		scenarioTableColumn3.setText("Protocol");
		scenarioTableColumn4.setText("Name");
		scenarioTableColumn1.setWidth(23);
		scenarioTableColumn2.setWidth(26);
		scenarioTableColumn3.setWidth(52);
		scenarioTableColumn4.setWidth(120);

		JSONParser parser = new JSONParser();
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get("E:\\VI\\LDX-QA\\scripts\\REST_test\\actions.json")));
			try {
				Object obj = parser.parse(content);
				JSONArray array = (JSONArray) obj;

				for (Object item : array) {
					ActionTemplate actionTemplate = new ActionTemplate(item);
					actionTemplates.add(actionTemplate);
				}
			} catch (ParseException pe) {
				System.out.println("position: " + pe.getPosition());
				System.out.println(pe);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Tree actionToolbox = new Tree(shlRestest, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		actionToolbox.setBounds(254, 347, 337, 167);
		actionToolbox.setHeaderVisible(true);
		TreeColumn toolboxColumn1 = new TreeColumn(actionToolbox, SWT.LEFT);
		toolboxColumn1.setText("Action");
		toolboxColumn1.setWidth(130);
		TreeColumn toolboxColumn2 = new TreeColumn(actionToolbox, SWT.CENTER);
		toolboxColumn2.setText("Protocol");
		toolboxColumn2.setWidth(85);
		actionTemplatesToTree(actionTemplates, actionToolbox);

		Label lblToolbox = new Label(shlRestest, SWT.NONE);
		lblToolbox.setBounds(254, 325, 49, 13);
		lblToolbox.setText("Toolbox");
		shlRestest.pack();

		Menu mainMenu = new Menu(shlRestest, SWT.BAR);
		shlRestest.setMenuBar(mainMenu);

		MenuItem mntmFile = new MenuItem(mainMenu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmOpenProject = new MenuItem(menu_1, SWT.NONE);
		mntmOpenProject.setText("Open project      Ctrl+O");
		mntmOpenProject.setAccelerator(SWT.MOD1 + 'O');

		mntmOpenProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("E:\\VI\\LDX-QA\\scripts\\REST_test\\Tests"));
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					openProject(selectedFile.getAbsolutePath(), scenario, shlRestest);
					scenarioToTable(scenario, actionTemplates, scenarioTable);
				}
			}
		});

		shlRestest.open();
		openProject("E:\\VI\\LDX-QA\\scripts\\REST_test\\Tests\\projectId.json", scenario, shlRestest);
		scenarioToTable(scenario, actionTemplates, scenarioTable);

	}
}
