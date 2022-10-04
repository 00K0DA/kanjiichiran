package com.oukoda.kanjiichiran.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.databinding.ActivityIndexBinding
import com.oukoda.kanjiichiran.databinding.ActivityMainBinding

class MainActivity  : AppCompatActivity(){
    companion object{
        private val TAG : String? = WordActivity::class.simpleName
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btIndexActivity.setOnClickListener {
            setDisableButton(true)
            val intent = Intent(applicationContext, IndexActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btFourWord.setOnClickListener {
            setDisableButton(true)
            val intent = Intent(applicationContext, MeaningFourWordActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btWriteFourWord.setOnClickListener {
            setDisableButton(true)
            val intent = Intent(applicationContext, WriteFourWordActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btReadActivity.setOnClickListener {
            setDisableButton(true)
            val intent = Intent(applicationContext, ReadActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setDisableButton(disableFlag: Boolean){
        binding.btIndexActivity.isEnabled = !disableFlag
        binding.btFourWord.isEnabled = !disableFlag
        binding.btWriteFourWord.isEnabled = !disableFlag
        binding.btReadActivity.isEnabled = !disableFlag
    }
}