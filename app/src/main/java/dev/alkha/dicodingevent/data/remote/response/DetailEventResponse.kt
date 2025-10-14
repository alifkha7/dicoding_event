package dev.alkha.dicodingevent.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailEventResponse(

	@field:SerializedName("event")
	val event: EventItem,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)