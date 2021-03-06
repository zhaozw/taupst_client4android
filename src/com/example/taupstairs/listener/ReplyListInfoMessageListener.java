package com.example.taupstairs.listener;

import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.taupstairs.bean.MessageContent;
import com.example.taupstairs.ui.activity.InfoMessageActivity;

public class ReplyListInfoMessageListener implements OnItemClickListener {

	private InfoMessageActivity context;
	private List<MessageContent> contents;

	public ReplyListInfoMessageListener(InfoMessageActivity context, List<MessageContent> contents) {
		super();
		this.context = context;
		this.contents = contents;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String replyId = contents.get(arg2).getReplyId();
		String replyNickname = contents.get(arg2).getReplyNickname();
		context.changeEditHint(replyId, replyNickname);
	}

}
