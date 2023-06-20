package com.example.sqliteimagedemo

import android.Manifest
import android.R
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 23
    var imageView: ImageView? = null
    var db: SQLiteDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Permission to access external storage
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
        imageView = findViewById<View>(R.id.imageView) as ImageView
        //creating database
        db = this.openOrCreateDatabase("test.db", MODE_PRIVATE, null)
        //creating table for storing image
        db.execSQL("create table if not exists imageTb ( image blob )")
    }

    fun viewImage(view: View?) {
        val c = db!!.rawQuery("select * from imageTb", null)
        if (c.moveToNext()) {
            val image = c.getBlob(0)
            val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
            imageView!!.setImageBitmap(bmp)
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    fun fetchImage(view: View?) {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/demoImage.jpg/"
        )
        val fis = FileInputStream(folder)
        val image = ByteArray(fis.available())
        fis.read(image)
        val values = ContentValues()
        values.put("image", image)
        db!!.insert("imageTb", null, values)
        fis.close()
        Toast.makeText(this, "Image Fetched", Toast.LENGTH_SHORT).show()
    }
}