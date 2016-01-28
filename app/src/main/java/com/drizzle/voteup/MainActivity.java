package com.drizzle.voteup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.drizzle.voteupanim.VoteUpView;

public class MainActivity extends AppCompatActivity {
	boolean flag = true;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final VoteUpView voteUpView = (VoteUpView) findViewById(R.id.circleRipple);
		voteUpView.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (flag) {
					voteUpView.voteUp();
					flag = false;
				} else {
					voteUpView.voteDown();
					flag = true;
				}
			}
		});
	}
}
