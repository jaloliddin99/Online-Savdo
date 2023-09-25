package org.don.onlineTrade.ui.add

import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.auth.textFieldStateSaver
import java.util.regex.Pattern


class ProductTitleState(val title: String? = null) :
    TextFieldState() {
    init {
        title?.let {
            text = it
        }
    }
}


val ProductTitleStateSaver = textFieldStateSaver(ProductTitleState())
val ProductDescriptionStateSaver = textFieldStateSaver(ProductTitleState())
val CategoryStateSaver = textFieldStateSaver(ProductTitleState())
val RegionStateSaver = textFieldStateSaver(ProductTitleState())
val ProductPriceStateSaver = textFieldStateSaver(ProductTitleState())