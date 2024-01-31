SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_book
-- ----------------------------
DROP TABLE IF EXISTS tb_book;
CREATE TABLE tb_book (
                         bId int(11) NOT NULL auto_increment,
                         bBookName varchar(40) NOT NULL,
                         bAuthor varchar(20) NOT NULL,
                         bSex varchar(10) NOT NULL,
                         bPrice float NOT NULL,
                         bBookDescription varchar(1000) NOT NULL,
                         btId int(12) NOT NULL,
                         PRIMARY KEY (bId)
);

-- ----------------------------
-- Records of tb_book
-- ----------------------------
INSERT INTO tb_book VALUES ('2', '书名', '作者名字', '男', '33', '描述', '1');
INSERT INTO tb_book VALUES ('5', '西游记', '吴承恩', '男', '50', '西游记是一部神魔小说。', '2');
INSERT INTO tb_book VALUES ('9', '三体', '刘慈欣', '男', '100', '科幻巨著。', '7');
INSERT INTO tb_book VALUES ('10', '醉花阴', '李清照', '女', '5', '词。', '2');

-- ----------------------------
-- Table structure for tb_booktype
-- ----------------------------
DROP TABLE IF EXISTS tb_booktype;
CREATE TABLE tb_booktype (
                             btId int(12) NOT NULL AUTO_INCREMENT,
                             btName varchar(40) NOT NULL,
                             btDescription varchar(1000) NOT NULL,
                             PRIMARY KEY (btId)
);

-- ----------------------------
-- Records of tb_booktype
-- ----------------------------
INSERT INTO tb_booktype VALUES ('2', '文学', '这些书都是与文学相关的书籍');
INSERT INTO tb_booktype VALUES ('5', '金瓶梅', '高数是一棵神奇的树。			');
INSERT INTO tb_booktype VALUES ('6', '外语', '学会一门外语是很有用处的。');
INSERT INTO tb_booktype VALUES ('7', '科幻', '对未来的期望。');
INSERT INTO tb_booktype VALUES ('8', '动漫', '海贼王、火影等。一人之下也是动漫。');
INSERT INTO tb_booktype VALUES ('9', '生活', '一些生活技能知识的书籍。');
