package com.solbios.model.productDetails

data class Data(
    val detail: Detail,
    val packet_sizes: List<PacketSize>,
    val similar_products: List<SimilarProduct>,
    val cart_total: Int

    )