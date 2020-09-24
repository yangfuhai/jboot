# JbootAdmin 数据库设计

JbootAdmin 目前的表结构如下：


**账户信息表** 用来存储每个账户的基本信息、账号密码
```sql
CREATE TABLE `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT '租户用户ID，租户ID是自己的时候，自己是租户的管理员',
  `superior_tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT '上级租户ID',
  `loginname` varchar(128) DEFAULT NULL COMMENT '登录名',
  `nickname` varchar(128) DEFAULT NULL COMMENT '昵称',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `salt` varchar(32) DEFAULT NULL COMMENT '盐',
  `email` varchar(64) DEFAULT NULL COMMENT '邮件',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机电话',
  `domain` varchar(32) DEFAULT NULL COMMENT 'SaaS系统给用户分配的域名',
  `avatar` varchar(256) DEFAULT NULL COMMENT '账户头像',
  `type` tinyint(2) DEFAULT NULL COMMENT '账户类型',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `activated` datetime DEFAULT NULL COMMENT '激活时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `loginname` (`loginname`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  KEY `created` (`created`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE,
  KEY `superior_tenant_id` (`superior_tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账号信息表，保存用户账号信息。';
```

**账户地址** 用户的收货地址表
```sql
CREATE TABLE `account_address` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户 id',
  `username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `province` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `city` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `district` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区（县）',
  `detail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址到门牌号',
  `zipcode` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮政编码',
  `default_enable` tinyint(1) DEFAULT '0' COMMENT '是否默认,1是，0否',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT '扩展字段',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `created` datetime DEFAULT NULL COMMENT '新增时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='收货地址';
```

**用户属性表** 用来保持用户的扩展属性
```sql
CREATE TABLE `account_attr` (
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户ID',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '属性名称',
  `content` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '属性内容',
  PRIMARY KEY (`account_id`,`name`) USING BTREE,
  KEY `content` (`content`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='账户属性表';
```

**账户部门关系表** 账户和部门的 多对多 映射关系
```sql
CREATE TABLE `account_dept` (
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '账户ID',
  `dept_id` bigint(20) unsigned NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`dept_id`,`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='部门和账户的多对多关系表';
```

**账户第三方关系表**
```sql
CREATE TABLE `account_openid` (
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '第三方类型：wechat，dingding，qq...',
  `openid` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方的openId的值',
  `access_token` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可能用不到',
  `expired_time` datetime DEFAULT NULL COMMENT 'access_token的过期时间',
  `nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT '其他扩展属性',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`type`,`account_id`) USING BTREE,
  KEY `type_openid` (`type`,`openid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='账号第三方账号绑定表';
```

```sql
CREATE TABLE `account_option` (
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '账户ID',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '扩展属性名称',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '扩展属性内容',
  PRIMARY KEY (`account_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='账户配置';
```

```sql
CREATE TABLE `account_permission` (
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户ID',
  `permission_id` varchar(128) NOT NULL DEFAULT '' COMMENT '权限ID',
  `own` tinyint(1) DEFAULT NULL COMMENT '1 拥有， 0 排除',
  PRIMARY KEY (`account_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账户和权限的多对多映射表，这个很少用到，只有在极特殊情况下，可以通过这个对某些权限进行添加或者排除';
```

```sql
CREATE TABLE `account_receive_msg` (
  `account_id` bigint(20) unsigned DEFAULT NULL COMMENT '账户id',
  `send_msg_type` tinyint(2) unsigned DEFAULT NULL COMMENT '可接收消息类型',
  KEY `account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知功能';
```

```sql
CREATE TABLE `account_role` (
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户ID',
  `role_id` bigint(20) unsigned NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`account_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账户和角色的多对多映射表';
```

```sql
CREATE TABLE `account_scope` (
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户ID',
  `scope_account_id` bigint(20) unsigned NOT NULL COMMENT '可以操作其他账户数据的账户ID',
  PRIMARY KEY (`account_id`,`scope_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='账户的操作范围（只可以操作哪些用户的数据）';
```

```sql
CREATE TABLE `account_session` (
  `id` varchar(32) NOT NULL DEFAULT '' COMMENT 'session id',
  `client` varchar(32) NOT NULL DEFAULT '' COMMENT '客户端',
  `platform` varchar(32) DEFAULT NULL COMMENT '登录平台',
  `account_id` bigint(20) unsigned NOT NULL COMMENT '账户id',
  `expire_at` datetime NOT NULL COMMENT '到期时间',
  `options` text COMMENT '扩展属性',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='账户登录session';
```

```sql
CREATE TABLE `account_station` (
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '账户ID',
  `station_id` bigint(20) unsigned NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`station_id`,`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='岗位和账户的多对多关系';
```

```sql
CREATE TABLE `dev_project` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
  `module_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块名称',
  `old_module_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '旧模块名称，用于在模块名称更新的时候，需要记录之前的模块名称',
  `package_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块报名',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '模块描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='项目表，对应的是 idea（或者eclipse）的代码模块';
```

```sql
CREATE TABLE `dev_tablefield` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字段名称',
  `table_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段所在的表名',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段的标题',
  `render_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渲染类型',
  `valid_required` tinyint(1) DEFAULT NULL COMMENT '是否必填',
  `valid_mobile` tinyint(1) DEFAULT NULL COMMENT '是否是手机',
  `valid_email` tinyint(1) DEFAULT NULL COMMENT '是否是邮件',
  `show_in_list` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在列表里显示',
  `show_in_edit` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在编辑页面编辑',
  `search_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '搜索类型  like、相等',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `table_name` (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='表字段配置';
```

```sql
CREATE TABLE `dev_tableinfo` (
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表名',
  `project_id` int(11) DEFAULT NULL COMMENT '所在项目',
  `controller_mapping` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Controller 的 url 映射',
  `title` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `alias` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '别名',
  `help_text` text COLLATE utf8mb4_unicode_ci COMMENT '帮助内容',
  `crumbs_text` text COLLATE utf8mb4_unicode_ci COMMENT '面包屑内容',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述或备注',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='表生成内容配置';
```

```sql
CREATE TABLE `log_action` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户ID',
  `tenant_id` bigint(20) unsigned DEFAULT NULL,
  `action` varchar(512) DEFAULT NULL COMMENT '访问路径',
  `name` varchar(128) DEFAULT NULL,
  `query` varchar(512) DEFAULT NULL COMMENT '访问参数',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP',
  `ua` varchar(1024) DEFAULT NULL COMMENT '浏览器',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户行为日志';
```

```sql
CREATE TABLE `log_login` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) unsigned NOT NULL COMMENT '登录账户',
  `tenant_id` bigint(20) unsigned DEFAULT NULL,
  `client` varchar(100) DEFAULT NULL COMMENT '登录客户端',
  `ip` varchar(100) DEFAULT NULL COMMENT '登录的I IP 地址',
  `ua` varchar(512) DEFAULT NULL COMMENT '登录的浏览器 ua',
  `platform` varchar(32) DEFAULT NULL COMMENT '登录平台',
  `created` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户登录日志';
```

```sql
CREATE TABLE `sys_cnarea` (
  `id` mediumint(7) unsigned NOT NULL AUTO_INCREMENT,
  `level` tinyint(2) unsigned NOT NULL COMMENT '层级',
  `parent_code` bigint(14) unsigned NOT NULL DEFAULT '0' COMMENT '父级行政代码',
  `area_code` bigint(14) unsigned NOT NULL DEFAULT '0' COMMENT '行政代码',
  `zip_code` mediumint(6) unsigned zerofill NOT NULL DEFAULT '000000' COMMENT '邮政编码',
  `city_code` char(6) NOT NULL DEFAULT '' COMMENT '区号',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `short_name` varchar(50) NOT NULL DEFAULT '' COMMENT '简称',
  `merger_name` varchar(50) NOT NULL DEFAULT '' COMMENT '组合名',
  `pinyin` varchar(30) NOT NULL DEFAULT '' COMMENT '拼音',
  `lng` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '经度',
  `lat` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '纬度',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`area_code`) USING BTREE,
  KEY `idx_parent_code` (`parent_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='中国行政地区表';
```

```sql
CREATE TABLE `sys_dept` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `type` tinyint(2) DEFAULT NULL COMMENT '部门类型',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '上级部门id',
  `code` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '部门编码',
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '部门名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '部门描述',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序编号',
  `tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='部门表';
```

```sql
CREATE TABLE `sys_dept_account` (
  `dept_id` bigint(20) unsigned NOT NULL COMMENT '部门ID',
  `account_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '账户ID',
  PRIMARY KEY (`dept_id`,`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='部门和账户的多对多关系表';
```

```sql
CREATE TABLE `sys_menu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `menu_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '菜单对应html的id属性的值',
  `menu_pid` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通过 PID 来设置上下级关系',
  `text` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单内容',
  `icon` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单ICON',
  `url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单URL地址',
  `target` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单的打开类型',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单类型',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单所在的平台，一个系统下可能存在多个平台',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序值',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT '其他扩展字段',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `platform` (`platform`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统菜单';
```

```sql
CREATE TABLE `sys_message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息标题',
  `level` tinyint(2) NOT NULL COMMENT '内容级别（1普通 2一般 3紧急 4弹窗）',
  `type` tinyint(2) DEFAULT NULL COMMENT '内容类型（1系统  2业务  3公告  4其它）',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `receive_type` tinyint(2) NOT NULL COMMENT '接受者类型（0全部 1用户 2部门 3角色 4岗位）',
  `send_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '发送者用户编码',
  `send_account_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者用户姓名',
  `send_date` datetime DEFAULT NULL COMMENT '发送时间',
  `notify_type` tinyint(2) DEFAULT NULL COMMENT '通知类型（PC APP 短信 邮件 微信）多选',
  `status` tinyint(2) NOT NULL COMMENT '状态（0正常 1删除 4审核 5驳回 9草稿）',
  `reply_status` tinyint(2) DEFAULT NULL COMMENT '是否支持回复等',
  `reply_id` bigint(20) unsigned DEFAULT NULL COMMENT '回复的消息ID',
  `created_by` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified_by` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `modified` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `send_account_id` (`send_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统消息';
```

```sql
CREATE TABLE `sys_message_send_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` bigint(20) unsigned NOT NULL COMMENT '所属消息',
  `send_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '发送消息用户',
  `receive_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '接受者用户',
  `read_status` tinyint(2) NOT NULL COMMENT '读取状态（0未送达 1已读 2未读）',
  `read_date` datetime DEFAULT NULL COMMENT '阅读时间',
  `reply_status` tinyint(2) DEFAULT NULL COMMENT '回复状态',
  `reply_date` datetime DEFAULT NULL COMMENT '回复时间',
  `is_star` tinyint(1) DEFAULT NULL COMMENT '是否标星',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `send_account_id` (`send_account_id`) USING BTREE,
  KEY `receive_account_id` (`receive_account_id`) USING BTREE,
  KEY `read_status` (`read_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='消息发送记录表';
```

```sql
CREATE TABLE `sys_option` (
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '配置key',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '配置内容',
  PRIMARY KEY (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统配置表';
```

```sql
CREATE TABLE `sys_permission` (
  `id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '权限ID',
  `group_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属的权限组ID',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限名称',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限描述',
  `type` tinyint(2) DEFAULT NULL COMMENT '权限类型，1 菜单，2 Action，3 逻辑权限， 4 敏感数据权限， 5 页面元素，6 其他类型',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_no` int(11) DEFAULT NULL COMMENT '权限排序，只用于展示',
  `created` datetime DEFAULT NULL COMMENT '创建修改',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE,
  KEY `platform` (`platform`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='权限表';
```

```sql
CREATE TABLE `sys_permission_group` (
  `id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '权限组ID',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限组名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '权限组描述',
  `sort_no` int(11) DEFAULT NULL COMMENT '权限组排序',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限组所在的平台',
  `type` tinyint(2) DEFAULT NULL COMMENT '权限组类型，1 菜单，2 Action，3 逻辑权限， 4 敏感数据权限， 5 页面元素，6 其他类型',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `platform` (`platform`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='对权限进行分组，只用于显示的作用，不起逻辑控制作用';
```

```sql
CREATE TABLE `sys_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '父级 id',
  `tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT '住户 ID',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '角色名称',
  `desc` text COMMENT '角色的描述',
  `flag` varchar(64) DEFAULT '' COMMENT '角色标识，全局唯一，sa 为超级管理员',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序编码',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色表';
```

```sql
CREATE TABLE `sys_role_permission` (
  `role_id` bigint(20) unsigned NOT NULL COMMENT '角色ID',
  `permission_id` varchar(128) NOT NULL DEFAULT '' COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色和权限的多对多映射表';
```

```sql
CREATE TABLE `sys_station` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '岗位名称',
  `type` tinyint(2) DEFAULT NULL COMMENT '岗位分类（高管、中层、基层）',
  `sort_no` int(10) DEFAULT NULL COMMENT '岗位排序（升序）',
  `status` tinyint(2) DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modifiy_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `modified` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='岗位表';
```

```sql
CREATE TABLE `wechat_account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '公众号类型',
  `tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT '租户ID',
  `app_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号的 APP ID',
  `app_secret` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号的 APP Secret',
  `app_token` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号配置的 token',
  `encoding_aes_key` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Aes 加密 key',
  `message_encrypt` tinyint(1) DEFAULT NULL COMMENT '是否进行加密',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '公众号描述',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序编号',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `app_id` (`app_id`) USING BTREE,
  KEY `tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='微信公众号表';
```

```sql
CREATE TABLE `wechat_account_keyword` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `wechat_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '归属公众号',
  `wechat_account_appid` varchar(64) DEFAULT NULL,
  `keyword` varchar(128) DEFAULT NULL COMMENT '关键字',
  `reply_content_type` tinyint(2) DEFAULT NULL COMMENT '回复类型',
  `reply_content` text COMMENT '回复内容，保存内容是一个 json',
  `options` text,
  `status` tinyint(2) DEFAULT NULL COMMENT '是否启用',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ak` (`wechat_account_appid`,`keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户自定义关键字回复表';
```

```sql
CREATE TABLE `wechat_account_menu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '父级ID',
  `wechat_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '归属公众号',
  `text` varchar(512) DEFAULT NULL COMMENT '文本内容',
  `keyword` varchar(128) DEFAULT NULL COMMENT '关键字',
  `type` varchar(32) DEFAULT '' COMMENT '菜单类型',
  `sort_no` int(11) DEFAULT '0' COMMENT '排序字段',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `wechat_account_id` (`wechat_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='微信公众号菜单表';
```

```sql
CREATE TABLE `wechat_account_msg` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `wechat_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '微信公众号ID',
  `wechat_account_appid` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信公众号的 AppID',
  `user_open_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信消息发送用户',
  `user_nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送消息的用户昵称',
  `user_avatar` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送消息的用户头像',
  `content_type` tinyint(2) DEFAULT NULL COMMENT '消息类型',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
  `created` datetime DEFAULT NULL COMMENT '消息的接收时间',
  `is_replied` tinyint(1) DEFAULT NULL COMMENT '是否已经回复',
  `reply_date` datetime DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `wechat_account_id` (`wechat_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='微信公众号消息表';
```

```sql
CREATE TABLE `wechat_account_msg_reply` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `wechat_account_id` bigint(20) unsigned DEFAULT NULL COMMENT '回复的微信公众号ID',
  `reply_msg_id` bigint(20) unsigned DEFAULT NULL COMMENT '回复的消息ID',
  `reply_to_open_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回复的用户ID',
  `reply_content_type` tinyint(2) DEFAULT NULL COMMENT '回复的消息类型',
  `reply_content` text COLLATE utf8mb4_unicode_ci COMMENT '回复内容',
  `created` datetime DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `wechat_account_id` (`wechat_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='微信公众号消息回复表';
```

```sql
CREATE TABLE `wechat_account_option` (
  `wechat_account_appid` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信公众号配置key',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '微信公众号配置Neri',
  PRIMARY KEY (`wechat_account_appid`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='微信公众号配置';

```