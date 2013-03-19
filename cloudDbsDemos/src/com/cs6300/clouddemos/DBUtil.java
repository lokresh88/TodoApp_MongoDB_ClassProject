package com.cs6300.clouddemos;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.w3c.dom.html.HTMLUListElement;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**  List of Imports for Mongo DB **/

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import java.util.Arrays;

public class DBUtil {

	private static final Logger log = Logger.getLogger(DBUtil.class.getName());
	Mongo mongoObj;
	DB db;

	/*********** Managing DB CONNECTIONS ***************/

	public DBUtil() throws UnknownHostException {

		try{
		// object will be a connection to a MongoDB server for the specified
		// database.
		mongoObj = new Mongo("127.0.0.1", 27017);

		// get a intsance to db
		db = mongoObj.getDB(DBUtilConstants.DATABASE_TODO_LIST);
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}

	public void closeConnection() {
		mongoObj.close();
	}

	/*********** Managing USERS ***************/

	/**
	 * createUser Input : user details contained in the User object. Output :
	 * Status of the database transaction and new user details. Description :
	 * Adds a new user , creates default list and returns the new user id.
	 */

	public User createUser(User userObj) {

		// Create your BSON
		BasicDBObject doc = new BasicDBObject();
		doc.put(DBUtilConstants.FIELD_NAME, userObj.getName());
		doc.put(DBUtilConstants.FIELD_PASSWORD, userObj.getPwd());

		// insert into the database
		DBCollection coll = db.getCollection(DBUtilConstants.COLLECTION_USERS);
		coll.insert(doc);

		// get the Object ID of the doc inserted
		ObjectId id = (ObjectId) doc.get("_id");
		userObj.setId(id);
		return userObj;

	}

	/**
	 * Validate User Input : user details contained in the User object. Output :
	 * Status of the database transaction and login details. Description :
	 * Checks the login information.
	 */

	public ObjectId validateUserLogin(String name, String pwd) {

		BasicDBObject query = new BasicDBObject();
		ObjectId id = null;

		// Form the Mongo Query ..
		query.put(DBUtilConstants.FIELD_NAME, name);
		query.put(DBUtilConstants.FIELD_PASSWORD, pwd);

		// Get the document
		DBCollection coll = db.getCollection(DBUtilConstants.COLLECTION_USERS);
		DBObject doc = coll.findOne(query);

		// return the id
		if (doc.get(DBUtilConstants.FIELD_NAME) != null) {
			id = (ObjectId) doc.get("_id");
		}
		return id;
	}

	/**
	 * getUserByID Input : userid Output : User Object representing his
	 * information Description : gets the user by id.
	 */
	public boolean selectUser(String name) {
		BasicDBObject query = new BasicDBObject();
		ObjectId id = null;

		// Form the Mongo Query ..
		query.put(DBUtilConstants.FIELD_NAME, name);

		// Get the document
		DBCollection coll = db.getCollection(DBUtilConstants.COLLECTION_USERS);
		DBObject doc = coll.findOne(query);

		// return the id
		if (doc.get(DBUtilConstants.FIELD_NAME) != null) {
			return true;
		}
		return false;
	}

	/*********** Managing TODO TASKS ***************/

	/**
	 * createItem Input : item details contained in the ToDOItem object. Output
	 * : Status of the database transaction and new item details. Description :
	 * Adds a new item and returns the new item id.
	 */

	public ObjectId createItem(ToDoItem item) {

		// Create your Task BSON
		BasicDBObject task = new BasicDBObject();
		Calendar cld = Calendar.getInstance();
		Long timenow = cld.getTimeInMillis();
		task.put(DBUtilConstants.FIELD_TASK_USERID, item.getUserId());
		task.put(DBUtilConstants.FIELD_TASK_TODONAME, item.getName());
		task.put(DBUtilConstants.FIELD_TASK_TODONOTE, item.getNote());
		task.put(DBUtilConstants.FIELD_TASK_DUETIME, item.getDueTime());
		task.put(DBUtilConstants.FIELD_TASK_NODUETIME, item.isNoDueTime());
		task.put(DBUtilConstants.FIELD_TASK_PRIORITY, item.getPriority());
		task.put(DBUtilConstants.FIELD_TASK_CHECKED, item.isChecked());
		task.put(DBUtilConstants.FIELD_TASK_DELETED, false);
		task.put(DBUtilConstants.FIELD_TASK_MODIFIED, timenow);

		// insert into the database
		DBCollection coll = db
				.getCollection(DBUtilConstants.COLLECTION_TODOLIST);
		coll.insert(task);

		// get the Object ID of the Task inserted
		ObjectId id = (ObjectId) task.get("_id");
		return id;
	}

	public HashMap<String, ArrayList<ToDoItem>> getToDoListByUId(long userId) {

		try {

			// Task - Categories ..
			ArrayList<ToDoItem> pendingList = new ArrayList<ToDoItem>();
			ArrayList<ToDoItem> completedList = new ArrayList<ToDoItem>();
			ArrayList<ToDoItem> overDueList = new ArrayList<ToDoItem>();
			ArrayList<ToDoItem> pendingListNoDue = new ArrayList<ToDoItem>();
			
			
			// Form the Mongo Query ..
			BasicDBObject query = new BasicDBObject();
			query.put(DBUtilConstants.FIELD_TASK_USERID, userId);
			query.put(DBUtilConstants.FIELD_TASK_DELETED, false);

			
			// Form the SortBy Obj
			BasicDBObject sortBy = new BasicDBObject();
			sortBy.put(DBUtilConstants.FIELD_MODIFIEDDATE, 1);

			
			// Get the documents - tasks for the User , Sorted by date..
			DBCollection tasks = db.getCollection(DBUtilConstants.COLLECTION_TODOLIST);
			DBCursor cursor = tasks.find(query).sort(sortBy);

			
			// form the query and save the tasks into 3 Categories		
			while(cursor.hasNext()) {
				ToDoItem item =formTodoItem(cursor.next());
				if (item.isChecked()) {
					completedList.add(item);
				} else {
					Calendar cld = Calendar.getInstance();
					Date today = cld.getTime();
					Long currMillis = today.getTime();
					Long taskduedate = item.getDueTime();
					if (taskduedate != null && taskduedate != -1
							&& currMillis > taskduedate) {
						item.setMissed(true);
						overDueList.add(item);
					} else {
						if (taskduedate != null && taskduedate != -1)
							pendingList.add(item);
						else
							pendingListNoDue.add(item);
					}
				}

			}
			// form the result and return
			pendingList.addAll(pendingListNoDue);
			HashMap<String, ArrayList<ToDoItem>> tasksInTheList = new HashMap<String, ArrayList<ToDoItem>>();
			tasksInTheList.put("AllCompleted", completedList);
			tasksInTheList.put("UnCompleted", pendingList);
			tasksInTheList.put("OverdueList", overDueList);
			System.out.print(completedList);

			return tasksInTheList;
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}

	}

	/**
	 * formToDoItem Input : database result. Output : todoItem object.
	 * Description : converts the database result into the todoitem object.
	 */

	private ToDoItem formTodoItem(DBObject dbObj) {
		ToDoItem item = new ToDoItem();
		
		// Populate the item obj from the doc returned ..
		item.setId((ObjectId)dbObj.get("_id"));
		item.setUserId((ObjectId)dbObj.get(DBUtilConstants.FIELD_TASK_USERID));
		item.setName((String)dbObj.get(DBUtilConstants.FIELD_TASK_TODONAME));
		item.setNote((String)dbObj.get(DBUtilConstants.FIELD_TASK_TODONOTE));
		item.setDueTime((Long)dbObj.get(DBUtilConstants.FIELD_TASK_DUETIME));
		Long duetime = item.getDueTime();
		String dueDateStr = "";
		int hr = 0;
		int sc = 0;
		int min = 0;
		if (duetime != null && duetime != -1) {
			try {
				Calendar cld = Calendar.getInstance();
				cld.setTimeInMillis(duetime);
				min = cld.get(Calendar.MINUTE);
				hr = cld.get(Calendar.HOUR);
				sc = cld.get(Calendar.SECOND);
				SimpleDateFormat dformat = new SimpleDateFormat("dd-MMM-yyyy");
				dueDateStr = dformat.format(duetime);
			} catch (NullPointerException exp) {
				dueDateStr = "";
			}
		}
		item.setDuedatestr(dueDateStr);
		item.setDate_hr(hr);
		item.setDate_min(min);
		item.setDate_sec(sc);
		item.setNoDueTime((Boolean)dbObj.get(DBUtilConstants.FIELD_TASK_NODUETIME));
		item.setPriority((Long)dbObj.get(DBUtilConstants.FIELD_TASK_PRIORITY));
		item.setChecked((Boolean)dbObj.get(DBUtilConstants.FIELD_TASK_CHECKED));
		return item;
	}

	/**
	 * getItemByID Input : item ID. Output : item details in the ToDOItem
	 * Object. Description : gets the Item details from the database.
	 

	public ToDoItem getItemByItemId(long id) {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Entity taskent;
		ToDoItem item = new ToDoItem();
		// form the query
		try {
			taskent = datastore.get(KeyFactory.createKey(
					DBUtilConstants.COLLECTION_TODOLIST, id));
			item.setId(taskent.getKey().getId());
			item.setName((String) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_TODONAME));
			item.setChecked((Boolean) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_CHECKED));
			// item.set((String)
			// taskent.getProperty(DBUtilConstants.COLUMN_TASK_COMMENT));
			item.setDueTime(taskent
					.getProperty(DBUtilConstants.FIELD_TASK_DUETIME) != null ? (Long) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_DUETIME) : null);
			item.setMod_date((Long) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_MODIFIED));
			item.setNoDueTime((Boolean) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_NODUETIME));
			item.setPriority((Long) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_PRIORITY));
			item.setNote((String) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_TODONOTE));
			item.setUserId((Long) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_USERID));
			item.setIsdeleted((Boolean) taskent
					.getProperty(DBUtilConstants.FIELD_TASK_DELETED));

		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return item;
	}

	/**
	 * deleteItem Input : item details contained in the TodoItem object. Output
	 * : Status of the database transaction. Description : delete the given
	 * item.
	 * 
	 * 
	 * public void deleteItem(Long taskid, Long userid) { ToDoItem item =
	 * getItemByItemId(taskid); if (item.getUserId() != null &&
	 * item.getUserId().equals(userid)) { item.setIsdeleted(true);
	 * updateItem(item); } return; }
	 * 
	 * /** checkItem Input : item details contained in the TodoItem object.
	 * Output : Status of the database transaction. Description : mark an item
	 * as complete.
	 * 
	 * 
	 * public void completeItem(Long taskid, Long userid) { ToDoItem item =
	 * getItemByItemId(taskid); if (item.getUserId() != null &&
	 * item.getUserId().equals(userid)) { item.setChecked(true);
	 * updateItem(item); } return; }
	 * 
	 * /** updateItem Input : item details contained in the todoItem object.
	 * Output : Status of the database transaction. Description : update item.
	 * 
	 * 
	 * public ToDoItem updateItem(ToDoItem item) { Calendar cld =
	 * Calendar.getInstance(); Long timenow = cld.getTimeInMillis();
	 * DatastoreService datastore = DatastoreServiceFactory
	 * .getDatastoreService(); Entity task = new
	 * Entity(DBUtilConstants.COLLECTION_TODOLIST, item.getId());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_USERID, item.getUserId());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_TODONAME, item.getName());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_TODONOTE, item.getNote());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_DUETIME, item.getDueTime());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_NODUETIME,
	 * item.isNoDueTime());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_PRIORITY,
	 * item.getPriority()); task.setProperty(DBUtilConstants.FIELD_TASK_CHECKED,
	 * item.isChecked()); task.setProperty(DBUtilConstants.FIELD_TASK_DELETED,
	 * item.isIsdeleted());
	 * task.setProperty(DBUtilConstants.FIELD_TASK_MODIFIED, timenow);
	 * datastore.put(task); return item; }
	 * 
	 * /** getListOfItemsForUser Input : user details contained in the GtodoUser
	 * object. Output : Status of the database transaction and list of tasks
	 * details. Description : get the items for the user.
	 */

	public static JSONObject getJSONObject(ToDoItem item) throws JSONException {
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("name", item.getName());
		jsonobj.put("date", item.getDuedatestr());
		jsonobj.put("prior", item.getPriority());
		jsonobj.put("hr", item.getDate_hr());
		jsonobj.put("sec", item.getDate_sec());
		jsonobj.put("min", item.getDate_min());
		jsonobj.put("notes", item.getNote());
		jsonobj.put("nodue", item.isNoDueTime() ? "false" : "checked");
		jsonobj.put("taskid", item.getId());
		return jsonobj;
	}

}
