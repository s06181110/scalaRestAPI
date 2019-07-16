package models

import javax.inject.{Inject, Singleton}

import play.api.db.Database
import play.api.libs.json.{JsValue, Json, Reads, Writes}

import anorm._
import anorm.SqlParser._


 /**
  * SQLのpostsからPostを作り出す為のクラス
  * @param id  投稿自身のID
  * @param user_id  投稿をしたユーザのID
  * @param text  投稿の本文
  * @param parent_post_id  コメントした元の投稿のID (普通の投稿の場合 NULL)
  * @param comment_count コメントされた数
  * @param posted_at 投稿された時間
  */
case class Post(id: String, user_id: String, text: String, parent_post_id: Option[String], comment_count: Int, posted_at: String)
object Post{
  val parser = str("id") ~ str("user_id") ~ str("text") ~ get[Option[String]]("parent_post_id") ~ int("comment_count") ~ date("posted_at")
  val mapper = parser.map{ case id~userId~text~parentPostId~commentCount~postedAt => Post(id, userId, text, parentPostId, commentCount, postedAt.toString) }
  implicit val postWrites: Writes[Post] = Json.writes[Post]
  implicit val postReads: Reads[Post] = Json.reads[Post]
}

 /**
  * 投稿をJson形式に変換できるようにします
  * @param posts
  */
case class PostResponse(posts: JsValue)
object PostResponse {
  implicit val responseWrites: Writes[PostResponse] = Json.writes[PostResponse]
}

 /**
  * コメントをJson形式に変換できるようにします
  * @param comments
  */
case class CommentResponse(comments: JsValue)
object CommentResponse {
  implicit val responseWrites: Writes[CommentResponse] = Json.writes[CommentResponse]
}

 /**
  * エラー文をJson形式に変換できるようにします
  * @param result
  * @param message
  */
case class ErrorResponse(result: String="NG", message: Seq[String])
object ErrorResponse {
  implicit val errorWrites: Writes[ErrorResponse] = Json.writes[ErrorResponse]
}

 /**
  * postの為のクラスです。データベースと接続し、投稿、取得などを行います
  * @param db defaultのデータベース [conf/evolutions/default/1.sql]
  */
@Singleton
class PostRepository @Inject()(db: Database){

    /**
     * SQLから投稿を時系列順に取得します
     * @return 投稿一覧
     */
  def getPosts: List[Post] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM posts ORDER BY posted_at DESC").as(Post.mapper.*)
    }
  }

    /**
     * 投稿をもらいに行き、Json形式に変換します
     * @return Json形式の投稿一覧
     */
  def postList: JsValue = {
    Json.toJson(PostResponse(Json.toJson(getPosts)))
  }

    /**
     * SQLからコメントを取得します
     * @param id コメント元の投稿ID
     * @return コメント一覧
     */
  def getComments(id: String): List[Post] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM posts WHERE parent_post_id = {id}").on("id"->id).as(Post.mapper.*)
    }
  }

    /**
     * コメントをもらいに行き、Json形式に変換します
     * @param id コメント元の投稿ID
     * @return Json形式のコメント一覧
     */
  def commentList(id: String): JsValue = {
    Json.toJson(CommentResponse(Json.toJson(getComments(id))))
  }

    /**
     * 本文の長さが適切か判断します
     * @param text 本文
     * @return 真理値
     */
  def isValidSize(text: String): Boolean = text.isEmpty || 100 < text.length

    /**
     * Insertする前に情報が適切かチェックします
     * @param userId  ユーザID
     * @param text  本文
     * @param parentPostId  コメント元のID
     * @return エラーメッセージ
     */
  def checkInsert(userId: String, text: String, parentPostId: String): List[String] = {
    val user: Option[User] = userFindById(userId)
    var message = List[String]()
    
    if(user.isEmpty) message +:= "user is not exist"
    if(isValidSize(text)) message +:= "comment length must have 1 ~ 100"

    // idが指定されているが、見つからない
    if(parentPostId != null){
      val parentPost: Option[Post] = postFindById(parentPostId)
      if(parentPost.isEmpty){
        message +:= "illegal parent post ID"
      }else if(message.isEmpty){
        updateCommentCount(parentPostId)
      }
    }

    message
  }

    /**
     * 投稿をデータベースに追加します
     * @param userId  ユーザID
     * @param text  本文
     * @param parentPostId  コメント元のID
     * @return インサートの結果
     */
  def insert(userId: String, text: String, parentPostId: String = null): JsValue = {
    val errorMessage: List[String] = checkInsert(userId, text, parentPostId)

    // エラーメッセージが返ってきてなければ投稿許可
    if(errorMessage.isEmpty){
      // uuid v4 を生成
      val uuid: String = java.util.UUID.randomUUID.toString

      db.withConnection { implicit c =>
        SQL("INSERT INTO posts(id ,user_id, text, parent_post_id) VALUES({id}, {user_id}, {text}, {parent_post_id})")
          .on("id"->uuid, "user_id"->userId, "text"->text, "parent_post_id"->parentPostId).executeInsert()
      }
      Json.obj("result" -> "OK")
    }else{
      Json.toJson(ErrorResponse("NG",errorMessage))
    }
  }

    /**
     * idからユーザを探します
     * @param id ユーザID
     * @return ユーザオブジェクト
     */
  def userFindById(id: String): Option[User] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM test_users WHERE id = {id}").on("id" -> id).as(User.mapper.singleOpt)
    }
  }

    /**
     * idから投稿を探します
     * @param id 投稿のID
     * @return 投稿のオブジェクト
     */
  def postFindById(id: String): Option[Post] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM posts WHERE id = {id}").on("id" -> id).as(Post.mapper.singleOpt)
    }
  }

    /**
     * コメントが追加された時に、親元のカウント数を増やします
     * @param id 投稿のID
     * @return 何も返さない
     */
  def updateCommentCount(id: String): Int = {
    db.withConnection { implicit c =>
      SQL("UPDATE posts SET comment_count = comment_count + 1 WHERE id = {id}").on("id" -> id).executeUpdate()
    }
  }
}
