package com.solbios.mapper

import com.solbios.db.entities.SearchData
import com.solbios.model.search.Data


fun Data.toSearchDataEntity() : SearchData {
     val searchData= SearchData(
         /*this . brand_id,
         this . cart_product_count,
         this . category_id,
         this . created_at,
         this . description,
         this . expiry_date,
         this . format_created_at,*/
         this.id,
        this.image,
       this .title,
         this .sub_title

     )
    return searchData
}
