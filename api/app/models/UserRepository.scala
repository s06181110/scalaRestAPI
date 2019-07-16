package models

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import anorm._
import anorm.SqlParser._

 /**
  * SQLのtest_usersからUserを作り出す為のクラス
  * @param id ユーザID
  * @param name 名前
  * @param created_at 作成日時
  */
case class User(id: String, name: String, created_at: Date)
object User {
  val parser = str("id") ~ str("name") ~ date("created_at")
  val mapper = parser.map{ case id~name~createdAt => User(id,name,createdAt) }
  implicit val jsonWrites: Writes[User] = Json.writes[User]
  implicit val jsonReads: Reads[User] = Json.reads[User]
}

 /**
  * ユーザをJson形式に変換できるようにします
  * @param users
  */
case class UserResponse(users: JsValue)
object UserResponse {
  implicit val jsonWrites: Writes[UserResponse] = Json.writes[UserResponse]
}

  /**
   * 今回は使いません
   *
   * ユーザの為のクラスです。データベースと接続し、投稿、取得などを行います
   * @param db defaultのデータベース [conf/evolutions/default/1.sql]
   */

@Singleton
class UserRepository @Inject()(db: Database){

   /**
    * 今回は使いません
    *
    * SQLからtest_usersを取得し、成形します
    * @return
    */
  def getUsers: List[User] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM test_users").as(User.mapper.*)
    }
  }

   /**
    * 今回は使いません
    *
    * ユーザをJson成形します
    * @return
    */
  def userList: JsValue = {
    Json.toJson(UserResponse(Json.toJson(getUsers)))
  }

   /**
    * 今回は使いません
    *
    * SQLより、特定のユーザを探します
    * @param id ユーザID
    * @return 該当するユーザ
    */
  def findById(id: String): Option[User] = {
    db.withConnection { implicit c =>
      SQL("SELECT * FROM test_users WHERE id = {id}").on("id" -> id).as(User.mapper.singleOpt)
    }
  }
}
