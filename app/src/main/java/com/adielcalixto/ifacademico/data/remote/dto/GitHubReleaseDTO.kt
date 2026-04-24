package com.adielcalixto.ifacademico.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubReleaseDTO(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String,
    @SerializedName("body") val body: String?,
    @SerializedName("html_url") val htmlUrl: String
)
