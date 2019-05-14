package io.jboot.test.seata.commons;

import com.jfinal.plugin.activerecord.IBean;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "seata_account", primaryKey = "ID")
public class Account extends JbootModel<Account> implements IBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
