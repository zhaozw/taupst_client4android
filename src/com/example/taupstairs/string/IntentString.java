package com.example.taupstairs.string;

public class IntentString {

	public static class RequestCode {
		public static final int LOGIN_SELECTCOLLEGE = 1;
		public static final int HOMEPAGE_WRITE = 2;
		public static final int TASKFRAGMENT_TASKDETAIL = 3;
		public static final int WRITE_SELECTENDTIME = 4;
		public static final int IMAGE_REQUEST_CODE = 5;
		public static final int CAMERA_REQUEST_CODE = 6;
		public static final int PHOTO_REQUEST_CODE = 7;
		public static final int MEFRAGMENT_UPDATAUSERDATABASE = 8;
		public static final int TASKDETAIL_SIGNUP = 10;
		public static final int INFOSIGNUP_INFOSIGNUPEXEC = 11;
		public static final int TASKBYID_SIGNUP = 12;
		public static final int SIGNUPLIST_EVALUATE = 13;
		public static final int MYRELEASESTATUS_TASKDETAIL = 14;
		public static final int MYSIGNUPSTATUS_TASKDETAIL = 15;
		public static final int INFOSIGNUP_TASKBYID = 16;
	}
	
	public static class ResultCode {
		public static final int SELECTCOLLEGE_LOGIN = 1;
		public static final int WRITE_HOMEPAGE = 2;
		public static final int TASKDETAIL_TASKFRAGMENT = 3;
		public static final int SELECTENDTIME_WRITE = 4;
		public static final int UPDATAUSERDATABASE_MEFRAGMENT_NICKNAME = 8;
		public static final int UPDATAUSERDATABASE_MEFRAGMENT_SIGNATURE = 9;
		public static final int SIGNUP_TASKDETAIL = 10;
		public static final int INFOSIGNUPEXEC_INFOSIGNUP = 11;
		public static final int SIGNUP_TASKBYID = 12;
		public static final int EVALUATE_SIGNUPLIST = 13;
		public static final int TASKDETAIL_MYRELEASESTATUS = 14;
		public static final int TASKDETAIL_MYSIGNUPSTATUS = 15;
		public static final int TASKBYID_INFOSIGNUP = 16;
	}
	
	public static class Extra {
		public static final String TYPE = "type";
		public static final String CONTENT = "content";
	}
	
	public static class Action {
		public static final String CHANGE_USER = "com.example.taupstairs.CHANGE_USER";
	}
}
