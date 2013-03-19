package com.cs6300.clouddemos;

import java.io.Serializable;

import org.bson.types.ObjectId;


public class ToDoItem implements Serializable {
	private static final long serialVersionUID = 2L;
	
	private ObjectId id = null;
	private long global_id=-1;
	private ObjectId userId;
	private String name;
	private String note;
	private Long dueTime = 1000000L;
	private boolean checked;
	private boolean noDueTime;
	private long priority = 0;
	private long mod_date;
	private String duedatestr;
	private boolean isdeleted=false;
	private int date_hr,date_sec,date_min = 0;

	// static constants used in code
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_HIGH = 2;
	public static final int PRIORITY_LOW = 1;
	public static final int TASK_COMPLETE = 1;
	public static final int TASK_PENDING = 0;

	private boolean missed = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return name;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getDueTime() {
		return dueTime;
	}

	public void setDueTime(Long dueTime) {
		this.dueTime = dueTime;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isNoDueTime() {
		return noDueTime;
	}

	public void setNoDueTime(boolean noDueTime) {
		this.noDueTime = noDueTime;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public long getMod_date() {
		return mod_date;
	}

	public void setMod_date(long mod_date) {
		this.mod_date = mod_date;
	}

	public boolean isMissed() {
		return missed;
	}

	public void setMissed(boolean missed) {
		this.missed = missed;
	}

	public String getDuedatestr() {
		return duedatestr;
	}

	public void setDuedatestr(String duedatestr) {
		this.duedatestr = duedatestr;
	}

	public boolean isIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(boolean isdeleted) {
		this.isdeleted = isdeleted;
	}
	
	

	public int getDate_hr() {
		return date_hr;
	}

	public void setDate_hr(int date_hr) {
		this.date_hr = date_hr;
	}

	public int getDate_sec() {
		return date_sec;
	}

	public void setDate_sec(int date_sec) {
		this.date_sec = date_sec;
	}

	public int getDate_min() {
		return date_min;
	}

	public void setDate_min(int date_min) {
		this.date_min = date_min;
	}

	public long getGlobal_id() {
		return global_id;
	}

	public void setGlobal_id(long global_id) {
		this.global_id = global_id;
	}
}
