package com.nenne.presentation.util

import android.util.Log
import android.widget.TextView
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

@BindingAdapter("app:setInfoTextByType")
fun TextView.setTextByType(type : ShopType?){
    when(type){
        ShopType.AUTO ->{
            this.text = "자동"
        }
        else->{
            this.text = "셀프"
        }
    }
}