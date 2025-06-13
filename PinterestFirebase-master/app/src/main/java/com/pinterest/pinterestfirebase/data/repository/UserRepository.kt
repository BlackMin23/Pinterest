package com.pinterest.pinterestfirebase.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.model.Usuarios
import java.io.File

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val mascotasRef = db.collection("mascotas")

    // Crear libro
    fun agregarLibro(mascota: Usuarios, imagenUri: Uri?, onComplete: (Boolean) -> Unit) {
        try {
            // Si viene con ID → editar, si no → crear nuevo documento
            val docRef = if (mascota.id.isNotEmpty()) {
                mascotasRef.document(mascota.id)
            } else {
                mascotasRef.document()
            }

            // UID del usuario actual
            val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

            // Solo asignar nuevo ID si estamos creando un nuevo libro
            val libroActualizado = if (mascota.id.isNotEmpty()) {
                mascota
            } else {
                mascota.copy(id = docRef.id)
            }

            // ✅ Agregar el usuarioId
            val libroConUsuario = libroActualizado.copy(ownerId = uid)

            docRef.set(libroConUsuario)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }

        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false)
        }
    }




    // Obtener todos los libros
    fun obtenerLibros(onResult: (List<Mascota>) -> Unit) {
        mascotasRef.get().addOnSuccessListener { snapshot ->
            val lista = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Mascota::class.java)?.copy(id = doc.id)
            }
            onResult(lista)
        }
    }

    // Actualizar libro
    fun actualizarLibro(mascota: Mascota, onComplete: (Boolean) -> Unit) {
        mascotasRef.document(mascota.id).set(mascota)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Eliminar libro
    fun eliminarLibro(id: String, onComplete: (Boolean) -> Unit) {
        mascotasRef.document(id).get().addOnSuccessListener { doc ->
            val mascota = doc.toObject(Mascota::class.java)

            // 🔁 Borra la imagen local si existe
            mascota?.imagenUrl?.let { ruta ->
                try {
                    val archivo = File(ruta)
                    if (archivo.exists()) {
                        archivo.delete()
                        Log.d("FirebaseRepo", "Imagen local eliminada: $ruta")
                    } else {
                        Log.d("FirebaseRepo", "No se encontró la imagen en: $ruta")
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseRepo", "Error al borrar la imagen local", e)
                }
            }

            // 🔥 Luego elimina el documento de Firestore
            mascotasRef.document(id).delete()
                .addOnSuccessListener {
                    Log.d("FirebaseRepo", "Libro eliminado con éxito: $id")
                    onComplete(true)
                }
                .addOnFailureListener { ex ->
                    Log.e("FirebaseRepo", "Error al eliminar libro en Firestore", ex)
                    onComplete(false)
                }

        }.addOnFailureListener { ex ->
            Log.e("FirebaseRepo", "Error al obtener el documento antes de eliminar", ex)
            onComplete(false)
        }
    }
}