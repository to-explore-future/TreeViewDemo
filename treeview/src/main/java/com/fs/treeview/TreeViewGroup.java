package com.fs.treeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by to-explore-future on 2020/4/21
 */
public class TreeViewGroup extends ViewGroup {


    private static final String TAG = TreeViewGroup.class.getSimpleName();

    private Context mContext;
    private TreeNode mTreeNode; //这个treeNode 存储跟绘制相关的数据,是android端独有的
    private ArrayList<Path> mPaths = new ArrayList<>();

    private float NODE_H_SPACING = 30;  //同级节点之间水平间距
    private float NODE_V_SPACING = 120;  //同级节点之间竖直间距
    private Paint mPathPaint;

    private Paint mCanvasOriginPaint;
    /**
     * 曲线的始末添加一段直线,直线占两node的百分比
     */
    private float startLinePercent = 0.02f;
    private float endLinePercent = 0.01f;

    /**
     * 为了让贝塞尔曲线更曲更翘,给连个控制点一定的偏移
     * 偏移量 = 两个node之间的垂直间距 * controlPointOffsetPercent
     * 第一个控制点向下偏移
     * 第二个控制点向上偏移
     */
    private float bezierCurveFirstControlPointOffsetPercent = 0.2f;
    private float bezierCurveSecondControlPointOffsetPercent = 0.4f;

    private TreeViewAdapter adapter;
    private Node mNode;  //这个node描述节点,前后端通用

    private float mostLeftX;
    private float mostRightX;
    private float mostTopY;
    private float mostBottomY;

    //这个控件的内边距
    private float paddingScale = 0.01f; //边距缩放值

    private int mScreenWidth;
    private int mScreenHeight;

    private Rect mSelectedRect;
    public static final int ORIENTATION_LEFT_RIGHT = 1; //从左至右:手机竖着的时候使用这种绘制方式
    public static final int ORIENTATION_TOP_BOTTOM = 0; //从上到下:手机躺着的时候使用这种绘制方式
    //注意:这个说的是绘制方向,并不是说手机的方向
//    private int drawOrientation = ORIENTATION_TOP_BOTTOM; //绘制方向
    private int drawOrientation = ORIENTATION_LEFT_RIGHT; //绘制方向

    public void setDrawOrientation(int drawOrientation) {
        this.drawOrientation = drawOrientation;
        mostLeftX = 0;
        mostRightX = 0;
        mostTopY = 0;
        mostBottomY = 0;
        init();
    }

    public TreeViewGroup(Context context) {
        super(context, null);
    }

    public TreeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        initPaint();
        mScreenWidth = ScreenUtil.getWidth(context);
        mScreenHeight = ScreenUtil.getHeight(context);
    }

    public TreeNode getTreeNode() {
        return mTreeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        mTreeNode = treeNode;
    }

    private void init() {
        //todo 对外提供一个方法,设置这个view的绘制方向 ,或者有没有自动的方法
        //判断用户是否打开了 自动旋转 ...
        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {  //手机竖着
            NODE_H_SPACING = 120;
            NODE_V_SPACING = 30;
        }

        if (drawOrientation == ORIENTATION_TOP_BOTTOM) { //手机躺着
            NODE_H_SPACING = 30;
            NODE_V_SPACING = 120;
        }
    }

    private void initPaint() {
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(Color.GREEN);
        mPathPaint.setStrokeWidth(3);

        mCanvasOriginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvasOriginPaint.setColor(Color.RED);
        mCanvasOriginPaint.setStyle(Paint.Style.STROKE);
        mCanvasOriginPaint.setStrokeWidth(10);
    }

    public void setAdapter(TreeViewAdapter adapter) {
        this.adapter = adapter;
        mNode = adapter.getNode();
        //根据data 生成 TreeNode
        mTreeNode = new TreeNode();
        mTreeNode.setNode(mNode);
        generateTreeNode(mNode, mTreeNode);
        generateView(mTreeNode);
    }

    public void updateUI() {
        generateTreeNode(mNode, mTreeNode);
        generateView(mTreeNode);
        mPaths.clear();
        requestLayout();
        mSelectedRect = null;
        invalidate();
    }

    public void addTreeNode(TreeNode selectedTreeNode) {
        addTreeNode(selectedTreeNode, mTreeNode);
        generateView(mTreeNode);
        mPaths.clear();
        requestLayout();
        mSelectedRect = null;
        invalidate();
    }

    /**
     * @param selectedTreeNode find one treeNode == selectedTreeNode
     */
    private void addTreeNode(TreeNode selectedTreeNode, TreeNode treeNode) {
        if (selectedTreeNode == treeNode) {
            TreeNode tempTreeNode = new TreeNode();
            Node node = new Node("subTopic", "");
            tempTreeNode.setNode(node);
            treeNode.getSubNodes().add(tempTreeNode);
        }
        for (TreeNode subNode : treeNode.getSubNodes()) {
            addTreeNode(selectedTreeNode, subNode);
        }
    }


    /**
     * 将数据导入进TreeNode里面
     *
     * @param node
     * @return
     */
    private void generateTreeNode(Node node, TreeNode treeNode) {
        ArrayList<Node> subNodes = node.getSubNode();
        for (Node subNode : subNodes) {
            TreeNode subTreeNode = new TreeNode();
            subTreeNode.setNode(copyNode(subNode));
            treeNode.getSubNodes().add(subTreeNode);
            generateTreeNode(subNode, subTreeNode);
        }
    }

    /**
     * copy all the params in {@link Node} except subNode
     *
     * @param oldNode
     * @return
     */
    private Node copyNode(Node oldNode) {
        Node node = new Node();
        node.setName(oldNode.getName());
        node.setNodeId(oldNode.getNodeId());
        node.setParentId(oldNode.getParentId());
        node.setTreeId(oldNode.getTreeId());
        node.setImgUrl(oldNode.getImgUrl());
        return node;
    }

    /**
     * 根据node生成view
     */
    private void generateView(TreeNode treeNode) {
        if (treeNode.getView() == null) {
            View view = adapter.getView(treeNode);
            treeNode.setView(view);
            addView(view);
        }
        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        for (TreeNode subNode : subNodes) {
            generateView(subNode);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 这个方法执行之后 所有的subview 的宽高才会生效
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //measureChildren 测量的是每个view的真实的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (mTreeNode == null) {
            return;
        }

        //测量所有node的宽高
        measureNodeWH(mTreeNode);
        //计算所有node的xy
        mTreeNode.setTopCenterX(100);
        mTreeNode.setTopCenterY(500);

        measureSubNodeXY(mTreeNode);

        //测量自身的大小
        int width = (int) (mostRightX - mostLeftX);
        int height = (int) (mostBottomY - mostTopY);
        //在原有的基础上增加 10%
        width *= (paddingScale * 2 + 1);
        height *= (paddingScale * 2 + 1);
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        if (width < mScreenWidth) {
            width = mScreenWidth;
        }
        if (height < mScreenHeight) {
            height = mScreenHeight;
        }
        int[] wh = adjustWH(width, height);
        width = wh[0];
        height = wh[1];
        setMeasuredDimension(width, height); //把宽高值保存起来，必须要调用
        Log.d(TAG, "------width:" + width + "--------------------height:" + height);

        adjustXOrY(mTreeNode);
        //把所有的view 进行平移
        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
            float offsetY = height / 2 - (mostTopY + Math.abs(mostBottomY - mostTopY) / 2);
            float offsetX = width / 2 - (mostLeftX + Math.abs(mostRightX - mostLeftX) / 2);
            translationAllView(mTreeNode, offsetX, offsetY);
        }
        if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
            float offsetX = width / 2 - (mostLeftX + Math.abs((mostRightX - mostLeftX) / 2));
            translationAllView(mTreeNode, offsetX, (height - mostBottomY) / 2);
        }
        if (mPaths != null) {
            mPaths.clear();
        }
        generatePath(mTreeNode);  //生成绘制贝塞尔曲线需要的path}
    }

    /**
     * 调整view的宽度和高度
     * 让这个view的宽高比和屏幕的宽高比保持一致
     * 主动去调整相对较小的那个值
     */
    int[] adjustWH(int width, int height) {
        Log.d(TAG, "before------width:" + width + "--------------------height:" + height);
        float w = width;
        float h = height;
        float sw = 0;
        float sh = 0;
        //屏幕旋转之后,所谓的屏幕宽高就会发生改变
        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
            sw = mScreenWidth;
            sh = mScreenHeight;
        }
        if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
            sw = mScreenHeight;
            sh = mScreenWidth;
        }

        //三种情况
        //1.宽高比相等
        if (h / w == sh / sw) {
            return new int[]{(int) w, (int) h};
        }
        if (h / w > sh / sw) { //宽度不够
            w = sw * h / sh;
            return new int[]{(int) w, (int) h};
        }
        if (h / w < sh / sw) { //高度不够
            h = sh * w / sw;
            return new int[]{(int) w, (int) h};
        }
        return new int[]{(int) w, (int) h};
    }

    /**
     * 最终效果要让最顶级节点在屏幕的中线上,
     * 在没有平移之前,所有的view的相对位置已经固定了,平移的方法就是找到顶级节点的x,看看和屏幕的中线差多少,这个就是offsetX
     *
     * @param offsetX view 需要平移的距离
     */
    private void translationAllView(TreeNode treeNode, float offsetX, float offsetY) {
        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
            treeNode.setLeftCenterX(treeNode.getLeftCenterX() + offsetX);
            treeNode.setLeftCenterY(treeNode.getLeftCenterY() + offsetY);
        }
        if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
            treeNode.setTopCenterX(treeNode.getTopCenterX() + offsetX);
            treeNode.setTopCenterY(treeNode.getTopCenterY() + offsetY);
        }

        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        if (subNodes.size() > 0) {
            for (TreeNode subNode : subNodes) {
                translationAllView(subNode, offsetX, offsetY);
            }
        }
    }

    private void generatePath(TreeNode node) {
        ArrayList<TreeNode> subNodes = node.getSubNodes();
        for (TreeNode subNode : subNodes) {
            Path path = createPath(node, subNode);
            mPaths.add(path);
            generatePath(subNode);
        }
    }

    /**
     * 生成 path
     *
     * @param superNode
     * @param subNode
     * @return
     */
    private Path createPath(TreeNode superNode, TreeNode subNode) {
        Path path = new Path();
        if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
            //拿到superNode的底部中心点 拿到subNode的顶部中心点
            float superX = superNode.getTopCenterX();
            float superY = superNode.getTopCenterY();
            //第一个点
            float startX = superX;
            float startY = superY + superNode.getHeight();
            //最后一个点
            float endX = subNode.getTopCenterX();
            float endY = subNode.getTopCenterY();
            //两个node的垂直距离
            float twoNodeVerticalSpacing = endY - startY;
            //起始竖线的高度
            float startLineHeight = twoNodeVerticalSpacing * startLinePercent;
            //第一条竖线的结尾点
            float startLineEndX = startX;
            float startLineEndY = startY + startLineHeight;
            //贝塞尔曲线的两个控制点
            float x1 = startLineEndX;
            float y1 = startLineEndY + (twoNodeVerticalSpacing / 2 - startLineHeight);
            float x2 = endX;
            float y2 = y1;
            float firstControlPointOffset = twoNodeVerticalSpacing * bezierCurveFirstControlPointOffsetPercent;
            float secondControlPointOffset = twoNodeVerticalSpacing * bezierCurveSecondControlPointOffsetPercent;
            //对两个控制点进行偏移
            y1 += firstControlPointOffset;
            y2 -= secondControlPointOffset;
            //结束竖线的高度
            float endLineHeight = twoNodeVerticalSpacing * endLinePercent;
            //贝塞尔曲线的结束点
            float x3 = endX;
            float y3 = endY - endLineHeight;

            path.moveTo(startX, startY);
            path.lineTo(startLineEndX, startLineEndY);
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.lineTo(endX, endY);
        }

        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
            //第一个点
            float startX = superNode.getRightCenterX();
            float startY = superNode.getRightCenterY();
            //最后一个点
            float endX = subNode.getLeftCenterX();
            float endY = subNode.getLeftCenterY();
            //两个node的水平距离
            float twoNodeHorizontalSpacing = endX - startX;
            //起始竖线的高度
            float startLineWidth = twoNodeHorizontalSpacing * startLinePercent;
            //第一条水平线的结尾点
            float startLineEndX = startX + startLineWidth;
            float startLineEndY = startY;
            //贝塞尔曲线的两个控制点
            float x1 = startLineEndX + (twoNodeHorizontalSpacing / 2 - startLineWidth);
            float y1 = startLineEndY;
            float x2 = x1;
            float y2 = endY;
            float firstControlPointOffset = twoNodeHorizontalSpacing * bezierCurveFirstControlPointOffsetPercent;
            float secondControlPointOffset = twoNodeHorizontalSpacing * bezierCurveSecondControlPointOffsetPercent;
            x1 += firstControlPointOffset;
            x2 -= secondControlPointOffset;
            float endLineWidth = twoNodeHorizontalSpacing * endLinePercent;
            float x3 = endX - endLineWidth;
            float y3 = endY;

            path.moveTo(startX, startY);
            path.lineTo(startLineEndX, startLineEndY);
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            path.lineTo(endX, endY);
        }
        return path;
    }

    /**
     * 测量出素所有的节点的宽度和高度
     * 注意:不同的绘制方向node的宽高是不一样的
     *
     * @param node
     * @return
     */
    private TreeNode measureNodeWH(TreeNode node) {
        ArrayList<TreeNode> subNodes = node.getSubNodes();
        int size = subNodes.size();
        if (size > 0) {//如果有子节点 那么这个父节点的宽度需要把所有的子节点的宽度累加
            if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
                float nodeHeight = 0;
                for (int i = 0; i < size; i++) {
                    TreeNode subNode = subNodes.get(i);
                    TreeNode treeNode = measureNodeWH(subNode);
                    nodeHeight += treeNode.getHeight();
                    if (i != size - 1) { //如果不是最后一个节点,那么节点之间的间距要考虑
                        nodeHeight += NODE_V_SPACING;
                    }
                }
                node.setWidth(node.getView().getMeasuredWidth());
                node.setHeight(nodeHeight);
                Log.d(TAG, "nodeHeight:" + nodeHeight);
            }

            if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
                float nodeWidth = 0;
                for (int i = 0; i < size; i++) {
                    TreeNode subNode = subNodes.get(i);
                    TreeNode treeNode = measureNodeWH(subNode);

                    nodeWidth += treeNode.getWidth();
                    if (i != size - 1) { //如果不是最后一个节点,那么节点之间的间距要考虑
                        nodeWidth += NODE_H_SPACING;
                    }
                }
                node.setWidth(nodeWidth);
                node.setHeight(node.getView().getMeasuredHeight());
            }
            return node;
        } else { //如果没有子节点 node的宽度就是node里面view的宽度
            node.setWidth(node.getView().getMeasuredWidth());
            node.setHeight(node.getView().getMeasuredHeight());
            return node; //其余情况 返回node的宽度
        }
    }

    /**
     * 测量子node的xy,如果没有子node,这个方法就不起作用
     * 从上往下绘制计算的是node的topCenterX,topCenterY
     * 从左往右绘制计算的是node的leftCenterX,leftCenterY
     */
    private void measureSubNodeXY(TreeNode node) {
        ArrayList<TreeNode> subNodes = node.getSubNodes();
        if (subNodes.size() > 0) {  //如果有子节点,就计算子节点的xy
            float centerX = 0; //
            float centerY = 0;
            for (int i = 0; i < subNodes.size(); i++) {
                //计算subNode的xy
                TreeNode subNode = subNodes.get(i);
                if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
                    subNode.setLeftCenterX(node.getRightCenterX() + node.getView().getMeasuredWidth() + NODE_H_SPACING);
                    if (i == 0) {
                        centerY = node.getRightCenterY() - node.getHeight() / 2 + subNode.getView().getMeasuredHeight() / 2;
                        subNode.setLeftCenterY(centerY);
                        centerY += subNode.getHeight() / 2;
                    } else {
                        centerY += subNode.getHeight() / 2 + NODE_V_SPACING;
                        subNode.setLeftCenterY(centerY);
                        centerY += subNode.getHeight() / 2;
                    }
                }

                if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
                    subNode.setTopCenterY(node.getTopCenterY() + node.getView().getMeasuredHeight() + NODE_V_SPACING);
                    //为下一个subNode的x做准备
                    if (i == 0) {
                        centerX = node.getTopCenterX() - node.getWidth() / 2 + subNode.getView().getMeasuredWidth() / 2;
                        subNode.setTopCenterX(centerX);
                        centerX += subNode.getWidth() / 2;
                    } else {
                        centerX += subNode.getWidth() / 2 + NODE_H_SPACING;
                        subNode.setTopCenterX(centerX);
                        centerX += subNode.getWidth() / 2;
                    }
                }

                //计算自己的范围
                float left = subNode.getLeft();
                if (left < mostLeftX) {
                    mostLeftX = left;
                }
                float right = subNode.getRight();
                if (right > mostRightX) {
                    mostRightX = right;
                }
                float bottom = subNode.getBottom();
                if (bottom > mostBottomY) {
                    mostBottomY = bottom;
                }
                float top = subNode.getTop();
                if (top < mostTopY) {
                    mostTopY = top;
                }
                measureSubNodeXY(subNode);
            }
        }
    }

    /**
     * 经过从子节点到父节点递归计算宽度,从父节点到子节点递归设置x(leftCenterX,topCenterX),发现superNode和subNode的相对位置有问题
     * 这个方法将从子节点到父节点调整superNode和subNode的相对关系,使得所有的superNode正好在所有的subNode的正上方
     */
    private void adjustXOrY(TreeNode node) {
        ArrayList<TreeNode> subNodes = node.getSubNodes();
        int size = subNodes.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                TreeNode subNode = subNodes.get(i);
                adjustXOrY(subNode);
            }
            if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
                //取出第一个subNode和最后一个subNode,将连个node的centerX的值之和的一半,赋值给superNode的centerX
                float firstX = subNodes.get(0).getTopCenterX();
                float lastX = subNodes.get(size - 1).getTopCenterX();
                float nodeX = (firstX + lastX) / 2;
                node.setTopCenterX(nodeX);
            }

            if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
                float firstY = subNodes.get(0).getLeftCenterY();
                float lastY = subNodes.get(size - 1).getLeftCenterY();
                float nodeY = (firstY + lastY) / 2;
                node.setLeftCenterY(nodeY);
            }
        }
    }

    /**
     * 据说这个方法就是用来确定 subView的left,top,right,bottom
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mTreeNode == null) {
            return;
        }
        onLayoutView(mTreeNode);
        //只有确定了view的位置,才能准确的绘制
        generateSelectedRect(mTreeNode);
    }

    private void onLayoutView(TreeNode node) {
        if (drawOrientation == ORIENTATION_TOP_BOTTOM) {
            int topCenterX = (int) node.getTopCenterX();
            int topCenterY = (int) node.getTopCenterY();
            View view = node.getView();
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int left = topCenterX - width / 2;
            int top = topCenterY;
            int right = topCenterX + width / 2;
            int bottom = topCenterY + height;
            view.layout(left, top, right, bottom);
        }

        if (drawOrientation == ORIENTATION_LEFT_RIGHT) {
            int leftCenterX = (int) node.getLeftCenterX();
            int leftCenterY = (int) node.getLeftCenterY();
            View view = node.getView();
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int left = leftCenterX;
            int top = leftCenterY - height / 2;
            int right = leftCenterX + width;
            int bottom = leftCenterY + height / 2;
            view.layout(left, top, right, bottom);
        }


        ArrayList<TreeNode> subNodes = node.getSubNodes();
        int size = subNodes.size();
        for (int i = 0; i < size; i++) {
            TreeNode subNode = subNodes.get(i);
            onLayoutView(subNode);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPaths(canvas, mPaths);
        if (mSelectedRect != null) {
            canvas.drawRect(mSelectedRect, mPathPaint);
        }
        //
//        drawNodeBorder(canvas, mTreeNode);
    }

    private void drawNodeBorder(Canvas canvas, TreeNode treeNode) {
        int x = (int) treeNode.getOriginY();
        int y = (int) treeNode.getOriginY();
        int width = (int) treeNode.getWidth();
        int height = (int) treeNode.getHeight();
        Rect rect = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rect, mPathPaint);
        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        for (TreeNode subNode : subNodes) {
            drawNodeBorder(canvas, subNode);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //判断的这个点是否在空白区域(没有点中任何一个view)
            float x = ev.getX();
            float y = ev.getY();
            boolean isClickView = isClickView(mTreeNode, x, y);
            if (!isClickView) { //如果没有点中view 那么说明点中了空白区域白
                requestFocus();
                if (mOnBlankAreaClickListener != null) {
                    mOnBlankAreaClickListener.onClick(x, y);
                }

                //如果有view被选中,取消选中状态
                setNoViewSelected(mTreeNode);
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_UP) {
            mSelectedRect = null;
            invalidate();
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }

    /**
     * 是否点中这个view的空白区域
     *
     * @return true:点中空白区域 false:点中view
     */
    private boolean isClickView(TreeNode treeNode, float downX, float downY) {
        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        if (subNodes.size() > 0) {
            for (TreeNode subNode : subNodes) {
                boolean clickView = isClickView(subNode, downX, downY);
                if (clickView) {
                    return true;
                }
            }
        }

        View view = treeNode.getView();
        float x = view.getX();
        float y = view.getY();
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        if (downX >= x && downX <= x + width && downY >= y && downY <= y + height) {
            //如果点中这个view 直接返回 这个递归结束
            return true;
        }
        return false;  //如果没有点中这个view并且这个节点没有子节点 就会返回false 但是这个并不表示
    }

    private void drawPaths(Canvas canvas, ArrayList<Path> paths) {
        for (Path path : paths) {
            canvas.drawPath(path, mPathPaint);
        }
    }

    /**
     * 点击那个view,就给那个view画一个边框圈起来
     */
    public void drawRect(View view) {
        setViewUnselected(mTreeNode);
        //通过这个view找到对应的node,
        setViewSelected(view, mTreeNode);
        generateSelectedRect(mTreeNode);
        invalidate();
    }

    /**
     * 取消所有的view的选中状态
     *
     * @param treeNode
     */
    public void setViewUnselected(TreeNode treeNode) {
        treeNode.setViewSelected(false);
        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        for (TreeNode subNode : subNodes) {
            setViewUnselected(subNode);
        }
    }

    /**
     * 给选中的view画一个边框
     *
     * @param treeNode
     */
    private void generateSelectedRect(TreeNode treeNode) {
        if (treeNode.isViewSelected()) {
            View view = treeNode.getView();
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int x = (int) view.getX();
            int y = (int) view.getY();
            mSelectedRect = new Rect(x, y, x + width, y + height);
            return;
        }
        for (TreeNode subNode : treeNode.getSubNodes()) {
            generateSelectedRect(subNode);
        }
    }

    /**
     * 只有选中view之后才会调用这个方法,所以这个方法的返回值一定是一个node
     *
     * @param view
     * @param treeNode
     * @return
     */
    TreeNode setViewSelected(View view, TreeNode treeNode) {
        ArrayList<TreeNode> subNodes = treeNode.getSubNodes();
        if (subNodes.size() > 0) {
            for (TreeNode subNode : subNodes) {
                TreeNode node = setViewSelected(view, subNode);
                if (node != null) {
                    return node;
                }
            }
        }
        View tempView = treeNode.getView();
        if (tempView == view) {
            treeNode.setViewSelected(true);
            return treeNode;
        }
        return null;
    }

    /**
     * 取消所有的view的选中状态
     *
     * @param treeNode
     */
    public void setNoViewSelected(TreeNode treeNode) {
        if (treeNode.isViewSelected()) {
            treeNode.setViewSelected(false);
        }
        for (TreeNode subNode : treeNode.getSubNodes()) {
            setNoViewSelected(subNode);
        }
    }

    private OnBlankAreaClickListener mOnBlankAreaClickListener;

    public void setOnBlankAreaClickListener(OnBlankAreaClickListener onBlankAreaClickListener) {
        this.mOnBlankAreaClickListener = onBlankAreaClickListener;
    }

}
