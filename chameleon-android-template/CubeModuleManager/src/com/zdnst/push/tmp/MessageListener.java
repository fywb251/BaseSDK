package com.zdnst.push.tmp;

import android.content.Intent;

public interface MessageListener {

	public Intent convertMessage2Intent(Message message);

}
