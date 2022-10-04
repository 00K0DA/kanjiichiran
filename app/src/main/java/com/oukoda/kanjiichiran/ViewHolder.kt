package com.oukoda.kanjiichiran

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oukoda.kanjiichiran.activity.IndexActivity

class ViewHolder(private val item: View, private val listener: IndexActivity.Companion.WordSelectListener) : RecyclerView.ViewHolder(item), View.OnClickListener {
    companion object {
        private val TAG: String? = ViewHolder::class.simpleName
    }

    var idTextView: TextView
    var wordTextView: TextView

    init {
        item.setOnClickListener(this)
        idTextView = item.findViewById(R.id.tv_id)
        wordTextView = item.findViewById(R.id.tv_word)
    }

    // implement for OnClickListener
    override fun onClick(view: View) {
        Log.d(TAG, "onClick: layoutPosition=$layoutPosition")
        listener.onSelectWord(layoutPosition)
    }
}
