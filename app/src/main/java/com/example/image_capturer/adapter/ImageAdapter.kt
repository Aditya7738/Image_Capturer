package com.example.image_capturer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.image_capturer.R
import com.example.image_capturer.ShowImageActivity
import com.example.image_capturer.model.ImageData

class ImageAdapter(private val context: Context, private val imageList: List<ImageData>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.capture_image_view)
        val textView: TextView = itemView.findViewById(R.id.image_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)
        Toast.makeText(parent.context, "View Holder Created", Toast.LENGTH_LONG).show()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageData = imageList[position]
        holder.imageView.setImageBitmap(imageData.bitmap)
        holder.textView.text = imageData.name

        Toast.makeText(context, "imageData.name ${imageData.name}", Toast.LENGTH_LONG).show()
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowImageActivity::class.java)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
