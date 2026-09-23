package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityActivity : AppCompatActivity() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmpty: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        
        window.statusBarColor = getColor(R.color.light_cream)

        rvPosts = findViewById(R.id.rv_community_posts)
        pbLoading = findViewById(R.id.pb_community)
        tvEmpty = findViewById(R.id.tv_empty_community)

        rvPosts.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.btn_create_post).setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        setupBottomNavigation()
        fetchPosts()
    }

    private fun fetchPosts() {
        pbLoading.visibility = View.VISIBLE
        db.collection("community")
            .addSnapshotListener { snapshots, e ->
                pbLoading.visibility = View.GONE
                if (e != null) {
                    Toast.makeText(this, "Error fetching posts", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                var posts = snapshots?.toObjects(CommunityPost::class.java)
                    ?.sortedByDescending { it.timestamp } ?: emptyList()
                
                // Add default authentic mock content if Firestore is completely empty
                if (posts.isEmpty()) {
                    posts = listOf(
                        CommunityPost(
                            postId = "mock_1",
                            userName = "Chef Thabo",
                            caption = "Just took these fresh, juicy Wood-Fired Smash Burgers off the flames! Who is ready for lunch? 🔥🍔",
                            likes = 42,
                            rating = 5.0f,
                            comments = listOf(Comment(userName = "Alice", text = "Best burgers in town!"))
                        ),
                        CommunityPost(
                            postId = "mock_2",
                            userName = "Sarah Jenkins",
                            caption = "The Sticky BBQ Wings here are absolutely addictive! Crispy, sticky, and perfect flavor profile.",
                            likes = 18,
                            rating = 4.5f
                        )
                    )
                }

                tvEmpty.visibility = View.GONE
                rvPosts.visibility = View.VISIBLE
                rvPosts.adapter = CommunityAdapter(posts)
            }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.nav_explore_btn)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_orders_btn)?.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_profile_btn)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    inner class CommunityAdapter(private val posts: List<CommunityPost>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvUser: TextView = view.findViewById(R.id.tv_user_name)
            val tvCaption: TextView = view.findViewById(R.id.tv_post_caption)
            val tvTime: TextView = view.findViewById(R.id.tv_post_time)
            val ivPostImage: ImageView = view.findViewById(R.id.iv_post_image)
            val ivAvatar: ImageView = view.findViewById(R.id.iv_user_avatar)
            val tvLikes: TextView = view.findViewById(R.id.tv_likes_count)
            val tvComments: TextView = view.findViewById(R.id.tv_comments_count)
            val ratingBar: RatingBar? = view.findViewById(R.id.post_rating_bar)
            val btnLike: View = view.findViewById(R.id.btn_like)
            val etComment: EditText? = view.findViewById(R.id.et_comment_input)
            val btnSendComment: View? = view.findViewById(R.id.btn_send_comment)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community_post, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = posts[position]
            holder.tvUser.text = post.userName
            holder.tvCaption.text = post.caption
            holder.tvLikes.text = post.likes.toString()
            holder.tvComments.text = post.comments.size.toString()
            holder.tvTime.text = "Just now"

            holder.ratingBar?.rating = post.rating

            holder.btnLike.setOnClickListener {
                post.likes++
                holder.tvLikes.text = post.likes.toString()
                if (!post.postId.startsWith("mock_")) {
                    db.collection("community").document(post.postId).update("likes", post.likes)
                }
            }

            holder.btnSendComment?.setOnClickListener {
                val txt = holder.etComment?.text?.toString()?.trim() ?: ""
                if (txt.isNotEmpty()) {
                    val currentUserName = auth.currentUser?.email?.substringBefore("@") ?: "User"
                    val newComment = Comment(userName = currentUserName, text = txt)
                    val updatedComments = post.comments.toMutableList().apply { add(newComment) }
                    
                    holder.etComment?.text?.clear()
                    holder.tvComments.text = updatedComments.size.toString()
                    Toast.makeText(this@CommunityActivity, "Comment added!", Toast.LENGTH_SHORT).show()
                    
                    if (!post.postId.startsWith("mock_")) {
                        db.collection("community").document(post.postId).update("comments", updatedComments)
                    }
                }
            }

            if (post.imageUrls.isNotEmpty()) {
                holder.ivPostImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(post.imageUrls[0]).into(holder.ivPostImage)
            } else {
                // Show default placeholders for mock items to remain vibrant
                if (post.postId == "mock_1") {
                    holder.ivPostImage.visibility = View.VISIBLE
                    holder.ivPostImage.setImageResource(R.drawable.food_burger_hero)
                } else if (post.postId == "mock_2") {
                    holder.ivPostImage.visibility = View.VISIBLE
                    holder.ivPostImage.setImageResource(R.drawable.food_sticky_wings)
                } else {
                    holder.ivPostImage.visibility = View.GONE
                }
            }

            holder.ivAvatar.setImageResource(R.drawable.bg_circle_icon)
        }

        override fun getItemCount() = posts.size
    }
}
