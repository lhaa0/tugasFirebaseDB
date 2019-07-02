package com.rizki.androidlatihan15_firebasedb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterBook : RecyclerView.Adapter<AdapterBook.BukuViewHolder> {

    lateinit var mContext : Context
    lateinit var itemBuku : List<ModelBook>

    constructor(){}
    constructor(mContext : Context, list: List<ModelBook>){
        this.mContext = mContext
        itemBuku = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.show_data, parent, false)
        val bukuViewHolder = BukuViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemBuku.size
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        val bukuModel = itemBuku.get(position)
        holder.tv_penulis.text = bukuModel.penulis
        holder.tv_tanggal.text = bukuModel.tanggal
        holder.tv_judul.text = bukuModel.judulBuku
        holder.ll_contact.setOnClickListener {
            //
        }
    }

    inner class BukuViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        var ll_contact : LinearLayout
        var tv_penulis : TextView
        var tv_tanggal : TextView
        var tv_judul : TextView
        init {
            ll_contact = itemview.findViewById(R.id.ll_contact)
            tv_penulis = itemview.findViewById(R.id.tv_penulis)
            tv_judul = itemview.findViewById(R.id.tv_judul)
            tv_tanggal = itemview.findViewById(R.id.tv_tanggal)
        }
    }
}