package com.pinterest.pinterestfirebase.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.pinterest.pinterestfirebase.data.model.Usuarios
import kotlinx.coroutines.tasks.await

class UserRepository() {

    suspend fun actualizarCuenta(email: String, firstName: String, lastName: String, imageUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw Exception("No hay usuario autenticado")

        // Actualiza nombre completo e imagen
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$firstName $lastName")
            .setPhotoUri(Uri.parse(imageUrl))
            .build()

        user.updateProfile(profileUpdates).await()

        // (Opcional) Si también quieres actualizar el email
        if (user.email != email) {
            user.updateEmail(email).await()
        }

        Log.d("ActualizarCuenta", "Perfil actualizado correctamente")
    }


    fun obtenerUsuarioActual(onResult: (Usuarios?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val partes = user?.displayName?.trim()?.split(" ")

        val apellidoPaterno = partes?.getOrNull(0) ?: ""
        val apellidoMaterno = partes?.getOrNull(1) ?: ""

        if (uid == null) {
            onResult(null) // No hay usuario logueado
            return
        }

        if(user != null){
            val usuario: Usuarios = Usuarios()
            usuario.imagenUrl = user.photoUrl.toString()
            usuario.email = user.email.toString()
            usuario.firstName = apellidoPaterno
            usuario.lastName = apellidoMaterno

            onResult(usuario)
        }

        onResult(null)

    }

}