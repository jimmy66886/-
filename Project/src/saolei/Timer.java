package saolei;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.TimerTask;

//计时器
public class Timer extends JButton implements Serializable {
    private static final long serialVersionUID = -3451745779567892274L;
    private int currTime = 0;
    private boolean isPause = false;

    //有命名冲突
    transient java.util.Timer t;

    public Timer(){
        this.setPreferredSize(new Dimension(90, 50));
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setText(String.format("%03d", this.getCurrTime()));//0补齐数字
        this.setFont(new Font("STENCIL STD", 1, 35));
        this.setFocusable(false);//设置为不可对焦
    }

    //开始计时
    public void start(){
        t = new java.util.Timer(true);
        t.schedule(new TimerTask() {
            @Override
            public void run() { //每隔一秒要做的事
                if(!isPause){
                    if(currTime != 999)//最大999秒
                        currTime++;
                    setText(String.format("%03d", getCurrTime()));
                }
            }
        }, 1000, 1000);//延迟1，间隔1秒
    }

    //结束
    public void end(){
            t.cancel();
    }

    public int getCurrTime() {
        return currTime;
    }
}