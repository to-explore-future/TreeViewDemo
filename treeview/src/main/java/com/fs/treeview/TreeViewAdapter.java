package com.fs.treeview;

import android.view.View;

/**
 * Created by 张强 869518570@qq.com on 2020/4/23
 */
public abstract class TreeViewAdapter {
    private Node mNode;

    public Node getNode() {
        return mNode;
    }

    public void setNode(Node node) {
        this.mNode = node;
    }

    public TreeViewAdapter(Node node) {
        this.mNode = node;
    }

    public abstract View getView(TreeNode treeNode);
}
