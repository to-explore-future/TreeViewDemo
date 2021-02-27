package com.zq.treeviewdemo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.fs.treeview.Node
import com.fs.treeview.TreeNode
import com.fs.treeview.TreeViewAdapter
import com.fs.treeview.TreeViewGroup

class MainActivity : AppCompatActivity() {
    val instance by lazy { this }
    private var mTreeViewGroup: TreeViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTreeViewGroup = findViewById(R.id.tree_view_group)


        //展示tree
//        // 解析数据
        val temp =
            "{\"name\":\"树\",\"url\":\"\",\"subNode\":[{\"name\":\"榆树\",\"url\":\"\",\"subNode\":[{\"name\":\"榆树1\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树2\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树3\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树4\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树5\",\"url\":\"\",\"subNode\":[{\"name\":\"松树1\",\"url\":\"\",\"subNode\":[]},{\"name\":\"松树2\",\"url\":\"\",\"subNode\":[]},{\"name\":\"松树3\",\"url\":\"\",\"subNode\":[]},{\"name\":\"松树4\",\"url\":\"\",\"subNode\":[]},{\"name\":\"松树5\",\"url\":\"\",\"subNode\":[]},{\"name\":\"松树6\",\"url\":\"\",\"subNode\":[]}]},{\"name\":\"榆树6\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树7\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树8\",\"url\":\"\",\"subNode\":[]},{\"name\":\"榆树9\",\"url\":\"\",\"subNode\":[]}]},{\"name\":\"银杏树\",\"url\":\"\",\"subNode\":[]},{\"name\":\"凤凰树\",\"url\":\"\",\"subNode\":[]},{\"name\":\"臭椿树\",\"url\":\"\",\"subNode\":[]},{\"name\":\"梧桐树\",\"url\":\"\",\"subNode\":[{\"name\":\"梧桐树1\",\"url\":\"\",\"subNode\":[]},{\"name\":\"梧桐树2\",\"url\":\"\",\"subNode\":[]},{\"name\":\"梧桐树3\",\"url\":\"\",\"subNode\":[]},{\"name\":\"梧桐树4\",\"url\":\"\",\"subNode\":[]},{\"name\":\"梧桐树5\",\"url\":\"\",\"subNode\":[]}]}]}"
//        String temp1 = "{\"name\":\"树\",\"url\":\"\",\"subData\":[{\"name\":\"榆树\",\"url\":\"\",\"subData\":[{\"name\":\"榆树1\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树2\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树3\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树4\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树5\",\"url\":\"\",\"subData\":[{\"name\":\"松树1\",\"url\":\"\",\"subData\":[]},{\"name\":\"松树2\",\"url\":\"\",\"subData\":[]},{\"name\":\"松树3\",\"url\":\"\",\"subData\":[]},{\"name\":\"松树4\",\"url\":\"\",\"subData\":[]},{\"name\":\"松树5\",\"url\":\"\",\"subData\":[]},{\"name\":\"松树6\",\"url\":\"\",\"subData\":[]}]},{\"name\":\"榆树6\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树7\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树8\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树9\",\"url\":\"\",\"subData\":[]}]},{\"name\":\"银杏树\",\"url\":\"\",\"subData\":[]},{\"name\":\"凤凰树\",\"url\":\"\",\"subData\":[]},{\"name\":\"臭椿树\",\"url\":\"\",\"subData\":[]}]}";
//        String small = "{\"name\":\"树\",\"url\":\"\",\"subData\":[{\"name\":\"榆树\",\"url\":\"\",\"subData\":[{\"name\":\"榆树1\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树2\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树3\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树4\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树5\",\"url\":\"\",\"subData\":[]},{\"name\":\"榆树6\",\"url\":\"\",\"subData\":[]}]}]}";
//        String soSmall = "{\"name\":\"树\",\"url\":\"\",\"subData\":[{\"name\":\"榆树\",\"url\":\"\",\"subData\":[]}]}";
        val soSmall1 = "{\"name\":\"title\",\"url\":\"\",\"subData\":[]}"

        var mNode: Node = JSON.parseObject(temp, Node::class.java)
        updateView(mNode)
    }

    private fun updateView(node: Node) {
        //模拟adapter
        mTreeViewGroup?.setAdapter(object : TreeViewAdapter(node) {
            override fun getView(treeNode: TreeNode): View {
                val view = View.inflate(instance, R.layout.item, null)
                val tv = view.findViewById<EditText>(R.id.tv)
                tv.setText(treeNode.node.name)
                val img = view.findViewById<ImageView>(R.id.img)

                return view
            }
        })

    }
}