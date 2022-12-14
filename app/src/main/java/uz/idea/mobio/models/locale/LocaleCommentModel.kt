package uz.idea.mobio.models.locale

import java.util.LinkedList
const val user_image_url = "data:image/png;base64,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"
const val user_image_url1 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9ksfRe5m8sEQfamLivERt-RdItL7WthgA4A&usqp=CAU"
const val user_image_url2 = "https://s.dou.ua/storage-files/image3_6mpW7yb.jpg"
data class LocaleCommentModel(
    val imageUser:String?=null,
    val name:String?=null,
    val rate:Float?=null,
    val time:String?=null,
    val comment:String?=null
){
    fun getCommentList():List<LocaleCommentModel>{
        val listComment = LinkedList<LocaleCommentModel>()
        listComment.add(LocaleCommentModel(user_image_url,"Dsotonbek Eshmurodov",3.4F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        listComment.add(LocaleCommentModel(user_image_url1,"Dsotonbek Eshmurodov",3F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        listComment.add(LocaleCommentModel(user_image_url2,"Dsotonbek Eshmurodov",3.2F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        listComment.add(LocaleCommentModel(user_image_url,"Dsotonbek Eshmurodov",3.1F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        listComment.add(LocaleCommentModel(user_image_url1,"Dsotonbek Eshmurodov",2.4F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        listComment.add(LocaleCommentModel(user_image_url2,"Dsotonbek Eshmurodov",2.2F,"Bugun 20:00","Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah"))
        return listComment
    }
}