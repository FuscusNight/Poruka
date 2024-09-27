package poruka.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class AuthRepository {

    // Set up the Firebase tools needed
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val storage = FirebaseStorage.getInstance()

    // Suspend fun can pause its execution without blocking the main thread and resume later, making it asynchronous
    suspend fun registerUser(email: String, password: String, userName: String): Result<String> {
        return try {
                                                                                        // Waits till async task complete
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            val userId = authResult.user?.uid ?: ""

            // Default profile picture URL
            val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/poruka-d701c.appspot.com/o/DefaultProfileJPG.jpg?alt=media&token=3999e5c2-04ef-426d-a9f4-17ad605cf1e1"

            // Represents user data that gets stored to Firestore, organized in a waz so Firestore can easily understand and store it
            val user = hashMapOf(
                "userId" to userId,
                "email" to email,
                "userName" to userName,
                "profilePictureUrl" to defaultProfileImageUrl
            )
            firestore.collection("users").document(userId).set(user).await()
            Result.success("User registered successfully!")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Log in for existing users
    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success("Login successful!")
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
    // Fetching current logged user ID
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    // List of maps where each maps represent a friend and holds key value pairs (such as user ID, name,email)
    suspend fun getFriends(): Result<List<Map<String, String>>> {
        // Checks if user is logged in even, if not function fails
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users").document(userId).collection("friends").get().await()
            //For each document, creates a map (Map<String, String>) contains mail,userid,name
            val friends = snapshot.documents.map { document ->
                mapOf(
                    "userId" to document.getString("userId").orEmpty(),
                    "userName" to document.getString("userName").orEmpty(),
                    "email" to document.getString("email").orEmpty()
                )
            }
            Result.success(friends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Send friend request
    suspend fun sendFriendRequest(searchQuery: String): Result<String> {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            return try {
                val snapshot = firestore.collection("users")
                    .whereEqualTo("email", searchQuery)
                    .get().await()

                if(snapshot.isEmpty) {
                    val usernameSnapshot = firestore.collection("users")
                        .whereEqualTo("userName", searchQuery)
                        .get().await()

                    if(usernameSnapshot.isEmpty) {
                        return Result.failure(Exception("No user found with that email or username"))

                    } else {
                        // retrieves the data for the friend as a Map<String, Any>. This map contains fields like the friend's userId, userName, and email.
                                                                // access first document returned by the query
                        val recipientDocument = usernameSnapshot.documents[0]
                        val recipientId = recipientDocument.id

                        // Create friend request data
                        val friendRequest = hashMapOf(
                            "senderId" to userId,
                            "status" to "pending"
                        )
                        /**
                         * firestore.collection("users") - Goes into users collection in firestore
                         * .document(userId): Fetches ID of the users that is getting the friend request.
                         *
                         * .collection("friendRequest"): Accesses (or creates, if it doesn't exist) a sub-collection
                         * called friendRequest inside the current user's document. This sub-collection will hold the list of friends for that user.
                         *
                         * .set(friendData).await(): This stores the friend's data (from the map friendData) in the document. The await() ensures
                         * that the function waits for the Firestore write operation to complete before proceeding.
                         */
                        firestore.collection("users").document(recipientId)
                            .collection("friendRequests").document(userId).set(friendRequest).await()

                        return Result.success("Friend request sent successfully!")
                    }

            } else {
                val recipientDocument = snapshot.documents[0]
                val recipientId = recipientDocument.id

                val friendRequest = hashMapOf(
                    "senderId" to userId,
                    "status" to "pending"
                )

                firestore.collection("users").document(recipientId)
                    .collection("friendRequests").document(userId).set(friendRequest).await()

                return Result.success("Friend request sent successfully!")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Get friend request
    suspend fun getFriendRequest(): Result<List<Map<String, String>>> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("friendRequests").whereEqualTo("status", "pending").get().await()

            val friendRequests = snapshot.documents.map { document ->
                val senderId = document.getString("senderId").orEmpty()

                // Fetch the sender's name from the "users" collection
                val senderSnapshot = firestore.collection("users").document(senderId).get().await()
                val senderName = senderSnapshot.getString("userName").orEmpty()

                mapOf(
                    "senderId" to document.getString("senderId").orEmpty(),
                    "senderName" to senderName,
                    "status" to document.getString("status").orEmpty()
                )
            }
            Result.success(friendRequests)
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    // Accepting friend requests
    suspend fun acceptFriendRequest(senderId: String): Result<String> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {

            // Get current user's data
            val userSnapshot = firestore.collection("users").document(userId).get().await()
            val currentUserData = userSnapshot.data ?: return Result.failure(Exception("Current user not found"))

            // Get sender's data
            val senderSnapshot = firestore.collection("users").document(senderId).get().await()
            val senderData = senderSnapshot.data ?: return Result.failure(Exception("Sender not found"))

            //adding each user to the other's frendo list
            firestore.collection("users").document(userId)
                .collection("friends").document(senderId).set(senderData).await()
            firestore.collection("users").document(senderId)
                .collection("friends").document(userId).set(currentUserData).await()

            // Remove friend request from recipient's collection
            firestore.collection("users").document(userId)
                .collection("friendRequests").document(senderId).delete().await()

            Result.success("Friend request accepted!")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reject a friend request
    suspend fun rejectFriendRequest(senderId: String): Result<String> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection("users").document(userId)
                .collection("friendRequests").document(senderId).delete().await()

            Result.success("Friend request rejected!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reauthenticate the user before updating sensitive data like email or password
    suspend fun reauthenticateUser(currentPassword: String): Result<FirebaseUser> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Send email verification
    suspend fun sendEmailVerification(): Result<String> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            user.sendEmailVerification().await()
            Result.success("Verification email sent successfully!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if email is verified
    suspend fun isEmailVerified(): Boolean {
        val user = firebaseAuth.currentUser
        return user?.isEmailVerified ?: false
    }

    // Update email, password, and username
    suspend fun updateUserDetails(
        currentPassword: String,
        newUsername: String?,
        newEmail: String?,
        newPassword: String?
    ): Result<String> {
        return try {
            val user = reauthenticateUser(currentPassword).getOrThrow()

            val userId = user.uid
            val updatedFields = mutableMapOf<String, Any>()

            if (!newUsername.isNullOrEmpty()) {
                updatedFields["userName"] = newUsername
                // Updates username in the user's own document
                firestore.collection("users").document(userId)
                    .update("userName", newUsername).await()

                // Updates username in all friends' documents
                val friendsSnapshot = firestore.collection("users").document(userId)
                    .collection("friends").get().await()
                for (friend in friendsSnapshot.documents) {
                    val friendId = friend.getString("userId").orEmpty()
                    firestore.collection("users").document(friendId)
                        .collection("friends").document(userId)
                        .update("userName", newUsername).await()
                }
            }

            if (!newEmail.isNullOrEmpty()) {
                updatedFields["email"] = newEmail
                // Verify before updating email in Firebase Authentication
                user.verifyBeforeUpdateEmail(newEmail).await()
                firestore.collection("users").document(userId)
                    .update("email", newEmail).await()

                // Updates email in all friends' documents
                val friendsSnapshot = firestore.collection("users").document(userId)
                    .collection("friends").get().await()
                for (friend in friendsSnapshot.documents) {
                    val friendId = friend.getString("userId").orEmpty()
                    firestore.collection("users").document(friendId)
                        .collection("friends").document(userId)
                        .update("email", newEmail).await()
                }
            }

            if (!newPassword.isNullOrEmpty()) {
                user.updatePassword(newPassword).await()
            }

            Result.success("User details updated successfully!")
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun reloadUser(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
            user.reload().await()  // This reloads the user's session
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Uploads the profile picture and returns the download URL
    suspend fun uploadProfilePicture(userId: String, imageUri: Uri, context: Context): Result<String> {
        return try {
            val storageRef = storage.reference.child("profile_pictures/$userId.jpg")

            // Log message indicating the image upload process started
            println("Uploading image for user: $userId")

            // Resize the image before uploading
            val bitmap = resizeImageUri(context, imageUri)

            println("Image successfully resized")

            val baos = ByteArrayOutputStream()
            val isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            // Log if the compression was successful
            if (isCompressed) {
                println("Image compressed successfully")
            } else {
                throw Exception("Image compression failed")
            }

            val imageData = baos.toByteArray()
            println("Uploading image data of size: ${imageData.size} bytes")

            // Upload the image to Firebase Storage
            storageRef.putBytes(imageData).await()

            println("Image uploaded successfully, fetching download URL")

            // Fetch the download URL
            val downloadUrl = storageRef.downloadUrl.await()

            // Update Firestore with the new profile picture URL
            firestore.collection("users").document(userId)
                .update("profilePictureUrl", downloadUrl.toString()).await()

            println("Profile picture URL updated in Firestore")

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            println("Error uploading image: ${e.localizedMessage}")
            Result.failure(Exception("Failed to upload image: ${e.localizedMessage}"))
        }
    }



    suspend fun resizeImageUri(context: Context, imageUri: Uri): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ImageDecoder for API level 28+
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            // For older Android versions
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }

        // Resize the image to 256x256
        return Bitmap.createScaledBitmap(bitmap, 256, 256, true)
    }
}





