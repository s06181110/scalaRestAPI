package controllers

import models.PostRepository
import javax.inject.Inject

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

 /**
  * Postに関するコントローラです
  * @param post PostRepositoryのモデル
  * @param cc コントローラのコンポーネント
  */
class PostController @Inject()(post: PostRepository, cc: ControllerComponents) extends AbstractController (cc) {

   /**
    * 投稿を見やすくして返します
    * @return レスポンス
    */
  def getPosts = Action {
    Ok(Json.prettyPrint(post.postList)).as("application/json")
  }

    /**
     * リクエストを受け取り、投稿を追加します
     * @return レスポンス
     */
  def addPost = Action { implicit request: Request[AnyContent] =>
    val param: Option[JsValue] = request.body.asJson
    val id: String = param.get("user_id").toString().replace(""""""", "")
    val text: String =  param.get("text").toString().replace(""""""", "")
    val res: JsValue = post.insert(id, text)
    if(res.equals(Json.obj("result"->"OK"))){
      Ok(res).as("application/json")
    }else{
      Status(400)(res)
    }
  }

    /**
     * コメントを見やすくして出力します
     * @param parentPostId  投稿元のID
     * @return レスポンス
     */
  def getComments(parentPostId: String) = Action {
    Ok(Json.prettyPrint(post.commentList(parentPostId))).as("application/json")
  }

    /**
     * リクエストを受け取り、コメントを追加します
     * @param parentPostId 投稿元のID
     * @return レスポンス
     */
  def addComment(parentPostId: String) = Action { implicit request: Request[AnyContent] =>
    val param: Option[JsValue] = request.body.asJson
    val id: String = param.get("user_id").toString().replace(""""""", "")
    val text: String =  param.get("text").toString().replace(""""""", "")
    val res: JsValue = post.insert(id, text, parentPostId)

    if(res.equals(Json.obj("result"->"OK"))){
      Ok(res).as("application/json")
    }else{
      Status(400)(res)
    }
  }
}

