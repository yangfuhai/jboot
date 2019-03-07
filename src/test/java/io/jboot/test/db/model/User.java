package io.jboot.test.db.model;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "user",primaryKey = "id")
public class User extends JbootModel {
}
