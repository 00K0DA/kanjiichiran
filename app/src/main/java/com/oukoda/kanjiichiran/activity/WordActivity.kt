package com.oukoda.kanjiichiran.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.R
import com.oukoda.kanjiichiran.databinding.ActivityWordBinding

class WordActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = WordActivity::class.simpleName
    }
    private lateinit var binding: ActivityWordBinding
    private var wordIndex: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wordIndex = intent.getIntExtra(getString(R.string.saved_word_index_key), 0)
        val wordListLength = (application as MyApplication).wordList.size
        binding.btNext.setOnClickListener {
            wordIndex += 1
            if (wordIndex >= wordListLength) {
                wordIndex = wordListLength - 1
            }
            setText()
            saveSelectedText()
        }
        binding.btPrevious.setOnClickListener {
            wordIndex -= 1
            if (wordIndex <= 0) {
                wordIndex = 0
            }
            setText()
            saveSelectedText()
        }
        binding.btIndex.setOnClickListener {
            val intent = Intent(applicationContext, IndexActivity::class.java)
            intent.putExtra(getString(R.string.intent_word_index_key), wordIndex)
            startActivity(intent)
            finish()
        }
        setText()
        saveSelectedText()
    }
    private fun setText() {
        val word = (application as MyApplication).wordList[wordIndex]
        binding.tvId.text = word.id.toString()
        binding.tvWord.text = word.word
        binding.tvDescriptor.text = word.descriptor
        binding.tvYomi.text = word.yomi
    }

    private fun saveSelectedText() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getString(R.string.saved_word_index_key), wordIndex)
            apply()
        }
    }
}
