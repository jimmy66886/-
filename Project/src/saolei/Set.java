package saolei;

//初始化游戏
public class Set {

    public static final char MINE = 'M';
    //雷

    // 默认参数常量

    //难度：easy-normal-hard
    //easy-9*9，10个雷
    //normal-16*16，40个雷
    //hard-16*30，99个雷
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final int SIZE_EASY_ROW = 9;
    public static final int SIZE_EASY_COL = 9;
    public static final int SIZE_EASY_MINE = 10;
    public static final int SIZE_NORMAL_ROW = 16;
    public static final int SIZE_NORMAL_COL = 16;
    public static final int SIZE_NORMAL_MINE = 40;
    public static final int SIZE_HARD_ROW = 16;
    public static final int SIZE_HARD_COL = 30;
    public static final int SIZE_HARD_MINE = 99;

    // 格子状态常量
    public static final int STAT_OPEN = 0;
    public static final int STAT_COVER = 1;
    public static final int STAT_FLAG = 2;

    // 行列偏移量常量 (位于↖,↑,↗,←,→,↙,↓,↘)
    public static final int[] OFFSET_AROUND_ROW = {-1, -1, -1, 0, 0, 1, 1, 1};
    public static final int[] OFFSET_AROUND_COL = {-1, 0, 1, -1, 1, -1, 0, 1};

    // 游戏状态
    public boolean isGameover;
    public boolean isWin;
    public int remain; // 待翻开格子数
    //需要用这个判断游戏是否获胜，不能删，可以设置不显示
    public int flags; // 标记数量
    public int deadRow, deadCol; // 爆炸发生的格子索引

    // 格子
    public char[][] grids;
    public int[][] status;

    public void init() {
        init(DIFFICULTY_EASY);
    }

    //初始化游戏-默认难度
    public void init(int difficulty) {
        switch (difficulty) {
            case DIFFICULTY_EASY:
                init(SIZE_EASY_ROW, SIZE_EASY_COL, SIZE_EASY_MINE);
                break;
            case DIFFICULTY_NORMAL:
                init(SIZE_NORMAL_ROW, SIZE_NORMAL_COL, SIZE_NORMAL_MINE);
                break;
            case DIFFICULTY_HARD:
                init(SIZE_HARD_ROW, SIZE_HARD_COL, SIZE_HARD_MINE);
                break;
        }
    }

    /*
        rowSize格子行数
        colSize格子列数
        mineSize地雷数量
     */
    public void init(int rowSize, int colSize, int mineSize) {
        isGameover = false;
        isWin = false;
        remain = rowSize * colSize - mineSize;
        flags = 0;

        grids = new char[rowSize][colSize];
        status = new int[rowSize][colSize];

        // 随机生成地雷
        int rCol;
        int rRow;
        for (int i = 0; i < mineSize; i++) {
            do {
                rRow = (int) (Math.random() * rowSize);
                rCol = (int) (Math.random() * colSize);
            } while (isMine(rRow, rCol));
            grids[rRow][rCol] = MINE;
        }

        // 遍历所有格子
        for (int row = 0; row < grids.length; row++) {
            for (int col = 0; col < grids[row].length; col++) {
                // 计算
                if (!isMine(row, col)) {
                    grids[row][col] = (char) (countMinesAround(row, col) + '0');
                }
                // 盖住
                status[row][col] = STAT_COVER;
            }
        }

    }

    //判断是否出界
    public boolean isOutOfBound(int row, int col) {
        return row < 0 || col < 0 || row >= grids.length || col >= grids[0].length;
    }

    //判断是否为地雷
    public boolean isMine(int row, int col) {
        if (grids[row][col] == MINE) {
            return true;
        }
        return false;
    }

    //判断是否被标记
    public boolean isFlag(int row, int col) {
        if (status[row][col] == STAT_FLAG) {
            return true;
        }
        return false;
    }

    //计算格子周围的地雷数量
    public int countMinesAround(int row, int col) {

        int mineCount = 0;

        // 遍历周边格子
        for (int i = 0; i < OFFSET_AROUND_ROW.length; i++) {
            int r = row + OFFSET_AROUND_ROW[i];
            int c = col + OFFSET_AROUND_COL[i];

            if (!isOutOfBound(r, c) && isMine(r, c)) {
                mineCount++;
            }
        }
        return mineCount;
    }

    //标记格子
    public void flag(int row, int col) {
        if (status[row][col] != STAT_OPEN) {
            if (status[row][col] == STAT_COVER) {
                status[row][col] = STAT_FLAG;
                flags++;
            } else {
                status[row][col] = STAT_COVER;
                flags--;
            }
        }
    }

    //翻开指定格子
    public void open(int row, int col) {
        status[row][col] = STAT_OPEN;
        remain--;

        // 如果翻到周围无雷的格子，则继续翻开周围一圈
        if (grids[row][col] == '0') {

            // 遍历周边格子
            for (int i = 0; i < OFFSET_AROUND_ROW.length; i++) {
                int r = row + OFFSET_AROUND_ROW[i];
                int c = col + OFFSET_AROUND_COL[i];

                // 翻开未越界且未翻开的
                if (!isOutOfBound(r, c) && status[r][c] == STAT_COVER) {
                    open(r, c);
                }
            }
        }

        // 判定是否翻到雷
        if (isMine(row, col)) {
            deadRow = row;
            deadCol = col;
            isGameover = true;
        }

        // 胜利判定
        if (remain == 0) {
            isWin = true;
        }
    }
}
