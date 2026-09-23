package com.example.a4499grill

import com.google.firebase.Timestamp

data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Int = 0,
    val imageUrl: Int = 0 
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val orderDate: Timestamp = Timestamp.now(),
    var paymentStatus: String = "Awaiting Payment", // Awaiting Payment, Paid
    var orderStatus: String = "Received", // Received, Grilling, Out for Delivery, Completed
    val orderNumber: String = ""
)

data class CommunityPost(
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfilePic: String = "",
    val caption: String = "",
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String = "", // Added for video support
    val timestamp: Timestamp = Timestamp.now(),
    var likes: Int = 0,
    var likedBy: List<String> = emptyList(),
    var comments: List<Comment> = emptyList(),
    var rating: Float = 0f // Added for ratings
)

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
