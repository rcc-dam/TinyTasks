package com.rcc.tinytasks.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rcc.tinytasks.model.User
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            handleFirebaseError(task.exception)
                        }
                    }
            } catch (ex: Exception) {
                handleError(ex.message)
            }
        }

    fun signInWithFacebook(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Facebook logueado!!!!")
                            home()
                        } else {
                            handleFirebaseError(task.exception)
                        }
                    }
            } catch (ex: Exception) {
                handleError(ex.message)
            }
        }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "signInWithEmailAndPassword logueado!!!!")
                            home()
                        } else {
                            handleFirebaseError(task.exception)
                        }
                    }
            } catch (ex: Exception) {
                handleError(ex.message)
            }
        }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            handleError("Por favor, ingresa un correo electrónico válido.")
            return
        }
        if (password.length < 6) {
            handleError("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _loading.value = false
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName)
                        home()
                    } else {
                        handleFirebaseError(task.exception)
                    }
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = User(
            userId = userId.toString(),
            displayName = displayName.toString()
        ).toMap()

        FirebaseFirestore.getInstance()
            .collection("users")
            .add(user)
            .addOnSuccessListener {
                Log.d("MyLogin", "Creado ${it.id}")
            }
            .addOnFailureListener {
                handleError("Ocurrió un error al crear el usuario: ${it.message}")
            }
    }

    private fun handleFirebaseError(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthInvalidUserException -> "Usuario no encontrado."
            is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo."
            else -> exception?.message ?: "Error desconocido."
        }
        handleError(message)
    }

    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> get() = _snackbarMessage

    fun handleError(message: String?) {
        _errorMessage.value = message ?: "Error desconocido."
        Log.d("MyLogin", _errorMessage.value!!)

        // Establecer el mensaje del Snackbar
        _snackbarMessage.value = _errorMessage.value
    }

    fun clearErrorMessage() {
        _snackbarMessage.value = null
    }

}
