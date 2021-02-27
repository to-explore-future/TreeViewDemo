package com.fs.treeview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by to-explore-future on 2020/4/22
 * {"name":"树","url":"","subData":[{"name":"榆树","url":"","subData":[{"name":"榆树1","url":"","subData":[]},{"name":"榆树2","url":"","subData":[]},{"name":"榆树3","url":"","subData":[]},{"name":"榆树4","url":"","subData":[]},{"name":"榆树5","url":"","subData":[{"name":"松树1","url":"","subData":[]},{"name":"松树2","url":"","subData":[]},{"name":"松树3","url":"","subData":[]},{"name":"松树4","url":"","subData":[]},{"name":"松树5","url":"","subData":[]},{"name":"松树6","url":"","subData":[]}]},{"name":"榆树6","url":"","subData":[]},{"name":"榆树7","url":"","subData":[]},{"name":"榆树8","url":"","subData":[]},{"name":"榆树9","url":"","subData":[]}]},{"name":"银杏树","url":"","subData":[]},{"name":"凤凰树","url":"","subData":[]},{"name":"臭椿树","url":"","subData":[]},{"name":"梧桐树","url":"","subData":[{"name":"梧桐树1","url":"","subData":[]},{"name":"梧桐树2","url":"","subData":[]},{"name":"梧桐树3","url":"","subData":[]},{"name":"梧桐树4","url":"","subData":[]},{"name":"梧桐树5","url":"","subData":[]}]}]}
 * node的数据结构
 */
public class Node implements Serializable {

    private int nodeId;
    private int parentId;
    private int treeId;

    private String name;
    private String imgUrl;

    private ArrayList<Node> subNode = new ArrayList();

    public Node() {
    }

    public Node(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public ArrayList<Node> getSubNode() {
        return subNode;
    }

    public void setSubNode(ArrayList<Node> subNode) {
        this.subNode = subNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    /**
     * @param superNode
     * @param currentNode
     */
    public static void delNode(Node superNode, Node currentNode) {

        ArrayList<Node> subData = superNode.getSubNode();
        if (subData.size() > 0) {
            if (subData.contains(currentNode)) {
                int i = subData.indexOf(currentNode);
                subData.remove(i);
                //强制递归结束
                throw new StepOutRecursionException();
            } else {
                for (Node subDatum : subData) {
                    if (subDatum.getSubNode().size() > 0) {
                        delNode(subDatum, currentNode);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return nodeId == node.nodeId &&
                parentId == node.parentId &&
                treeId == node.treeId &&
                Objects.equals(name, node.name) &&
                Objects.equals(imgUrl, node.imgUrl) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, parentId, treeId, name, imgUrl);
    }
}
