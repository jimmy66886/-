package saolei;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.io.FileInputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SaoLei extends JFrame {
    public static final String TITLE = "扫雷 ";
    //扫雷游戏标题

    // 数字颜色 (1~8)
    //一个数组，存放1~8的颜色
    public static final Color[] COLORS_NUM = {new Color(64, 80, 192), new Color(16, 102, 0), new Color(176, 0, 0), new Color(0, 0, 128), new Color(128, 0, 0), new Color(0, 128, 128), new Color(160, 0, 0), new Color(176, 0, 0)};

    // 预置主题色 ColorTheme
    public static final Color CT_RED = new Color(14, 120, 226);//困难模式下的方块颜色
    public static final Color CT_RED_FOCUS = new Color(26, 89, 159);//鼠标放置在方块上显示的颜色

    public static final Color CT_BLUE = new Color(109, 164, 119);//正常模式下的方块颜色
    public static final Color CT_BLUE_FOCUS = new Color(58, 82, 73);//鼠标放置在方块上显示的颜色

    public static final Color CT_PURPLE = new Color(223, 232, 120);//简单模式下方块颜色
    public static final Color CT_PURPLE_FOCUS = new Color(105, 110, 58);//鼠标放置在方块上显示的颜色

    // 图标资源路径
    public static final String PATH_IMG_MINE = "res/img/bomb.png";//炸弹图标
    public static final String PATH_IMG_FLAG = "res/img/flag.png";//旗子图标
    public static final String PATH_IMG_FLAG_CORRECT = "res/img/flag_correct.png";//正确旗子图标
    public static final String PATH_IMG_FLAG_WRONG = "res/img/flag_wrong.png";//错误旗子图标

    // 主题色
    private Color themeColor;
    private Color themeFocusColor;

    // 图标
    public ImageIcon icMine, icFlag, icFlagCorrect, icFlagWrong;

    private JPanel titlePane;
    private JPanel gamePane;
    private JPanel menuPane;
    private JLabel lblTitle;

    private JLabel[][] tiles; // 数字格子
    private JButton[][] covers; // 砖块

    // 游戏初始化
    private Set core = new Set();

    //计时器
    Timer time1 = new Timer();

    //窗口
    public SaoLei() {
        // 设置窗口 (Frame)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认关闭方式
        setMinimumSize(new Dimension(800, 600));//设置最小窗口大小，默认是640*480
        setSize(800, 600);//设置默认窗口大小
        setLocationRelativeTo(null); // 窗口居中

        // 显示标题界面
        showTitle();
    }

    //重新计算图标大小 (初始化时调用 / 自适应窗口改变砖块大小时调用)
    private void initImage(int width, int height) {
        icMine = new ImageIcon();
        icFlag = new ImageIcon();
        icFlagCorrect = new ImageIcon();
        icFlagWrong = new ImageIcon();

        ImageIcon[] icons = {icMine, icFlag, icFlagCorrect, icFlagWrong};
        String[] pathes = {PATH_IMG_MINE, PATH_IMG_FLAG, PATH_IMG_FLAG_CORRECT, PATH_IMG_FLAG_WRONG};

        for (int i = 0; i < icons.length; i++) {
            ImageIcon ic = new ImageIcon(pathes[i]);
            icons[i].setImage(ic.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }//给图标设置宽高
    }

    static void beginMusic() {
        //播放开始音乐
        try {
            //在游戏结束时添加音频文件
            FileInputStream fileau = new FileInputStream("D:\\Codefield\\CODE_JAVA\\Project\\src\\begin.wav");
            AudioStream as = new AudioStream(fileau);
            AudioPlayer.player.start(as);
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    //显示界面
    private void showTitle() {
        setTitle(TITLE);

        // 设置容器 (Panel)
        titlePane = new JPanel();
        titlePane.setBackground(Color.WHITE);
        //盒式容器，垂直布局
        titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));

        // 标题
        lblTitle = new JLabel(TITLE, JLabel.CENTER);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);//垂直对齐方式
        lblTitle.setPreferredSize(new Dimension(0, 128));//设置最好的大小，不一定与实际显示出来的空间大小一致（根据界面整体的变化而变化）
        lblTitle.setFont(new Font(null, Font.ITALIC, 36));

        // 按钮的容器
        menuPane = new JPanel();
        menuPane.setOpaque(false);//设置控件透明，true是不透明，false透明
        //盒子容器，水平布局
        menuPane.setLayout(new BoxLayout(menuPane, BoxLayout.X_AXIS));//设置布局方式

        // 开始按钮的属性定义
        Font btnFont = new Font(null, Font.BOLD, 30);//加粗
        String[] btnTexts = new String[]{"简单", "正常", "困难"};
        Color[] btnColors = new Color[]{new Color(0, 192, 0), new Color(0, 160, 255), new Color(255, 64, 0)};
        JButton[] btns = new JButton[btnTexts.length];

        // 开始按钮的事件
        ActionListener btnActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                //绿蓝红代表三种难度
                if (btnTexts[0].equals(cmd)) {
                    themeColor = CT_PURPLE;//给方块设置默认颜色
                    themeFocusColor = CT_PURPLE_FOCUS;//设置鼠标放置在方块上方的颜色
                    startGame(Set.SIZE_EASY_ROW, Set.SIZE_EASY_COL, Set.SIZE_EASY_MINE);//设置地图的行列数
                    beginMusic();//播放开始音乐
                    //开始计时
                    time1.start();
                } else if (btnTexts[1].equals(cmd)) {
                    themeColor = CT_BLUE;
                    themeFocusColor = CT_BLUE_FOCUS;
                    startGame(Set.SIZE_NORMAL_ROW, Set.SIZE_NORMAL_COL, Set.SIZE_NORMAL_MINE);
                    beginMusic();
                    //开始计时
                    time1.start();
                } else if (btnTexts[2].equals(cmd)) {
                    themeColor = CT_RED;
                    themeFocusColor = CT_RED_FOCUS;
                    startGame(Set.SIZE_HARD_ROW, Set.SIZE_HARD_COL, Set.SIZE_HARD_MINE);
                    beginMusic();
                    time1.start();
                }
            }
        };

        // 设定按钮
        for (int i = 0; i < btns.length; i++) {//btns个数
            // 实例化
            btns[i] = new JButton();

            // 设置尺寸
            btns[i].setMinimumSize(new Dimension(128, 128));
            btns[i].setMaximumSize(new Dimension(128, 128));
            btns[i].setPreferredSize(new Dimension(128, 128));

            // 前景(文字)颜色
            btns[i].setBackground(btnColors[i]);//依次给按纽设置颜色
            btns[i].setForeground(Color.WHITE);
            btns[i].setFont(btnFont);
            btns[i].setText(btnTexts[i]);

            // 注册监听
            btns[i].addActionListener(btnActionListener);

            // 添加按钮和占位元素到面板
            menuPane.add(btns[i]);
            menuPane.add(Box.createHorizontalStrut(100)); // 占位元素，默认是32，这里代表每个按钮之间的间距
        }

        // 移除最后一个多余的占位元素 (Strut)
        menuPane.remove(menuPane.getComponentCount() - 1);

        // 添加组件到TitlePane
        titlePane.add(Box.createVerticalGlue()); // 自适应占位元素
        titlePane.add(lblTitle);
        titlePane.add(menuPane);
        titlePane.add(Box.createVerticalGlue());

        setContentPane(titlePane);
        revalidate(); // 刷新UI
    }

    //初始化游戏窗口-行数rowSize
    //列数colSize
    //mineSize-地雷数量
    private void startGame(int rowSize, int colSize, int mineSize) {
        // UI尺寸
        int tileSize = 64;
        int paddingHorizontal = 128;
        int paddingVertical = 128;

        // 调整尺寸
        Insets ins = getInsets(); // 获取窗口边框尺寸
        setSize(paddingHorizontal * 2 + tileSize * colSize + ins.left + ins.right, paddingVertical * 2 + tileSize * rowSize + ins.top + ins.bottom);
        setLocationRelativeTo(null); // 窗口居中

        // 初始化图标
        initImage(tileSize, tileSize);
        core.init(rowSize, colSize, mineSize);

        // 扫雷按钮事件
        // 鼠标事件
        MouseAdapter sweepActionListener = new MouseAdapter() {

            static final int BTN_L = MouseEvent.BUTTON1_DOWN_MASK;//左键
            static final int BTN_M = MouseEvent.BUTTON2_DOWN_MASK;//中建
            static final int BTN_R = MouseEvent.BUTTON3_DOWN_MASK;//右键
            static final int BTN_L_R = MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.BUTTON3_DOWN_MASK;//左右一起按

            static final int STAT_NORMAL = 0;
            static final int STAT_FOCUS = 1;
            static final int STAT_DOWM = 2;

            int mask; // 记录当前按键

            Object focusComponent;

            public void mouseEntered(MouseEvent e) {
                focusComponent = e.getSource(); // 鼠标进入控件时标记为当前控件

                if (focusComponent instanceof JButton) {
                    if (mask == BTN_L || mask == BTN_M || mask == BTN_L_R) {
                        // 鼠标按键进行“翻开”相关操作的情形
                        setGridStyle((JButton) focusComponent, STAT_DOWM);
                    } else {
                        setGridStyle((JButton) focusComponent, STAT_FOCUS);
                    }
                }
            }


            public void mouseExited(MouseEvent e) {
                focusComponent = null; // 鼠标离开控件时清除当前控件
                // (否则在场景外松开鼠标依然会触发位于边缘的格子的事件)
                Object eSrc = e.getSource();
                if (eSrc instanceof JButton) {
                    setGridStyle((JButton) eSrc, STAT_NORMAL);
                }
            }

            public void mousePressed(MouseEvent e) {
                mask = e.getModifiersEx(); // 按下鼠标时记录鼠标按键

                if (focusComponent instanceof JButton) {
                    if (mask == BTN_L || mask == BTN_M || mask == BTN_L_R) {
                        // 鼠标按键进行“翻开”相关操作的情形
                        setGridStyle((JButton) focusComponent, STAT_DOWM);
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                // 遍历所有按钮
                boolean flag = true; // 是否继续遍历的标志
                for (int row = 0; flag && row < tiles.length; row++) {
                    for (int col = 0; flag && col < tiles[0].length; col++) {

                        // 匹配发生点击事件的按钮
                        if (focusComponent == covers[row][col] && mask == BTN_L) {
                            open(row, col); // 左键翻开
                            flag = false;
                        } else if (focusComponent == covers[row][col] && mask == BTN_R) {
                            flag(row, col); // 右键标记
                            flag = false;
                        }
                    }
                }
                mask = 0; // 重置按键标识
            }
        };

        // 根据尺寸创建数组
        tiles = new JLabel[rowSize][colSize];
        covers = new JButton[rowSize][colSize];

        // 创建容器 (Panel)
        gamePane = new JPanel();
        gamePane.setLayout(null);

        // 创建控件
        for (int r = 0; r < rowSize; r++) {
            for (int c = 0; c < colSize; c++) {
                // 格子
                int num = core.grids[r][c] - '0';
                tiles[r][c] = new JLabel("" + core.grids[r][c], JLabel.CENTER);
                gamePane.add(tiles[r][c]);
                tiles[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
                tiles[r][c].setBorder(BorderFactory.createLoweredSoftBevelBorder());
                tiles[r][c].setFont(new Font(null, Font.BOLD, tileSize * 3 / 4));
                if (num == 0) {
                    tiles[r][c].setText("");//点开显示空白
                } else if (num > 0 && num < 9) {
                    tiles[r][c].setForeground(COLORS_NUM[num - 1]);
                } else if (core.grids[r][c] == Set.MINE) {
                    tiles[r][c].setText("");
                    tiles[r][c].setIcon(icMine);
                }
                tiles[r][c].setVisible(false);

                // 盖子 (按钮)
                covers[r][c] = new JButton();
                covers[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
                setGridStyle(covers[r][c], 0);
                gamePane.add(covers[r][c]);

                // 绑定事件
                covers[r][c].addMouseListener(sweepActionListener);
                tiles[r][c].addMouseListener(sweepActionListener);
            }
        }

        setContentPane(gamePane);
        revalidate(); // 刷新UI
    }

    //标记格子
    private void flag(int row, int col) {

        // 未翻开才可标记
        if (core.status[row][col] != Set.STAT_OPEN) {
            core.flag(row, col);

            setTitle(TITLE + " ( Remain: " + core.remain + " , Flag: " + core.flags + " )");

            // 刷新格子状态
            if (core.status[row][col] == Set.STAT_FLAG) {
                covers[row][col].setIcon(icFlag);
            } else {
                covers[row][col].setIcon(null);
            }
        }
    }

    //翻开盒子
    private void open(int row, int col) {

        // 没有标记且未翻开才可翻开
        if (core.status[row][col] == Set.STAT_COVER) {
            core.open(row, col);
            // 刷新所有格子状态
            refreshStatus();
        }
    }

    //设置初始样式
    //component-控件对象
    //status-空间状态（0普通状态，1焦点状态，2按下状态）
    private void setGridStyle(JComponent component, int status) {
        switch (status) {
            case 1:
                component.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                //创建一个具有凸出斜面边缘的边框，将组件当前背景色的较亮的色度用于高亮显示，较暗的色度用于阴影。（在凸出边框中，高亮显示位于顶部，阴影位于其下。）
                //JComponent是一个和JPanel很相似的组件容器，但JPanel不透明
                component.setBackground(themeFocusColor);
                break;
            case 2:
                component.setBorder(BorderFactory.createLoweredSoftBevelBorder());
                component.setBackground(themeColor);
                break;
            case 0:
            default:
                component.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                component.setBackground(themeColor);
                break;
        }

    }

    //刷新格子状态
    private void refreshStatus() {

        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                boolean isOpened = core.status[r][c] == Set.STAT_OPEN;
                tiles[r][c].setVisible(isOpened);
                covers[r][c].setVisible(!isOpened);

                // 游戏结束则标出雷的位置
                if (core.isGameover) {
                    if (core.isMine(r, c) && core.status[r][c] == Set.STAT_COVER) {
                        // 未标记的雷
                        covers[r][c].setIcon(icMine);//设置对应的图标
                    } else if (core.isMine(r, c) && core.status[r][c] == Set.STAT_FLAG) {
                        // 标记正确的雷
                        covers[r][c].setIcon(icFlagCorrect);
                    } else if (!core.isMine(r, c) && core.status[r][c] == Set.STAT_FLAG) {
                        // 标记错误的雷
                        covers[r][c].setIcon(icFlagWrong);
                    }
                }
            }
        }

        // 游戏结束判定
        if (core.isGameover) {
                try {
                    //在游戏结束时添加音频文件
                    FileInputStream fileau = new FileInputStream("D:\\Codefield\\CODE_JAVA\\Project\\src\\boom.wav");
                    AudioStream as = new AudioStream(fileau);
                    AudioPlayer.player.start(as);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            tiles[core.deadRow][core.deadCol].setOpaque(true);
            tiles[core.deadRow][core.deadCol].setBackground(Color.RED);
            JOptionPane.showMessageDialog(null, "游戏结束，你输了", TITLE, JOptionPane.WARNING_MESSAGE);

            //计算时间
            time1.end();
            JOptionPane.showMessageDialog(null, time1, "用时", JOptionPane.WARNING_MESSAGE);
            showTitle();
        } else if (core.isWin) {
            JOptionPane.showMessageDialog(null, "恭喜你，你赢了", TITLE, JOptionPane.PLAIN_MESSAGE);
            //计算时间
            time1.end();
            JOptionPane.showMessageDialog(null, time1, "用时", JOptionPane.WARNING_MESSAGE);
            showTitle();
        }
    }

    //主方法
    public static void main(String[] args) {
        SaoLei frame = new SaoLei();
        frame.setVisible(true);
    }
}
