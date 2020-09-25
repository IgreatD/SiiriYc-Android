package com.siiri.yc.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qmuiteam.qmui.kotlin.onClick
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.siiri.yc.R
import com.siiri.yc.ui.activity.MainActivity
import kotlinx.android.synthetic.main.item_drop_down.view.*

/**
 * @author: dinglei
 * @date: 2020/9/25 13:49
 */
object SwitchIpUtil {

    fun switchIp(activity: Activity) {
        val autoUrls = mutableListOf("172.16.40.23", "117.158.214.190")
        autoUrls.addAll(UserUtils.getInputHistories())
        val dialog = QMUIDialog.CustomDialogBuilder(activity)
            .setLayout(R.layout.layout_switch_url)
            .setTitle("切换IP地址")
            .setCanceledOnTouchOutside(true)
            .setCancelable(true)
            .addAction("取消") { dialog, _ -> dialog.dismiss() }
            .addAction("确定") { dialog, _ ->
                val inputUrl =
                    dialog.findViewById<AppCompatEditText>(R.id.etUrl)?.text.toString()
                val ip = RegexUtils.isIP(inputUrl)
                if (!ip) {
                    ToastUtils.showShort("请输入或者选择正确的IP地址")
                    return@addAction
                }
                UserUtils.webViewIP = inputUrl
                if (!autoUrls.contains(inputUrl)) {
                    UserUtils.addInputHistory(inputUrl)
                }
                dialog.dismiss()
                activity.finish()
                ActivityUtils.startActivity(Intent(activity, MainActivity::class.java))
            }.create()
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerview)
        if (recyclerView != null)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(activity)
                val baseQuickAdapter =
                    object : BaseQuickAdapter<String, BaseViewHolder>(
                        R.layout.item_drop_down
                    ) {
                        override fun convert(holder: BaseViewHolder, item: String) {
                            holder.setText(R.id.tvUrl, item)
                            holder.itemView.iv_clear.onClick {
                                UserUtils.removeInputHistory(item)
                                removeAt(holder.layoutPosition)
                            }
                            holder.itemView.onClick {
                                dialog.findViewById<AppCompatEditText>(R.id.etUrl)?.setText(item)
                            }
                        }
                    }
                baseQuickAdapter.setList(autoUrls)
                adapter = baseQuickAdapter
            }
        val arrow = dialog.findViewById<AppCompatImageView>(R.id.iv_show)
        arrow?.onClick {
            if (recyclerView?.isVisible == true) {
                recyclerView.visibility = View.GONE
                arrow.setImageResource(
                    R.drawable.ic_arrow_down_24
                )
            } else {
                recyclerView?.visibility = View.VISIBLE
                arrow.setImageResource(
                    R.drawable.ic_arrow_up_24
                )
            }
        }
        dialog.show()
    }

}