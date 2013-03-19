package com.cs6300.clouddemos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class GetTasks extends HttpServlet {
	//private static final Logger log = Logger.getLogger(GetTasks.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession serv = req.getSession();
		String username = (String) serv.getAttribute("loggedinUserName");
		Long userid = (Long) serv.getAttribute("loggedinUserId");

		ArrayList<ToDoItem> tasks = new ArrayList<ToDoItem>();
		DBUtil dbutil = new DBUtil();
		HashMap<String, ArrayList<ToDoItem>> tasksInTheList= new HashMap<String, ArrayList<ToDoItem>>();
		tasksInTheList= dbutil.getToDoListByUId(userid);
		if(tasksInTheList.size() > 0) {
			ArrayList<ToDoItem> completedList = (ArrayList<ToDoItem>) tasksInTheList.get("AllCompleted");
			ArrayList<ToDoItem> pendingList = (ArrayList<ToDoItem>)tasksInTheList.get("UnCompleted");
			ArrayList<ToDoItem> overDueList = (ArrayList<ToDoItem>)tasksInTheList.get("OverdueList");
			req.setAttribute("completedList", completedList);
			req.setAttribute("pendingList", pendingList);
			req.setAttribute("overDueList", overDueList);
			System.out.print(completedList);

		}
		//req.setAttribute("tasks", tasksInTheList);
		RequestDispatcher rd = req.getRequestDispatcher("jsps/getTasks.jsp");
		resp.setHeader("Cache-Control", "no-cahce");
		resp.setHeader("Expires", "0");
		rd.forward(req, resp);
	}
}
