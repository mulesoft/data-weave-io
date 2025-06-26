%dw 2.0

type BlobContent = {
  id: String,
  name: String,
  size: Number,
  mediaType: String
}

fun create(name: String, mediaType: String, content: Binary): BlobContent = ???