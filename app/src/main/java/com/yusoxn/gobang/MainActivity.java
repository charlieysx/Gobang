package com.yusoxn.gobang;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.bean.HumanPlayer;
import com.yusoxn.gobang.bean.IPlayer;
import com.yusoxn.gobang.view.GameView;

public class MainActivity extends AppCompatActivity {

    GameView mGameView;

    private GameControl mControl;

    private HumanPlayer[] players;

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
        mControl = new GameControl(mGameView);

        players = new HumanPlayer[2];
        players[0] = new HumanPlayer("小熊", ChessPoint.BLACK);
        players[1] = new HumanPlayer("yusxon", ChessPoint.WHITE);
        mControl.setPlayers(players[0], players[1]);
        listener();
        mControl.start();
    }

    private void listener() {
        mControl.setOnGameListener(new GameControl.OnGameListener() {
            @Override
            public void onGameOver(IPlayer winner) {
                Toast.makeText(MainActivity.this, winner.getPlayerName() + "赢", Toast.LENGTH_SHORT).show();
            }
        });
        mGameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        mControl.calcRawXY(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });
    }
}
