/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 8.0.29 : Database - fly
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE
DATABASE /*!32312 IF NOT EXISTS*/`fly` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE
`fly`;

/*Table structure for table `order_information` */

DROP TABLE IF EXISTS `order_information`;

CREATE TABLE `order_information`
(
    `order_information_id` int         NOT NULL AUTO_INCREMENT COMMENT '订单信息ID',
    `order_number`         varchar(64)          DEFAULT NULL COMMENT '订单编号',
    `flight_number`        varchar(64)          DEFAULT NULL COMMENT '航班号',
    `place_of_departure`   varchar(64)          DEFAULT NULL COMMENT '出发地',
    `destination`          varchar(64)          DEFAULT NULL COMMENT '目的地',
    `departure_time`       varchar(64)          DEFAULT NULL COMMENT '出发时间',
    `arrival_time`         varchar(64)          DEFAULT NULL COMMENT '到达时间',
    `transit_information`  varchar(64)          DEFAULT NULL COMMENT '中转信息',
    `air_ticket_price`     varchar(64)          DEFAULT NULL COMMENT '机票价格',
    `passengers`           int                  DEFAULT '0' COMMENT '乘机人',
    `full_name`            varchar(64)          DEFAULT NULL COMMENT '姓名',
    `gender`               varchar(64)          DEFAULT NULL COMMENT '性别',
    `phone_number`         varchar(16)          DEFAULT NULL COMMENT '手机号码',
    `id_number`            varchar(64)          DEFAULT NULL COMMENT '身份证号',
    `order_status`         varchar(64)          DEFAULT NULL COMMENT '订单状态',
    `pay_state`            varchar(16) NOT NULL DEFAULT '未支付' COMMENT '支付状态',
    `pay_type`             varchar(16)          DEFAULT '' COMMENT '支付类型: 微信、支付宝、网银',
    `recommend`            int         NOT NULL DEFAULT '0' COMMENT '智能推荐',
    `create_time`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`order_information_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COMMENT='订单信息';

/*Data for the table `order_information` */

insert into `order_information`(`order_information_id`, `order_number`, `flight_number`, `place_of_departure`,
                                `destination`, `departure_time`, `arrival_time`, `transit_information`,
                                `air_ticket_price`, `passengers`, `full_name`, `gender`, `phone_number`, `id_number`,
                                `order_status`, `pay_state`, `pay_type`, `recommend`, `create_time`, `update_time`)
values (1, '订单编号1', '航班号1', '出发地1', '目的地1', '出发时间1', '到达时间1', '中转信息1', '机票价格1', 0, '姓名1',
        '性别1', '18945782351', '身份证号1', '订单状态1', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (2, '订单编号2', '航班号2', '出发地2', '目的地2', '出发时间2', '到达时间2', '中转信息2', '机票价格2', 0, '姓名2',
        '性别2', '18945782352', '身份证号2', '订单状态2', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (3, '订单编号3', '航班号3', '出发地3', '目的地3', '出发时间3', '到达时间3', '中转信息3', '机票价格3', 0, '姓名3',
        '性别3', '18945782353', '身份证号3', '订单状态3', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (4, '订单编号4', '航班号4', '出发地4', '目的地4', '出发时间4', '到达时间4', '中转信息4', '机票价格4', 0, '姓名4',
        '性别4', '18945782354', '身份证号4', '订单状态4', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (5, '订单编号5', '航班号5', '出发地5', '目的地5', '出发时间5', '到达时间5', '中转信息5', '机票价格5', 0, '姓名5',
        '性别5', '18945782355', '身份证号5', '订单状态5', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (6, '订单编号6', '航班号6', '出发地6', '目的地6', '出发时间6', '到达时间6', '中转信息6', '机票价格6', 0, '姓名6',
        '性别6', '18945782356', '身份证号6', '订单状态6', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (7, '订单编号7', '航班号7', '出发地7', '目的地7', '出发时间7', '到达时间7', '中转信息7', '机票价格7', 0, '姓名7',
        '性别7', '18945782357', '身份证号7', '订单状态7', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53'),
       (8, '订单编号8', '航班号8', '出发地8', '目的地8', '出发时间8', '到达时间8', '中转信息8', '机票价格8', 0, '姓名8',
        '性别8', '18945782358', '身份证号8', '订单状态8', '未支付', '支付宝', 0, '2022-03-27 17:53:53',
        '2022-03-27 17:53:53');

/*Table structure for table `slides` */

DROP TABLE IF EXISTS `slides`;

CREATE TABLE `slides`
(
    `slides_id`   int unsigned NOT NULL AUTO_INCREMENT COMMENT '轮播图ID：',
    `title`       varchar(64)        DEFAULT NULL COMMENT '标题：',
    `content`     varchar(255)       DEFAULT NULL COMMENT '内容：',
    `url`         varchar(255)       DEFAULT NULL COMMENT '链接：',
    `img`         varchar(255)       DEFAULT NULL COMMENT '轮播图：',
    `hits`        int unsigned NOT NULL DEFAULT '0' COMMENT '点击量：',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间：',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间：',
    PRIMARY KEY (`slides_id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='轮播图：';

/*Data for the table `slides` */

insert into `slides`(`slides_id`, `title`, `content`, `url`, `img`, `hits`, `create_time`, `update_time`)
values (1, '轮播图1', '内容1', '/article/details?article=1', '/api/upload/image_1646359076003.jpeg', 112,
        '2022-03-27 09:53:53', '2022-03-27 09:53:53'),
       (2, '轮播图2', '内容2', '/article/details?article=2', '/api/upload/image_1646359076001.jpeg', 172,
        '2022-03-27 09:53:53', '2022-03-27 09:53:53'),
       (3, '轮播图3', '内容3', '/article/details?article=3', '/api/upload/image_1646359076010.jpeg', 168,
        '2022-03-27 09:53:53', '2022-03-27 09:53:53');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
