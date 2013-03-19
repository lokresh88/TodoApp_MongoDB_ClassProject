package com.cs6300.clouddemos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.bson.types.ObjectId;
import org.mortbay.util.ajax.JSON;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class TaskOperations extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(GetTasks.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv = req.getSession();
		JSONObject jsobj = new JSONObject();

		String username = (String) serv.getAttribute("loggedinUserName");
		ObjectId userid = (ObjectId) serv.getAttribute("loggedinUserId");

		String action = req.getParameter("action") != null ? req
				.getParameter("action") : null;
				System.out.print("test");

		if (action != null && (action.equalsIgnoreCase("addTask") || action.equalsIgnoreCase("editTask"))) {

			String name = URLDecoder.decode(req.getParameter("name"));
			String note = URLDecoder.decode(req.getParameter("notes"));
			String duedate = URLDecoder.decode(req.getParameter("duedate"));
			String timecomp = URLDecoder.decode(req.getParameter("timeComp"));
			Date dueDateVal = null;
			Boolean noduedate = req.getParameter("noduedate") != null ? !Boolean
					.parseBoolean(req.getParameter("noduedate")) : true;
			Long prior = req.getParameter("priority") != null ? Long
					.parseLong(req.getParameter("priority")) : 0;
			if (duedate != null) {
				try {
					DateFormat formatter;
					formatter = new SimpleDateFormat("dd-MMM-yyyy H:mm:ss");
					dueDateVal = (Date) formatter.parse(duedate + " "
							+ timecomp);
				} catch (Exception exp) {
					exp.printStackTrace();
					noduedate = true;
				}
			} else {
				noduedate = true;
			}
			Calendar cld = Calendar.getInstance();

			long timenow = cld.getTimeInMillis();
			ObjectId newedittaskid = null;
			ToDoItem ins;
			boolean editmode = false;
			DBUtil dbutil = new DBUtil();
			boolean taskchecked =false;

			
			if(action.equalsIgnoreCase("editTask")){
				editmode=true;
			//	newedittaskid = req.getParameter("id")!=null?ObjectId(req.getParameter("id")):null;
				if(newedittaskid!=null){
				//	ins = dbutil.getItemByItemId(newedittaskid);
				//	taskchecked = ins.isChecked();
				}
			}
			if (!noduedate) {
				if (timenow > dueDateVal.getTime() && (!editmode || !taskchecked)) {
					try {
						jsobj.put("status", false);
						jsobj.put("errorMessage",
								"Due date must be greater than current time.");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					PrintWriter pout = resp.getWriter();
					pout.write(jsobj.toString());
					return;
				}
			}
			ToDoItem taskObj = new ToDoItem();
			taskObj.setName(name);
			taskObj.setDueTime((!noduedate && dueDateVal != null) ? dueDateVal
					.getTime() : -1);
			taskObj.setChecked(false);
			taskObj.setMissed(false);
			taskObj.setNoDueTime(noduedate);
			taskObj.setNote(note);
			taskObj.setPriority(prior);
			taskObj.setUserId(userid);
			
			if(action.equalsIgnoreCase("editTask")){
				if(newedittaskid!=null){
				//	ins = dbutil.getItemByItemId(newedittaskid);
			//		taskObj.setId(newedittaskid);
			//		taskObj.setChecked(ins.isChecked());
			//		if(ins.getUserId()!=null && ins.getUserId().equals(userid)){
				//		dbutil.updateItem(taskObj);
			//		}
				try {
					jsobj.put("status", true);
					jsobj.put("taskId", newedittaskid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				}
				PrintWriter pout = resp.getWriter();
				pout.write(jsobj.toString());
			}else{
			
			ObjectId newtaskid = dbutil.createItem(taskObj);
			try {
				jsobj.put("status", true);
				jsobj.put("taskId", newtaskid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			PrintWriter pout = resp.getWriter();
			pout.write(jsobj.toString());
			}
		} else if (action != null && action.equalsIgnoreCase("editTask")) {
			/*
			 * userObj = dbutil.createUser(userObj); try { HttpSession serv =
			 * req.getSession(); jsobj.put("status", true); jsobj.put("userID",
			 * userObj.getId()); serv.setAttribute("loggedinUserName", name);
			 * serv.setAttribute("loggedinUserId", userObj.getId()); } catch
			 * (JSONException e) { e.printStackTrace(); }
			 */
		} else if (action != null && action.equalsIgnoreCase("completeTask")) {
			Long taskid = req.getParameter("taskId") != null ? Long
					.parseLong(req.getParameter("taskId")) : null;
					System.out.print("test");

			if (taskid != null) {
				DBUtil dbutil = new DBUtil();
		//		dbutil.completeItem(taskid, userid);
			}
		}else if (action != null && action.equalsIgnoreCase("deleteTask")) {
			Long taskid = req.getParameter("taskId") != null ? Long
					.parseLong(req.getParameter("taskId")) : null;
			if (taskid != null) {
				DBUtil dbutil = new DBUtil();
		//		dbutil.deleteItem(taskid, userid);
			}
		}

	}
}
