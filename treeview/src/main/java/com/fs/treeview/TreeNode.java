package com.fs.treeview;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by to-explore-future on 2020/4/21
 * 节点表示与其他节点之间的关系 并且存储数据
 * 这个node表示跟绘制相关的数据:
 */
public class TreeNode {

    private float topCenterX;       //上边框中心x
    private float topCenterY;       //上边框中心y
    private float leftCenterX;      //左边框中心X
    private float leftCenterY;      //左边框中心y
    private float bottomCenterX;    //下边框中心x
    private float bottomCenterY;    //下边框中心y
    private float rightCenterX;     //右边框中心x
    private float rightCenterY;     //有边框中心y
    private float originX;
    private float originY;

    private float width;
    private float height;

    private Node node;      // 数据
    private String mPath = "";   // 从图片选择器选择图片之后.本地图片地址临时存储在这里.
    private String url = "";     // 图片上传成功之后,把网址保存到这里
    private ArrayList<TreeNode> subNodes = new ArrayList<>();
    private View mView;
    private boolean isViewSelected; //view是否被选中

    /**
     * 通过传入一个view,找到对应的path,
     *
     * @param view
     */
    public boolean setPath(View view, String path) {
        if (view == this.mView) {
            mPath = path;
            return true;
        }
        ArrayList<TreeNode> subNodes = getSubNodes();
        for (TreeNode subNode : subNodes) {
            boolean b = subNode.setPath(view, path);
            if (b) {
                return true;
            }
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public boolean isViewSelected() {
        return isViewSelected;
    }

    public void setViewSelected(boolean viewSelected) {
        isViewSelected = viewSelected;
    }

    public float getTopCenterX() {
        return topCenterX;
    }

    public void setTopCenterX(float topCenterX) {
        this.topCenterX = topCenterX;
        setOriginX(topCenterX - width / 2);
    }

    public float getTopCenterY() {
        return topCenterY;
    }

    public void setTopCenterY(float topCenterY) {
        this.topCenterY = topCenterY;
        setOriginY(topCenterY);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public ArrayList<TreeNode> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(ArrayList<TreeNode> subNodes) {
        this.subNodes = subNodes;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public float getLeft() {
        return originX;
    }

    public float getRight() {
        return originX + width;
    }

    public float getBottom() {
        return originY + height;
    }

    public float getTop() {
        return originY;
    }

    public float getLeftCenterX() {
        return originX;
    }

    public void setLeftCenterX(float leftCenterX) {
        this.leftCenterX = leftCenterX;
        setOriginX(leftCenterX);
    }

    public float getLeftCenterY() {
        return originY + height / 2;
    }

    public void setLeftCenterY(float leftCenterY) {
        this.leftCenterY = leftCenterY;
        setOriginY(leftCenterY - height / 2);
    }

    public float getBottomCenterX() {
        return originX + width / 2;
    }

    public float getBottomCenterY() {
        return originY + height;
    }

    public float getRightCenterX() {
        return originX + width;
    }

    public float getRightCenterY() {
        return originY + height / 2;
    }


    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }


}
