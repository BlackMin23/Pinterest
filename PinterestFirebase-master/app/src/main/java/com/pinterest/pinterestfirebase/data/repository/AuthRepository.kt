package com.pinterest.pinterestfirebase.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Registra un nuevo usuario con correo electrónico y contraseña.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return Un objeto Result<Boolean> que indica éxito o fracaso.
     */
    suspend fun registerUser(email: String, password: String, firstName: String, lastName: String,ImageUrl: String ): Result<Boolean> {
        return try {
            // Crea el usuario solo con email y contraseña

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user?: throw Exception("Usuario no creado")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .setPhotoUri(Uri.parse(ImageUrl))
                .build()

            user.updateProfile(profileUpdates).await()



            // Si el usuario se creó correctamente, guarda información adicional en Firestore
//            user?.let { firebaseUser ->
//                val userData = hashMapOf(
//                    "email" to firebaseUser.email,
//                    "firstName" to firstName,
//                    "lastName" to lastName,
//                    "imagenUrl" to ImageUrl,
//                    "createdAt" to System.currentTimeMillis()
//                    // Puedes añadir más campos de perfil aquí si es necesario
//                )
//                // Guarda el UID del usuario como ID del documento en la colección 'users'
//                firestore.collection("users").document(firebaseUser.uid).set(userData).await()
//            }
            Result.success(true) // Registro exitoso
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e) // Fallo en el registro
        }
    }

    /**
     * Inicia sesión de un usuario con correo electrónico y contraseña.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return Un objeto Result<Boolean> que indica éxito o fracaso.
     */
    suspend fun loginUser(email: String, password: String): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true) // Inicio de sesión exitoso
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e) // Fallo en el inicio de sesión
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun logoutUser() {
        firebaseAuth.signOut()
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     * @return El UID del usuario si está autenticado, o null si no lo está.
     */
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    /**
     * Verifica si hay un usuario actualmente autenticado.
     * @return true si hay un usuario autenticado, false en caso contrario.
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }


    /**
     * Obtiene los datos del usuario actual desde Firestore
     */
    suspend fun getUserData(userId: String): Result<Map<String, Any>> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                Result.success(document.data!!)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos del usuario en Firestore
     */
    suspend fun updateUserData(
        userId: String,
        updates: Map<String, Any>
    ): Result<Boolean> {
        return try {
            firestore.collection("users").document(userId).update(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}