package com.example.a4499grill

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existing = items.find { it.id == item.id }
        if (existing != null) {
            existing.quantity += item.quantity
        } else {
            items.add(item)
        }
    }

    fun getItems(): List<CartItem> = items

    fun getTotal(): Double = items.sumOf { it.price * it.quantity }

    fun clear() {
        items.clear()
    }
    
    fun isEmpty() = items.isEmpty()
}
