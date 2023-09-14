/*
 Navicat Premium Data Transfer

 Source Server         : 61.243.3.19
 Source Server Type    : PostgreSQL
 Source Server Version : 130004
 Source Host           : 61.243.3.19:5432
 Source Catalog        : blue
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 130004
 File Encoding         : 65001

 Date: 14/09/2023 10:05:37
*/


-- ----------------------------
-- Table structure for t_relation
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_relation";
CREATE TABLE "public"."t_relation" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "username" varchar(32) COLLATE "pg_catalog"."default",
  "watch_user" varchar(32) COLLATE "pg_catalog"."default",
  "permit" int4
)
;

-- ----------------------------
-- Records of t_relation
-- ----------------------------
INSERT INTO "public"."t_relation" VALUES ('hgrskdjecesaa', '18385471840', '15885417470', 1);
INSERT INTO "public"."t_relation" VALUES ('hgrsdsfaedsa', '13965114730', '18385640839', 1);
INSERT INTO "public"."t_relation" VALUES ('hgrskdjkkjha', '18385471840', '18385640839', 1);
INSERT INTO "public"."t_relation" VALUES ('d224a06698e643dd8bb61448407503e1', '18385471840', '15117801779', 1);
INSERT INTO "public"."t_relation" VALUES ('hgrsfaedsa', '13965114730', '18385471840', 1);
INSERT INTO "public"."t_relation" VALUES ('bvdkdjvcdsha', '18385640839', '18385471840', 1);
INSERT INTO "public"."t_relation" VALUES ('a9f7a710b97044e0acbbd45b1e95115d', '15117801779', '15885417470', 1);
INSERT INTO "public"."t_relation" VALUES ('03fa863eeafe4965b1086f066f74059c', '15117801779', '18385471840', 1);
INSERT INTO "public"."t_relation" VALUES ('0473be28a9ec4cfc8cbe486e59522d5a', '15885417470', '15117801779', 0);
INSERT INTO "public"."t_relation" VALUES ('f4eef6312e5a48aebb1fb3d9533a60db', '13965114730', '15117801779', 1);
INSERT INTO "public"."t_relation" VALUES ('hgrskdjvcdsha', '18385640839', '13965114730', 0);
INSERT INTO "public"."t_relation" VALUES ('6e3dbcd7106c4f8eb8ef3af688de48e8', '15885417470', '13965114730', 0);
INSERT INTO "public"."t_relation" VALUES ('8d7dabf9691d4900a1d952c5796f50c5', '15117801779', '13965114730', 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user";
CREATE TABLE "public"."t_user" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "username" varchar(32) COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "icon" varchar(255) COLLATE "pg_catalog"."default",
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "mac_address" varchar(255) COLLATE "pg_catalog"."default",
  "state" int4 DEFAULT 0,
  "watch_token" varchar(32) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."t_user"."state" IS '状态';
COMMENT ON COLUMN "public"."t_user"."watch_token" IS '观看令牌';

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO "public"."t_user" VALUES ('hgn', '15885417470', '123', NULL, '测试', NULL, 0, '112321');
INSERT INTO "public"."t_user" VALUES ('39ac621f77d14c74a4261f5398e5092a', '15117801779', '123', NULL, NULL, NULL, 1, '745754');
INSERT INTO "public"."t_user" VALUES ('jthdrge', '18385471840', '123', NULL, '测试', NULL, 0, '112321');
INSERT INTO "public"."t_user" VALUES ('6681c80c760c4c43a311ef452cdb4be2', '18385640839', '123', NULL, NULL, NULL, 0, '112321');
INSERT INTO "public"."t_user" VALUES ('jrhtgef', '13965114730', '123', NULL, '测试', NULL, 1, '831175');

-- ----------------------------
-- Primary Key structure for table t_relation
-- ----------------------------
ALTER TABLE "public"."t_relation" ADD CONSTRAINT "t_relation_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_user
-- ----------------------------
ALTER TABLE "public"."t_user" ADD CONSTRAINT "t_user_pkey" PRIMARY KEY ("id");
