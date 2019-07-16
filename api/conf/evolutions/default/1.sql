-- test_users schema

-- !Ups

CREATE TABLE test_users (
  id varchar(36) NOT NULL,
  name varchar(255) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE posts (
  id varchar(36) NOT NULL,
  user_id varchar(36) NOT NULL,
  text  varchar(100) NOT NULL,
  parent_post_id varchar(36) DEFAULT(NULL),
  comment_count INT NOT NULL DEFAULT(0),
  posted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SET @user_1 = '11111111-1111-1111-1111-111111111111';
SET @user_2 = '22222222-2222-2222-2222-222222222222';
SET @user_3 = '33333333-3333-3333-3333-333333333333';

INSERT INTO test_users(id, name) VALUES (@user_1, 'alice');
INSERT INTO test_users(id, name) VALUES (@user_2, 'bob');
INSERT INTO test_users(id, name) VALUES (@user_3, 'charlie');

-- !Downs
DROP TABLE test_users;
DROP TABLE posts
