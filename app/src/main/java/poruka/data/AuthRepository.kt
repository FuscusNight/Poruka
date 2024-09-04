package poruka.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    // Set up the Firebase tools needed
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Suspend fun can pause its execution without blocking the main thread and resume later, making it asynchronous
    suspend fun registerUser(email: String, password: String, userName: String): Result<String> {
        return try {
                                                                                        // Waits till async task complete
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            val userId = authResult.user?.uid ?: ""

            // Represents user data that gets stored to Firestore, organized in a waz so Firestore can easily understand and store it
            val user = hashMapOf(
                "userId" to userId,
                "email" to email,
                "userName" to userName
            )
            firestore.collection("users").document(userId).set(user).await()
            Result.success("User registered successfully!")

        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}