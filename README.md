# intern2021_Summer_EnomotoYoshiki

* インターン課題内容
  * 投稿一覧
  * 新規投稿作成
  * 投稿へのコメント一覧
  * 投稿へのコメント作成

## エラーについて
[check_result.txt](https://github.com/f81/intern2021_Summer_EnomotoYoshiki/blob/master/check_result.txt)は最初のバージョンです。エラー内容が１つだけ出力されます。

[check_result_multiple_error.txt](https://github.com/f81/intern2021_Summer_EnomotoYoshiki/blob/master/check_result_multiple_error.txt)が現在のバージョンです。該当するエラー内容が全て出力されます。

## launch
```bash
$ cd fringe81-intern
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
