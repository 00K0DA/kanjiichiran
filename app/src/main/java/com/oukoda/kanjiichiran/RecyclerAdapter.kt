package com.oukoda.kanjiichiran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oukoda.kanjiichiran.activity.IndexActivity
import com.oukoda.kanjiichiran.dataclass.Word

class RecyclerAdapter(private val wordList: List<Word>, private val listener: IndexActivity.Companion.WordSelectListener) : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_list, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.idTextView.text = wordList[position].id.toString()
        holder.wordTextView.text = wordList[position].word
    }

    override fun getItemCount(): Int = wordList.size

}