package com.rizki.androidlatihan15_firebasedb

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class AdapterBook : RecyclerView.Adapter<AdapterBook.BukuViewHolder> {

    lateinit var mContext : Context
    lateinit var itemBuku : List<ModelBook>
    lateinit var listener: FirebaseDataListener

    constructor(){}
    constructor(mContext : Context, list: List<ModelBook>){
        this.mContext = mContext
        itemBuku = list
        this.listener = mContext as MainPage
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
        holder.ll_contact.setOnLongClickListener(object :View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage("Pilih operasi Data!!")
                builder.setPositiveButton("Update"){
                    dialog, i ->
                    listener.onUpdateData(bukuModel)
                }
                builder.setNegativeButton("Delete") {
                    dialog, i ->
                    listener.onDeleteData(bukuModel)
                }

                val dialog = builder.create()
                dialog.show()

                return true
            }
        })
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

    interface FirebaseDataListener {
        fun onDeleteData(buku : ModelBook)
        fun onUpdateData(buku : ModelBook)
    }
}