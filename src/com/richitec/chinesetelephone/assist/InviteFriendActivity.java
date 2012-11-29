package com.richitec.chinesetelephone.assist;

import java.util.HashMap;
import com.richitec.chinesetelephone.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class InviteFriendActivity extends NavigationActivity {
	private String inviteLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_invite_friend_layout);
        
        inviteLink = getIntent().getStringExtra("inviteLink");
		Log.d("inviteLink", inviteLink);
        
        setTitle(R.string.invite_friend_title);
    }
    
    public void smsInvite(View v){
    	HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("inviteLink", inviteLink);
		pushActivity(ContactLisInviteFriendActivity.class,params);
    }
}
