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

    suspend fun getFriendRequest(): Result<List<Map<String, String>>> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("friendRequests").whereEqualTo("status", "pending").get().await()

            val friendRequests = snapshot.documents.map { document ->
                mapOf(
                    "senderId" to document.getString("senderId").orEmpty(),
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
            firestore.collection("users").document(userId)
                .collection("friendRequests").document(senderId).update("status", "accepted").await()

            // Get sender's user details
            val senderSnapshot = firestore.collection("users").document(senderId).get().await()
            val userData = senderSnapshot.data ?: return Result.failure(Exception("Sender not found"))
            firestore.collection("users").document(senderId)
                .collection("friends").document(userId).set(userData).await()

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
                .collection("friendRequests").document(senderId).update("status", "rejected").await()

            Result.success("Friend request rejected!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}





    /* Adding friends by searching email or username
    suspend fun addFriend(searchQuery: String): Result<String> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", searchQuery)
                .get().await()

            if (snapshot.isEmpty) {
                val usernameSnapshot = firestore.collection("users")
                    .whereEqualTo("userName", searchQuery)
                    .get().await()

                if (usernameSnapshot.isEmpty) {
                    return Result.failure(Exception("No user found with that email or username"))
                } else {
                    // retrieves the data for the friend as a Map<String, Any>. This map contains fields like the friend's userId, userName, and email.
                                                         // access first document returned by the quqery
                    val friendDocument = usernameSnapshot.documents[0]

                    val friendData = friendDocument.data as Map<String, Any>
                    /**
                     * firestore.collection("users") - Goes into users collection in firestore
                     * .document(userId): Fetches the current logged-in user's document by their userId.
                     *
                     * .collection("friends"): Accesses (or creates, if it doesn't exist) a sub-collection
                     * called friends inside the current user's document. This sub-collection will hold the list of friends for that user.
                     *
                     * .document(friendDocument.id): Inside the friends sub-collection, it creates a new document for the friend. The friend's
                     * document ID is based on their own userId (which was retrieved from the friend search).
                     *
                     * .set(friendData).await(): This stores the friend's data (from the map friendData) in the document. The await() ensures
                     * that the function waits for the Firestore write operation to complete before proceeding.
                      */
                    firestore.collection("users").document(userId).collection("friends").document(friendDocument.id).set(friendData).await()
                    return Result.success("Friend added successfully")
                }

            } else {
                // Same as before, this is just if we used an email to look for the friend to add
                val friendDocument = snapshot.documents[0]
                val friendData = friendDocument.data as Map<String,Any>

                firestore.collection("users").document(userId).collection("friends").document(friendDocument.id).set(friendData).await()
                return Result.success("Friend added successfully!")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     */




