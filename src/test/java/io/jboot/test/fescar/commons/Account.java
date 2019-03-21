package io.jboot.test.fescar.commons;

import com.jfinal.plugin.activerecord.IBean;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "fescar_account", primaryKey = "ID")
public class Account extends JbootModel<Account> implements IBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
