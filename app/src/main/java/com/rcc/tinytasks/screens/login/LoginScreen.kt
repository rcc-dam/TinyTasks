package com.rcc.tinytasks.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

import com.rcc.tinytasks.R
import com.rcc.tinytasks.navigation.Screens
import com.rcc.tinytasks.ui.theme.Light_Gray
import com.rcc.tinytasks.ui.theme.Dark_Gray
import com.rcc.tinytasks.ui.theme.Primary
import com.rcc.tinytasks.ui.theme.Secondary

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    val errorMessage = viewModel.snackbarMessage.observeAsState()

    // Google
    // este token se consigue en Firebase->Proveedores de Acceso->Google->Conf del SKD->Id de cliente web

    val token = "266786331906-nn28bc476rv00ert0ca6g72j8miv0iuq.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts
            .StartActivityForResult() // esto abrirá un activity para hacer el login de Google
    ) {
        val task =
            GoogleSignIn.getSignedInAccountFromIntent(it.data) // esto lo facilita la librería añadida
        // el intent será enviado cuando se lance el launcher
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló")
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(15.dp)
        ) {
            // Coloca la imagen en lugar del texto
            Image(
                painter = painterResource(id = R.drawable.logo_no_bg),
                contentDescription = "Logo de TinyTasks",
                modifier = Modifier.width(200.dp).height(300.dp),
                contentScale = ContentScale.Crop // Recorta y centra la imagen
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Comprobar si es inicio de sesión o registro

            Text(
                text = if (showLoginForm.value) "Inicia Sesión" else "Crear una cuenta",
                color = Primary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .background(color = Dark_Gray, shape = RoundedCornerShape(15.dp))
                    .padding(15.dp)
            ) {

                if (showLoginForm.value) {
                    UserForm(isCreateAccount = false) { email, password ->
                        Log.d("My Login", "Logueando con $email y $password")
                        viewModel.signInWithEmailAndPassword(
                            email,
                            password
                        ) {//pasamos email, password, y la funcion que navega hacia home
                            navController.navigate(Screens.HomeScreen.name)
                        }
                    }
                } else {
                    UserForm(isCreateAccount = true) { email, password ->
                        Log.d("My Login", "Creando cuenta con $email y $password")
                        viewModel.createUserWithEmailAndPassword(
                            email,
                            password
                        ) {//pasamos email, password, y la funcion que navega hacia home
                            navController.navigate(Screens.HomeScreen.name)
                        }

                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // GOOGLE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        //.background()
                        .clickable { // Se crea un buider de opciones, una de ellas incluye un token

                            val opciones = GoogleSignInOptions
                                .Builder(
                                    GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                .requestIdToken(token) //requiere el token
                                .requestEmail() //y tb requiere el email
                                .build()
                            //creamos un cliente de logueo con estas opciones
                            val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
                            launcher.launch(googleSingInCliente.signInIntent)
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_google_medio),
                        contentDescription = "Login con GOOGLE",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(40.dp)
                    )

                    Text(
                        text = "Login con Google",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // alternar entre Crear cuenta e iniciar sesion
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val text1 = if (showLoginForm.value) "¿No tienes cuenta?"
                    else "¿Ya tienes cuenta?"

                    val text2 = if (showLoginForm.value) "Registrate"
                    else "Inicia sesión"

                    Text(
                        text = text1,
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "|",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = text2,
                        modifier = Modifier
                            .clickable {
                                showLoginForm.value = !showLoginForm.value
                            }
                            .padding(start = 5.dp),
                        color = Primary,
                        fontSize = 14.sp
                    )
                }
            }

        }

        // Dialog para mostrar errores
        errorMessage.value?.let { message ->
            AlertDialog(
                onDismissRequest = {
                    // Limpia el mensaje para cerrar el diálogo
                    viewModel.clearErrorMessage()
                },
                title = {
                    Text(text = "Error", color = Light_Gray, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(text = message, color = Light_Gray)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearErrorMessage()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary,
                            contentColor = Primary,
                            disabledContainerColor = Color(0xFF101010),
                            disabledContentColor = Color.Gray,
                        ),
                    ) {
                        Text("OK")
                    }
                },
                containerColor = Dark_Gray
            )
        }
    }

}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {

    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }

    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    //controla que al hacer clic en el boton submite, el teclado se oculte
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(
            emailState = email
        )

        Spacer(modifier = Modifier.width(15.dp))

        PasswordInput(
            passwordState = password,
            passwordVisible = passwordVisible
        )

        Spacer(modifier = Modifier.width(15.dp))

        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ) {
            onDone(email.value.trim(), password.value.trim())
            //se oculta el teclado, el ? es que se llama la función en modo seguro
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Secondary,
            contentColor = Primary,
            disabledContainerColor = Color(0xFF101010),
            disabledContentColor = Color.Gray,
        ),
        enabled = inputValido,
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        //label = { Text(text = labelId, color = Color.White) },
        placeholder = { Text(text = labelId, color = Color.White) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White
            )
        },
        singleLine = isSingleLine,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            //.padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .padding(8.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.White,
            containerColor = Color.Gray,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = Color.White,
            unfocusedPlaceholderColor = Color.White,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String = "Password",
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        //label = { Text(text = labelId) },
        placeholder = { Text(text = labelId, color = Color.White) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            //.padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            //.fillMaxWidth()
            //.background(Color.Gray.copy(alpha = 0.2f)),
            .padding(8.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.White,
            containerColor = Color.Gray,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = Color.White,
            unfocusedPlaceholderColor = Color.White,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image = if (passwordVisible.value)
        Icons.Default.VisibilityOff
    else
        Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = "",
            tint = Color.White
        )
    }
}