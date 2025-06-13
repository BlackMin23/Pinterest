package com.pinterest.pinterestfirebase.data.model

import com.google.firebase.firestore.DocumentId

data class PublicacionN (
    @DocumentId
    var id: String = "",
    var name: String = "",
    var descripcion: String = "",
    var ownerId: String = ""
)