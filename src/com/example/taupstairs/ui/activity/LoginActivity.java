package com.example.taupstairs.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taupstairs.R;
import com.example.taupstairs.bean.College;
import com.example.taupstairs.bean.Task;
import com.example.taupstairs.bean.User;
import com.example.taupstairs.logic.ItaActivity;
import com.example.taupstairs.logic.MainService;
import com.example.taupstairs.services.InfoService;
import com.example.taupstairs.services.MyReleaseStatusService;
import com.example.taupstairs.services.MySignUpStatusService;
import com.example.taupstairs.services.RankService;
import com.example.taupstairs.services.StatusService;
import com.example.taupstairs.string.IntentString;
import com.example.taupstairs.string.JsonString;
import com.example.taupstairs.toast.LoginToast;
import com.example.taupstairs.util.SharedPreferencesUtil;

public class LoginActivity extends Activity implements ItaActivity {

	private Button btn_login, btn_captcha;
	private String userId, studentId, password;
	private String collegeId, collegeName, captchaUrl;
	private ProgressDialog progressDialog;
	private TextView txt_college_name, txt_about, txt_server;
	private EditText edit_studentid, edit_password, edit_captcha;
	private ImageView img_captcha;
	private boolean hasGetCaptcha = false;
	private boolean isExist = false;
	private boolean isRefresh = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		MainService.addActivity(this);	//将自己添加到activitys链表里面去
		init();
	}

	@Override
	public void init() {
		View view = findViewById(R.id.layout_login_college);
		txt_college_name = (TextView)findViewById(R.id.txt_college_name);
		edit_studentid = (EditText)findViewById(R.id.edit_studentid);
		edit_password = (EditText)findViewById(R.id.edit_password);
		btn_login = (Button)findViewById(R.id.btn_login);
		img_captcha = (ImageView)findViewById(R.id.img_captcha);
		btn_captcha = (Button)findViewById(R.id.btn_refresh_captcha);
		edit_captcha = (EditText)findViewById(R.id.edit_captcha);
		txt_about = (TextView)findViewById(R.id.txt_about_us);
		txt_server = (TextView)findViewById(R.id.txt_server);
		progressDialog = new ProgressDialog(this);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SelectCollegeActivity.class);
				startActivityForResult(intent, IntentString.RequestCode.LOGIN_SELECTCOLLEGE);
			}
		});
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txt_college_name.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "请选择学校", Toast.LENGTH_SHORT).show();
				} else if (edit_studentid.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "请输入学号", Toast.LENGTH_SHORT).show();
				} else if (edit_password.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "请输入教务系统密码", Toast.LENGTH_SHORT).show();
				} else {
					studentId = edit_studentid.getText().toString();
					password = edit_password.getText().toString();
					if (hasGetCaptcha) {
						doLoginTask();
					} else {
						doCheckUserTask();
					}	
				}
			}
		});
		txt_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, AboutUsActivity.class);
				startActivity(intent);
			}
		});
		txt_server.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, ServerDeclareActivity.class);
				startActivity(intent);
			}
		});
		btn_captcha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCheckUserTask();
			}
		});
	}
	
	private void showProgressDialog() {
		progressDialog.setCancelable(false);
		progressDialog.setMessage("    稍等片刻...");
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doCheckUserTask() {
		if (!isRefresh) {
			isRefresh = true;
			showProgressDialog();
			HashMap<String, Object> taskParams = new HashMap<String, Object>(2);
			taskParams.put(College.COLLEGE_ID, collegeId);
			taskParams.put(User.USER_STUDENTID, studentId);
			Task task = new Task(Task.TA_CHECKUSER, taskParams);
			MainService.addTask(task);
		}
	}

	private void doGetCaptchaTask() {
		if (!isRefresh) {
			isRefresh = true;
			HashMap<String, Object> taskParams = new HashMap<String, Object>(1);
			taskParams.put(Task.TA_GETCAPTCHA_CAPTCHAURL, captchaUrl);
			Task task = new Task(Task.TA_GETCAPTCHA, taskParams);
			MainService.addTask(task);
		}
	}
	
	/*登录放到后台处理*/
	private void doLoginTask() {
		if (!isRefresh) {
			isRefresh = true;			
			Map<String, Object> taskParams = new HashMap<String, Object>();
			if (!isExist && hasGetCaptcha) {
				EditText editText = (EditText) findViewById(R.id.edit_captcha);
				String captcha = editText.getText().toString().trim();
				if (captcha.equals("")) {
					isRefresh = false;
					Toast.makeText(LoginActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
					return;
				} 
				taskParams.put(Task.TA_LOGIN_CAPTCHA, captcha);
			}
			showProgressDialog();
			taskParams.put(User.USER_COLLEGEID, collegeId);
			taskParams.put(User.USER_STUDENTID, studentId);
			taskParams.put(User.USER_PASSWORD, password);
			Task task = new Task(Task.TA_LOGIN, taskParams);
			MainService.addTask(task);
		}
	}

	@Override
	public void refresh(Object... params) {	
		dismissProgressDialog();
		isRefresh = false;
		if (null == params[1]) {
			Toast.makeText(LoginActivity.this, "没网络啊！！！亲", Toast.LENGTH_SHORT).show();
		} else {
			int taskId = (Integer) params[0];
			switch (taskId) {
			case Task.TA_CHECKUSER:	
				String checkuser = ((String) params[1]).trim();
				refreshCheckUser(checkuser);
				break;
				
			case Task.TA_GETCAPTCHA:
				Drawable drawable = (Drawable) params[1];
				refreshGetCaptcha(drawable);
				break;
				
			case Task.TA_LOGIN:
				String login = ((String) params[1]).trim();	//这里的字符串要去空格，不然很可能不会equals
				refreshLogin(login);
				break;

			default:
				break;
			}
		}
	}
	
	private void refreshCheckUser(String checkuser) {
		try {
			JSONObject jsonObject = new JSONObject(checkuser);
			String state = jsonObject.getString(JsonString.Return.STATE).trim();
			if (state.equals(JsonString.Return.STATE_OK)) {
				isExist = true;
				doLoginTask();
			} else {
				isExist = false;	
				if (jsonObject.isNull(JsonString.Login.CAPTCHA)) {
					doLoginTask();
				} else {
					captchaUrl = jsonObject.getString(JsonString.Login.CAPTCHA).trim();
					Toast.makeText(LoginActivity.this, 
							"第一次登录需同步教务系统\n请填写验证码后再点击登录", 
							Toast.LENGTH_LONG).show();
					doGetCaptchaTask();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshGetCaptcha(Drawable drawable) {
		hasGetCaptcha = true;
		edit_captcha.setText(null);
		img_captcha.setImageDrawable(drawable);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_login_captcha);
		layout.setVisibility(View.VISIBLE);
	}
	
	private void refreshLogin(String result) {
		try {
			JSONObject loginJsonObject = new JSONObject(result);
			String isLogined = loginJsonObject.getString(JsonString.Login.IS_LOGINED).trim();
			if(isLogined.equals(Task.TA_FALSE)) {
				hasGetCaptcha = false;
				int state = Integer.parseInt(loginJsonObject.getString(JsonString.Login.STATE).trim());
				LoginToast.testState(LoginActivity.this, state);
			} else if (isLogined.equals(Task.TA_TRUE)) {
				userId = loginJsonObject.getString(JsonString.Login.USERS_ID);
				beforeJump();
				if (isExist) {
					jumpToHomePage();
				} else {
					jumpToCompleteUserdata();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void beforeJump() {
		User user = new User(userId, collegeId, collegeName, studentId, password);
		/*至关重要的一步，保存后下次会自动跳到主页面*/
		SharedPreferencesUtil.saveDefaultUser(LoginActivity.this, user);
		InfoService infoService = new InfoService(this);
		infoService.emptyInfoDb();
		infoService.closeDBHelper();
		StatusService statusService = new StatusService(LoginActivity.this);
		statusService.emptyStatusDb();
		statusService.closeDBHelper();
		MyReleaseStatusService myReleaseStatusService = new MyReleaseStatusService(this);
		myReleaseStatusService.emptyStatusDb();
		myReleaseStatusService.closeDBHelper();
		MySignUpStatusService mySignUpStatusService = new MySignUpStatusService(this);
		mySignUpStatusService.emptyStatusDb();
		mySignUpStatusService.closeDBHelper();
		RankService rankService = new RankService(this);
		rankService.emptyRankDb();
		rankService.closeDBHelper();
	}
	
	/*跳转到主页面去*/
	private void jumpToHomePage() {			
		Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void jumpToCompleteUserdata() {
		Intent intent = new Intent(LoginActivity.this, CompleteUserdataActivity.class);
		startActivity(intent);
		finish();
	}
	
	/*接收Intent返回的数据*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (IntentString.RequestCode.LOGIN_SELECTCOLLEGE == requestCode) {
			if (IntentString.ResultCode.SELECTCOLLEGE_LOGIN == resultCode) {
				collegeId = data.getStringExtra(College.COLLEGE_ID);
				collegeName = data.getStringExtra(College.COLLEGE_NAME);
				txt_college_name.setText(collegeName);
				LoginToast.checkCollege(this, Integer.parseInt(collegeId));
			}
		}
	}
	
	/*不重写这个方法，在退出的时候杀死进程的话，
	 * 会导致没有完全杀死程序的，会残留哪些我也不太清楚
	 * 使得手机在没有清空缓存的时候，再一次打开软件，
	 * 会出现后台的MainService调用UI线程中的refresh函数不能更新UI的情况*/
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainService.removeActivity(this);
	}
	
}
