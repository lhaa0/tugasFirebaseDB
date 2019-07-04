package com.rizki.androidlatihan15_firebasedb

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ModelBook (

    var penulis : String? = null,
    var tanggal : String? = null,
    var judulBuku : String? = null,
    var desc : String? = null,
    var key : String? = null

) : Serializable