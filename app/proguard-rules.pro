# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class uz.idea.mobio.models.auth.* { *; }
-keep class uz.idea.mobio.models.basketModel.addBasket.addBasketReq.* { *; }
-keep class uz.idea.mobio.models.basketModel.addBasket.addBasketRes.* { *; }
-keep class uz.idea.mobio.models.basketModel.basketList.* { *; }
-keep class uz.idea.mobio.models.basketModel.deleteBasket.reqDeleteBasket.* { *; }
-keep class uz.idea.mobio.models.basketModel.deleteBasket.resDeleteBasket.* { *; }
-keep class uz.idea.mobio.models.basketModel.udateBasket.resUpdateBasket* { *; }
-keep class uz.idea.mobio.models.categoryModel* { *; }
-keep class uz.idea.mobio.models.categoryProductModel* { *; }
-keep class uz.idea.mobio.models.comment.commentList* { *; }
-keep class uz.idea.mobio.models.comment.saveComment.resSaveComment* { *; }
-keep class uz.idea.mobio.models.comment.saveComment* { *; }
-keep class uz.idea.mobio.models.errors.errorLogin* { *; }
-keep class uz.idea.mobio.models.errors.errorRegister* { *; }
-keep class uz.idea.mobio.models.favoritesData.favoritesReq* { *; }
-keep class uz.idea.mobio.models.favoritesData.resSaveFavorite* { *; }
-keep class uz.idea.mobio.models.locale* { *; }
-keep class uz.idea.mobio.models.logOutModel* { *; }
-keep class uz.idea.mobio.models.mainChildModel* { *; }
-keep class uz.idea.mobio.models.mainProduct* { *; }
-keep class uz.idea.mobio.models.newProductModel* { *; }
-keep class uz.idea.mobio.models.productModel* { *; }
-keep class uz.idea.mobio.models.register* { *; }
-keep class uz.idea.mobio.models.searchModel* { *; }
-keep class uz.idea.mobio.models.userDataModel* { *; }