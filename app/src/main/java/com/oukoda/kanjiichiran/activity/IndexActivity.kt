package com.oukoda.kanjiichiran.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.R
import com.oukoda.kanjiichiran.RecyclerAdapter
import com.oukoda.kanjiichiran.databinding.ActivityIndexBinding


class IndexActivity : AppCompatActivity(){
    companion object{
        private val TAG : String? = IndexActivity::class.simpleName
        interface WordSelectListener{
            fun onSelectWord(wordIndex: Int)
        }
    }
    private lateinit var binding: ActivityIndexBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val wordList = (application as MyApplication).wordList
        val listener: WordSelectListener = object : WordSelectListener {
            override fun onSelectWord(wordIndex: Int) {
                val intent = Intent(applicationContext, WordActivity::class.java)
                intent.putExtra(getString(R.string.intent_word_index_key), wordIndex)
                startActivity(intent)
                finish()
            }
        }
        var wordIndex = intent.getIntExtra(getString(R.string.intent_word_index_key), -1)
        if (wordIndex == -1){
            Log.d(TAG, "onCreate: ")
            wordIndex = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).getInt(getString(R.string.saved_word_index_key),
                0
            )
            Log.d(TAG, "onCreate: $wordIndex")

        }
        binding.RecyclerList.adapter = RecyclerAdapter(wordList, listener)
        binding.RecyclerList.layoutManager = LinearLayoutManager(this)
        binding.RecyclerList.scrollToPosition(wordIndex)
    }
}