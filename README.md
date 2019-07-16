# scalaRestAPI

* API
  * 投稿一覧
  * 新規投稿作成
  * 投稿へのコメント一覧
  * 投稿へのコメント作成

## launch
```bash
$ cd api
$ sbt
$ run
```

## test
* 投稿一覧
```bash
$ curl -i -s http://localhost:9000/posts
```
* 新規投稿作成
```bash
$ curl -i -s http://localhost:9000/posts/create \
-X POST -H "Content-Type: application/json" \
-d '{"user_id":"11111111-1111-1111-1111-111111111111","text":"a"}'
```
* 投稿へのコメント一覧
```bash
$ curl -i -s http://localhost:9000/posts/:post_id/comments
```
* 投稿へのコメント作成
```bash
$ curl -i -s http://localhost:9000/posts/:post_id/comments/create \
-X POST -H "Content-Type: application/json" \
-d '{"user_id":"11111111-1111-1111-1111-111111111111","text":"a"}'
```
