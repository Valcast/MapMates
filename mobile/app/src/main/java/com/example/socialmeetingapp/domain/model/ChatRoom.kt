package com.example.socialmeetingapp.domain.model

data class ChatRoom(
    val id: String,
    val authorId: String,
    val name: String,
    val members: List<String>,
    val lastMessage: Message? = null,
    val authorOnlyWrite: Boolean = false
) {
    companion object {
        val EMPTY = ChatRoom(
            id = "",
            authorId = "",
            name = "",
            members = emptyList()
        )
    }
}