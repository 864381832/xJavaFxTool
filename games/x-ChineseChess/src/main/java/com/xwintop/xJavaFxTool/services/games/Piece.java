package com.xwintop.xJavaFxTool.services.games;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

abstract class Piece extends Rectangle {
    Piece(int x, int y, boolean colorIsRed) {
        setWidth(4.0 * CheckerBoard.UNIT / 5.0);
        setHeight(4.0 * CheckerBoard.UNIT / 5.0);
        this.x = x;
        this.y = y;
        isRed = colorIsRed;
        setX(CheckerBoard.xToPx(x) - getWidth() / 2);
        setY(CheckerBoard.yToPx(y) - getHeight() / 2);
        ChineseChessService.points[x][y] = this;
        ChineseChessService.allPieces.add(this);
        setVisible(false);
    } //所有棋子构造时都应调用父类的这个构造函数

    int x;
    int y; //x,y都是棋盘上的坐标
    boolean isRed; //true为红色
    boolean isSelected = false;
    String name;

    protected void die() {
        ChineseChessService.points[x][y] = null;
        setVisible(false);
        setDisable(true);
    }

    protected abstract void moveTo(int dstX, int dstY) throws CanNotMoveToException;

    protected abstract void loadImage();

    void beSelected() {
        isSelected = true;
        ChineseChessService.selectedPiece = this;
        loadImage();
    }

    void cancelSelected() {
        isSelected = false;
        ChineseChessService.selectedPiece = null;
        loadImage();
    }

    static class CanNotMoveToException extends Exception {
    }
}

class Jiang extends Piece {


    Jiang(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);    //构造
        if (colorIsRed) {
            name = "帅";
        } else {
            name = "将";
        }

        loadImage();               //加载图片
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
        String winner;
        if (this.isRed)
            winner = "黑";
        else
            winner = "红";
        Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
        gameOverAlert.setTitle("游戏结束");
        gameOverAlert.setHeaderText(null);
        gameOverAlert.setContentText(winner + "方获胜");
        gameOverAlert.setGraphic(null);
        gameOverAlert.showAndWait();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        //移动到目标点
        if (Math.abs(dstX - x) + Math.abs(dstY - y) != 1)
            throw new CanNotMoveToException();
        if (dstX < 3 || dstX > 5)
            throw new CanNotMoveToException();
        if (isRed && dstY < 7)
            throw new CanNotMoveToException();
        if (!isRed && dstY > 2)
            throw new CanNotMoveToException(); //判断能否移动，不能则抛出异常
        if (ChineseChessService.points[dstX][dstY] != null) {      //目标点是否有棋子
            if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                throw new CanNotMoveToException();
            ChineseChessService.points[x][y].die();  //目标棋子死亡
        }


        ChineseChessService.points[x][y] = null;
        ChineseChessService.points[dstX][dstY] = this;
        x = dstX;
        y = dstY;
        //目标点变成当前棋子
        this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
        this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
    }

    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RK.GIF");
            else
                image = new Image("chessImages/BK.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RKS.GIF");
            else
                image = new Image("chessImages/BKS.GIF");
        }
        setFill(new ImagePattern(image));
    }           //加载正确的图片

}

class Shi extends Piece {
    Shi(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);
        if (colorIsRed) {
            name = "仕";
        } else {
            name = "士";
        }
        loadImage();
        setOnMousePressed(e -> {
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        if (Math.abs(dstX - x) + Math.abs(dstY - y) != 2)
            throw new CanNotMoveToException();
        if (dstX < 3 || dstX > 5)
            throw new CanNotMoveToException();
        if (isRed && dstY < 7)
            throw new CanNotMoveToException();
        if (!isRed && dstY > 2)
            throw new CanNotMoveToException();
        if (ChineseChessService.points[dstX][dstY] != null) {
            if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                throw new CanNotMoveToException();
            ChineseChessService.points[dstX][dstY].die();
        }
        ChineseChessService.points[x][y] = null;
        ChineseChessService.points[dstX][dstY] = this;
        x = dstX;
        y = dstY;
        this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
        this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
    }

    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RA.GIF");
            else
                image = new Image("chessImages/BA.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RAS.GIF");
            else
                image = new Image("chessImages/BAS.GIF");
        }
        setFill(new ImagePattern(image));
    }
}

class Xiang extends Piece {
    Xiang(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);
        if (colorIsRed) {
            name = "相";
        } else {
            name = "象";
        }
        loadImage();
        setOnMousePressed(e -> {
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        int midx = (dstX + x) / 2;
        int midy = (dstY + y) / 2;
        if (dstX < 0 || dstX > 8 || dstY < 0 || dstY > 9)//越界
        {
            throw new CanNotMoveToException();
        } else if (ChineseChessService.points[dstX][dstY] != null && ChineseChessService.points[dstX][dstY].isRed == isRed)//同色
        {
            throw new CanNotMoveToException();
        }//符合要求
        else if ((Math.abs(dstX - x) == 2) && (Math.abs(dstY - y) == 2) && (ChineseChessService.points[midx][midy] == null)
                && ((isRed && dstY >= 5) || (!isRed && dstY <= 4))) {
            if (ChineseChessService.points[dstX][dstY] != null) {
                ChineseChessService.points[dstX][dstY].die();
            }
            ChineseChessService.points[x][y] = null;
            ChineseChessService.points[dstX][dstY] = this;
            x = dstX;
            y = dstY;
            this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
            this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
        }//不符合要求
        else {
            throw new CanNotMoveToException();
        }
    }

    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RB.GIF");
            else
                image = new Image("chessImages/BB.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RBS.GIF");
            else
                image = new Image("chessImages/BBS.GIF");
        }
        setFill(new ImagePattern(image));
    }
}

class Ma extends Piece {
    Ma(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);
        name = "马";
        loadImage();
        setOnMousePressed(e -> {
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {

        boolean ans = false;
        if (dstX < 0 || dstX > 8 || dstY < 0 || dstY > 9) {
            throw new CanNotMoveToException();
        } else if (ChineseChessService.points[dstX][dstY] != null && ChineseChessService.points[dstX][dstY].isRed == isRed) {
            throw new CanNotMoveToException();
        } else if (((Math.abs(dstX - x) == 2 && Math.abs(dstY - y) == 1) ||
                (Math.abs(dstX - x) == 1 && Math.abs(dstY - y) == 2))) {
            if (Math.abs(dstX - x) == 2 && Math.abs(dstY - y) == 1) {
                if (dstX > x && ChineseChessService.points[x + 1][y] == null) {
                    ans = true;
                } else if (dstX < x && ChineseChessService.points[x - 1][y] == null) {
                    ans = true;
                }
            } else {

                if (dstY > y && ChineseChessService.points[x][y + 1] == null) {
                    ans = true;
                } else if (dstY < y && ChineseChessService.points[x][y - 1] == null) {
                    ans = true;
                }
            }
        }
        if (ans) {
            if (ChineseChessService.points[dstX][dstY] != null) {
                ChineseChessService.points[dstX][dstY].die();
            }
            ChineseChessService.points[x][y] = null;
            ChineseChessService.points[dstX][dstY] = this;
            x = dstX;
            y = dstY;
            this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
            this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
        }
        if (!ans) {
            throw new CanNotMoveToException();
        }
    }

    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RN.GIF");
            else
                image = new Image("chessImages/BN.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RNS.GIF");
            else
                image = new Image("chessImages/BNS.GIF");
        }
        setFill(new ImagePattern(image));
    }
}

class Ju extends Piece {
    Ju(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);
        name = "车";
        loadImage();
        setOnMousePressed(e -> {
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        int count = 0;
        //没有移动
        if (dstX == this.x && dstY == this.y)
            count = 1;
        //对角线移动
        if (dstX != this.x && dstY != this.y)
            count = 1;
        //判断中间有子。情况一
        if (dstX > this.x && dstY == this.y) {
            for (int temp = this.x + 1; temp < dstX; temp++) {
                if (ChineseChessService.points[temp][y] != null)
                    count = 1;
            }
        }
        //情况二
        if (dstX < this.x && dstY != this.y) {
            for (int temp = this.x - 1; temp > dstX; temp--) {
                if (ChineseChessService.points[temp][y] != null)
                    count = 1;
            }
        }
        //情况三
        if (dstX == this.x && dstY > this.y) {
            for (int temp = this.y + 1; temp < dstY; temp++) {
                if (ChineseChessService.points[x][temp] != null)
                    count = 1;
            }
        }
        //情况四
        if (dstX == this.x && dstY < this.y) {
            for (int temp = this.y - 1; temp > dstY; temp--) {
                if (ChineseChessService.points[x][temp] != null)
                    count = 1;
            }
        }
        //count==0，可以移动，且目标点不为己方子
        if (count == 0 && ChineseChessService.points[dstX][dstY] != null && ChineseChessService.points[dstX][dstY].isRed != this.isRed) {
            ChineseChessService.points[dstX][dstY].die();
            ChineseChessService.points[x][y] = null;
            ChineseChessService.points[dstX][dstY] = this;
            x = dstX;
            y = dstY;
            this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
            this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
        } else if (count == 0 && ChineseChessService.points[dstX][dstY] == null) {
            ChineseChessService.points[x][y] = null;
            ChineseChessService.points[dstX][dstY] = this;
            x = dstX;
            y = dstY;
            this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
            this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
        } else
            throw new CanNotMoveToException();

    }


    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RR.GIF");
            else
                image = new Image("chessImages/BR.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RRS.GIF");
            else
                image = new Image("chessImages/BRS.GIF");
        }
        setFill(new ImagePattern(image));
    }
}

class Pao extends Piece {
    Pao(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);    //构造
        name = "炮";
        loadImage();                //加载图片
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        int count = 0;//记录两点之间棋子个数
        //下列为6种情况
        if (dstX == this.x && dstY == this.y)
            throw new CanNotMoveToException();
        if (dstX != this.x && dstY != this.y)
            throw new CanNotMoveToException();
        if (dstX == this.x && dstY > this.y) {
            for (int tem = this.y + 1; tem < dstY; tem++) {
                if (ChineseChessService.points[x][tem] != null) {
                    count++;
                }
            }
        }
        if (dstX == this.x && dstY < this.y) {
            for (int tem = dstY + 1; tem < this.y; tem++) {
                if (ChineseChessService.points[x][tem] != null) {
                    count++;
                }
            }
        }
        if (dstY == this.y && dstX > this.x) {
            for (int tem = this.x + 1; tem < dstX; tem++) {
                if (ChineseChessService.points[tem][y] != null) {
                    count++;
                }
            }

        }
        if (dstY == this.y && dstX < this.x) {
            for (int tem = dstX + 1; tem < this.x; tem++) {
                if (ChineseChessService.points[tem][y] != null) {
                    count++;
                }
            }
        }
        if (ChineseChessService.points[dstX][dstY] == null) {//目的地是否有棋子以及是否为己方棋子
            if (count != 0)
                throw new CanNotMoveToException();
        } else {
            if (count != 1 || ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                throw new CanNotMoveToException();
            else
                ChineseChessService.points[dstX][dstY].die();
        }
        count = 0;
        ChineseChessService.points[x][y] = null;
        ChineseChessService.points[dstX][dstY] = this;
        x = dstX;
        y = dstY;
        this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
        this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
    }

    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RC.GIF");
            else
                image = new Image("chessImages/BC.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RCS.GIF");
            else
                image = new Image("chessImages/BCS.GIF");
        }
        setFill(new ImagePattern(image));
    }           //加载正确的图片
}


class Bing extends Piece {
    Bing(int x, int y, boolean colorIsRed) {
        super(x, y, colorIsRed);    //构造
        if (colorIsRed) {
            name = "兵";
        } else {
            name = "卒";
        }
        loadImage();                //加载图片
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChineseChessService.selectedPiece == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    protected void die() {
        super.die();
    }

    @Override
    protected void moveTo(int dstX, int dstY) throws CanNotMoveToException {
        if (this.isRed) {//红兵
            if (this.y >= 5) {//没过河
                if (dstX != this.x)
                    throw new CanNotMoveToException();
                else if (dstY != this.y - 1)
                    throw new CanNotMoveToException();
                if (ChineseChessService.points[dstX][dstY] != null) {
                    if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                        throw new CanNotMoveToException();
                    else
                        ChineseChessService.points[dstX][dstY].die();
                }
            } else {//过了河
                if (Math.abs(x - dstX) + Math.abs(y - dstY) != 1) {
                    throw new CanNotMoveToException();
                } else if (dstY == this.y + 1) {
                    throw new CanNotMoveToException();
                } else {
                    if (ChineseChessService.points[dstX][dstY] != null) {
                        if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                            throw new CanNotMoveToException();
                        else
                            ChineseChessService.points[dstX][dstY].die();
                    }
                }
            }
        } else {//黑卒
            if (this.y <= 4) {//没过河
                if (dstX != this.x)
                    throw new CanNotMoveToException();
                else if (dstY != this.y + 1)
                    throw new CanNotMoveToException();
                if (ChineseChessService.points[dstX][dstY] != null) {
                    if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                        throw new CanNotMoveToException();
                    else
                        ChineseChessService.points[dstX][dstY].die();
                }
            } else {//过了河
                if (Math.abs(x - dstX) + Math.abs(y - dstY) != 1) {
                    throw new CanNotMoveToException();
                } else if (dstY == this.y - 1) {
                    throw new CanNotMoveToException();
                } else {
                    if (ChineseChessService.points[dstX][dstY] != null) {
                        if (ChineseChessService.points[dstX][dstY].isRed == this.isRed)
                            throw new CanNotMoveToException();
                        else
                            ChineseChessService.points[dstX][dstY].die();
                    }
                }
            }
        }
        ChineseChessService.points[x][y] = null;
        ChineseChessService.points[dstX][dstY] = this;
        x = dstX;
        y = dstY;
        this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
        this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
    }


    @Override
    protected void loadImage() {
        Image image;
        if (!isSelected) {
            if (isRed)
                image = new Image("chessImages/RP.GIF");
            else
                image = new Image("chessImages/BP.GIF");
        } else {
            if (isRed)
                image = new Image("chessImages/RPS.GIF");
            else
                image = new Image("chessImages/BPS.GIF");
        }
        setFill(new ImagePattern(image));
    }           //加载正确的图片

}
