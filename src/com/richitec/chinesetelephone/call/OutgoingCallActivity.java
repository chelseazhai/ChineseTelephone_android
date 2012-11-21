package com.richitec.chinesetelephone.call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;*/

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.richitec.chinesetelephone.R;

public class OutgoingCallActivity extends Activity {

	/*private static final String LOG_TAG = "OutgoingCallActivity";

	// outgoing call activity onCreate param key
	public static final String OUTGOING_CALL_PHONE = "outgoing_call_phone";
	public static final String OUTGOING_CALL_OWNERSHIP = "outgoing_call_ownership";
	public static final String OUTGOING_CALL_SIPSESSIONID = "outgoing_call_sipSessionId";

	// doubango ngnEngine instance
	private final NgnEngine NGN_ENGINE = NgnEngine.getInstance();

	// outgoing call phone number
	private String _mCallerPhone;

	// doubango ngn audio/video session
	private NgnAVSession _mAVSession;

	// doubango ngn audio/video session state broadcast receiver
	private BroadcastReceiver _mAVSessionStateBroadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// keep outgoing call activity screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// set content view
		setContentView(R.layout.outgoing_call_activity_layout);

		// get the intent parameter data
		Bundle _data = getIntent().getExtras();

		// check the data bundle and get call phone
		if (null != _data) {
			// init outgoing call phone
			if (null != _data.getString(OUTGOING_CALL_PHONE)) {
				_mCallerPhone = _data.getString(OUTGOING_CALL_PHONE);
			}

			// set callee textView text
			((TextView) findViewById(R.id.callee_textView))
					.setText(null != _data.getString(OUTGOING_CALL_OWNERSHIP) ? _data
							.getString(OUTGOING_CALL_OWNERSHIP) : _mCallerPhone);

			// init doubango ngn audio/video session
			_mAVSession = NgnAVSession.getSession(_data
					.getLong(OUTGOING_CALL_SIPSESSIONID));

			// check the session
			if (null == _mAVSession) {
				Log.e(LOG_TAG, "Doubango ngn audio/video session is null");

				// delayed one second to back
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// update call state textView text
						((TextView) findViewById(R.id.callState_textView))
								.setText(R.string.outgoing_call_failed);

						// finish outgoing call activity
						finish();
					}
				}, 1000);
			} else {
				// increase doubango ngn audio/video session reference and set
				// context
				_mAVSession.incRef();

				_mAVSession.setContext(this);
			}
		}

		// init doubango ngn audio/video session state broadcast receiver
		_mAVSessionStateBroadcastReceiver = new AVSessionStateBroadcastReceiver();

		// add doubango ngn audio/video session state changed listener
		IntentFilter _intentFilter = new IntentFilter();
		_intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);

		registerReceiver(_mAVSessionStateBroadcastReceiver, _intentFilter);

		// set wallpaper as outgoing call background
		((ImageView) findViewById(R.id.outgoingcall_background_imageView))
				.setImageDrawable(getWallpaper());

		// get call controller gridView
		GridView _callControllerGridView = (GridView) findViewById(R.id.callController_gridView);

		// set call controller gridView adapter
		_callControllerGridView.setAdapter(generateCallControllerAdapter());

		// set call controller gridView on item click listener
		_callControllerGridView
				.setOnItemClickListener(new CallControllerGridViewOnItemClickListener());

		// bind hangup outgoing call button on click listener
		((ImageButton) findViewById(R.id.hangup_button))
				.setOnClickListener(new HangupOutgoingCallBtnOnClickListener());

		// bind hide keyboard button on click listener
		((ImageButton) findViewById(R.id.hideKeyboard_button))
				.setOnClickListener(new HideKeyboardBtnOnClickListener());
	}

	@Override
	public void onBackPressed() {
		// nothing to do
	}

	@Override
	protected void onDestroy() {
		// release doubango ngn audio/video session state broadcast receiver
		if (null != _mAVSessionStateBroadcastReceiver) {
			unregisterReceiver(_mAVSessionStateBroadcastReceiver);

			_mAVSessionStateBroadcastReceiver = null;
		}

		// release doubango ngn audio/video session
		if (null != _mAVSession) {
			_mAVSession.setContext(null);

			_mAVSession.decRef();
		}

		super.onDestroy();
	}

	// generate call controller adapter
	private ListAdapter generateCallControllerAdapter() {
		// call controller item adapter data key
		final String CALL_CONTROLLER_ITEM_BACKGROUND = "call_controller_item_background";
		final String CALL_CONTROLLER_ITEM_ICON = "call_controller_item_icon";
		final String CALL_CONTROLLER_ITEM_LABEL = "call_controller_item_label";

		// define call controller gridView content
		final int[][] _callControllerGridViewContentArray = new int[][] {
				{ R.drawable.callcontroller_contactitem_bg,
						R.drawable.img_callcontroller_contactitem_normal,
						R.string.callController_contactItem_text },
				{ R.drawable.callcontroller_keyboarditem_bg,
						R.drawable.img_callcontroller_keyboarditem_normal,
						R.string.callController_keyboardItem_text },
				{ R.drawable.callcontroller_muteitem_bg,
						R.drawable.img_callcontroller_muteitem_normal,
						R.string.callController_muteItem_text },
				{ R.drawable.callcontroller_handfreeitem_bg,
						R.drawable.img_callcontroller_handfreeitem_normal,
						R.string.callController_handfreeItem_text } };

		// set call controller data list
		List<Map<String, ?>> _callControllerDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < _callControllerGridViewContentArray.length; i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// put value
			_dataMap.put(CALL_CONTROLLER_ITEM_BACKGROUND,
					_callControllerGridViewContentArray[i][0]);
			_dataMap.put(CALL_CONTROLLER_ITEM_ICON,
					_callControllerGridViewContentArray[i][1]);
			_dataMap.put(CALL_CONTROLLER_ITEM_LABEL,
					_callControllerGridViewContentArray[i][2]);

			// add data to list
			_callControllerDataList.add(_dataMap);
		}

		return new OutgoingCallControllerAdapter(
				this,
				_callControllerDataList,
				R.layout.call_controller_item,
				new String[] { CALL_CONTROLLER_ITEM_BACKGROUND,
						CALL_CONTROLLER_ITEM_ICON, CALL_CONTROLLER_ITEM_LABEL },
				new int[] { R.id.callController_item_relativeLayout,
						R.id.callController_item_iconImgView,
						R.id.callController_item_labelTextView });
	}

	// get outgoing call invite state string
	private String getOutgoingCallInviteState(InviteState inviteState) {
		String _inviteStateString = "";

		// check invite state
		switch (inviteState) {
		case INPROGRESS:
			_inviteStateString = getResources().getString(
					R.string.outgoing_call_trying);
			break;
		case REMOTE_RINGING:
		case EARLY_MEDIA:
			_inviteStateString = getResources().getString(
					R.string.outgoing_call_earlyMedia7RemoteRing);
			break;
		case INCALL:
			_inviteStateString = "speaking ...";
			break;
		case TERMINATING:
			_inviteStateString = getResources().getString(
					R.string.end_outgoing_call);
			break;
		case TERMINATED:
			_inviteStateString = getResources().getString(
					R.string.outgoing_call_ended);
			break;
		case NONE:
		default:
			_inviteStateString = getResources().getString(
					R.string.outgoing_call_unknownState);
			break;
		}

		return _inviteStateString;
	}

	// inner class
	// doubango ngn audio/video session state broadcast receiver
	class AVSessionStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// check ngn audio/video session
			if (null == _mAVSession) {
				Log.e(LOG_TAG, "Doubango ngn audio/video session is null");
			} else {
				// get the action
				String _action = intent.getAction();

				// check the action for ngn invite event
				if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(_action)) {
					// get ngn invite event arguments
					NgnInviteEventArgs _ngnInviteEventArgs = intent
							.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);

					// check the arguments
					if (null == _ngnInviteEventArgs) {
						Log.e(LOG_TAG,
								"Doubango ngn invite event arguments is null");
					} else if (_mAVSession.getId() != _ngnInviteEventArgs
							.getSessionId()) {
						Log.e(LOG_TAG,
								"Doubango ngn audio/video session invalid");
					} else {
						// get the ngn invite state
						InviteState _inviteState = _mAVSession.getState();

						Log.d(LOG_TAG,
								"AVSessionStateBroadcastReceiver on receive invite state = "
										+ _inviteState);

						// update call state textView text
						((TextView) findViewById(R.id.callState_textView))
								.setText(getOutgoingCallInviteState(_inviteState));

						// check invite state
						switch (_inviteState) {
						case REMOTE_RINGING:
							NGN_ENGINE.getSoundService().startRingBackTone();
							break;
						case EARLY_MEDIA:
						case INCALL:
							NGN_ENGINE.getSoundService().stopRingBackTone();

							_mAVSession.setSpeakerphoneOn(false);
							break;
						case TERMINATING:
						case TERMINATED:
							NGN_ENGINE.getSoundService().stopRingBackTone();

							// finish outgoing call activity
							finish();

							break;
						default:
							// ??
							break;
						}
					}
				}
			}
		}

	}

	// call controller gridView on item click listener
	class CallControllerGridViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(LOG_TAG,
					"call controller gridView on item click listener, parent = "
							+ parent + ", view = " + view + ", position = "
							+ position + " and id = " + id);

			//
			// ??
		}

	}

	// hangup outgoing call button on click listener
	class HangupOutgoingCallBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// check doubango ngn audio/video session
			if (null != _mAVSession) {
				_mAVSession.hangUpCall();
			} else {
				Log.e(LOG_TAG,
						"Doubango ngn audio/video session is null, force finish outgoing call activity");

				// finish outgoing call activity
				finish();
			}
		}

	}

	// hide keyboard button on click listener
	class HideKeyboardBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "hide keyboard and view = " + v);

			//
			// ??
		}

	}*/

}
