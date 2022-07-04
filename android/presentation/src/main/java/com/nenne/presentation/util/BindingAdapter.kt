package com.nenne.presentation.util

import android.util.Log
import androidx.databinding.BindingAdapter
import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nocompany.presentation.R
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("app:autoAndSelfImg")
fun CircleImageView.setImgFromDrawable(type: ShopType?) {
    when (type) {
        ShopType.AUTO -> {
            this.setImageResource(R.drawable.auto_text_img)
        }
        else -> {
            this.setImageResource(R.drawable.self_text_img)
        }
    }

}