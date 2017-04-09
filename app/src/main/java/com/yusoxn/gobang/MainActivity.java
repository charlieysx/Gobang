package com.yusoxn.gobang;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.bean.player.BasePlayer;
import com.yusoxn.gobang.bean.player.ComputerPlayer;
import com.yusoxn.gobang.bean.player.HumanPlayer;
import com.yusoxn.gobang.interfaces.IPlayer;
import com.yusoxn.gobang.view.GameView;

public class MainActivity extends AppCompatActivity {

    GameView mGameView;
    Button btnStart;

    private GameControl mControl;

    private AlertDialog dialog;

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

        BasePlayer[] players = new BasePlayer[2];
        players[0] = new ComputerPlayer("小熊", ChessPoint.BLACK);
        players[1] = new HumanPlayer("yusxon", ChessPoint.WHITE);
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
                            showDialog("游戏结束", "和棋");
                        } else {
                            showDialog("游戏结束", winner.getPlayerName() + "赢");
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
                        if(!mControl.clickXY(x, y)) {
                            String text = "AI下棋中";
                            if(btnStart.getVisibility() == View.VISIBLE) {
                                text = "请先点击开始";
                            }
                            showDialog("提示", text);
                        }
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

    private void showDialog(String title, String message) {
        if(null != dialog && dialog.isShowing()) {
            return;
        }
        if(null == dialog) {
            dialog = new AlertDialog.Builder(this).create();
        }
        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.show();
    }
}
