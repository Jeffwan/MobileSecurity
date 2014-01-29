package edu.pitt.mobilesecurity.ui;

import edu.pitt.mobilesecurity.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_content;
	private CheckBox cb_status;
	private String title;
	private String content_on;
	private String content_off;
	
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		TypedArray a= context.obtainStyledAttributes(attrs, R.styleable.setting_view);
		title = a.getString(R.styleable.setting_view_title);
		content_on = a.getString(R.styleable.setting_view_content_on);
		content_off = a.getString(R.styleable.setting_view_content_off);
		setTitle(title);
		if(isChecked()) {
			setContent(content_on);
		} else {
			setContent(content_off);
		}
		a.recycle();
	}

	public SettingView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
		View view = View.inflate(context, R.layout.ui_setting_view, this);
		tv_title = (TextView) view.findViewById(R.id.tv_setting_view_title);
		tv_content = (TextView) view.findViewById(R.id.tv_setting_view_content);
		cb_status = (CheckBox) view.findViewById(R.id.cb_setting_view_status);
		
		this.setBackgroundResource(R.drawable.status_selector);
	}
	
	public void setTitle(String text) {
		tv_title.setText(text);
	}
	
	public void setContent(String text) {
		tv_content.setText(text);
	}
	
	public void setChecked(boolean checked) {
		cb_status.setChecked(checked);
		if(checked) {
			setContent(content_on);
		} else {
			setContent(content_off);
		}
	}
	
	public boolean isChecked() {
		return cb_status.isChecked();
	}
}
