package cn.onestravel.fivefiveplayer.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.*
import cn.onestravel.fivefiveplayer.R
import cn.onestravel.fivefiveplayer.utils.VideoUtils
import org.w3c.dom.Text


/**
 * @author onestravel
 * @createTime 2020-03-21
 * @description 选择器view
 */
typealias OnSelectedCallback = (position: Int, selectedData: String) -> Unit

open class SelectorPopView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mDatas: Array<String>
    private val listView: ListView by lazy { ListView(context) }

    init {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setPadding(VideoUtils.dp2px(context, 40f), 0, VideoUtils.dp2px(context, 40f), 0)
        setBackgroundColor(resources.getColor(R.color.five_color_window_bg_color))
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        listView.layoutParams = lp
        listView.divider = ColorDrawable(Color.WHITE)
        listView.dividerHeight = VideoUtils.dp2px(context, 1f)
        listView.isScrollContainer = false
        listView.selector = ColorDrawable(Color.TRANSPARENT)
        addView(listView)
    }

    fun setData(datas: Array<String>, selectData: String) {
        mDatas = datas
        val adapter = DataAdapter(context, datas, selectData);
        listView?.let {
            it.adapter = adapter
        }
    }


    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    fun setonSelectedCallBack(onSelectedCallback: OnSelectedCallback) {
        listView.setOnItemClickListener { parent, view, position, id ->
            mDatas?.let {
                onSelectedCallback.invoke(position, it[position])
            }
        }
    }


    class DataAdapter(context: Context, val datas: Array<String>, val selectData: String) :
        ArrayAdapter<String>(context, R.layout.five_item_selector, datas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            context?.let {
                view.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,VideoUtils.dp2px(it,40f))
                if (view is TextView) {
                    if (datas.get(position) == selectData) {
                        view.setTextColor(it.resources.getColor(R.color.five_color_select_color))
                    } else {
                        view.setTextColor(it.resources.getColor(R.color.five_color_default_color))
                    }
                }
            }
            return view
        }
    }
}