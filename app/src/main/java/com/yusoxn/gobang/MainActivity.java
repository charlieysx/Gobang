package com.yusoxn.gobang;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.bean.ComputerPlayer;
import com.yusoxn.gobang.bean.HumanPlayer;
import com.yusoxn.gobang.bean.IPlayer;
import com.yusoxn.gobang.bean.Player;
import com.yusoxn.gobang.view.GameView;

public class MainActivity extends AppCompatActivity {

    GameView mGameView;
    Button btnStart;

    private GameControl mControl;

    private Player[] players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mGameView = (GameView) findViewById(R.id.gv_wuqizi);
        btnStart = (Button) findViewById(R.id.btn_start);

        mControl = new GameControl(mGameView);

        players = new Player[2];
        players[0] = new HumanPlayer("小熊", ChessPoint.BLACK);
        players[1] = new ComputerPlayer("yusxon", ChessPoint.WHITE);
        mControl.setPlayers(players[0], players[1]);
        listener();
    }

    private void listener() {
        mControl.setOnGameListener(new GameControl.OnGameListener() {
            @Override
            public void onGameOver(final IPlayer winner) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setVisibility(View.VISIBLE);
                        if (null == winner) {
                            Toast.makeText(MainActivity.this, "和棋", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, winner.getPlayerName() + "赢", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mGameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        if(!mControl.calcRawXY(x, y)) {
                            String text = "AI下棋中";
                            if(btnStart.getVisibility() == View.VISIBLE) {
                                text = "请先点击开始";
                            }
                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);
                mControl.start();
            }
        });
    }
}
