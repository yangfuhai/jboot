package io.jboot.test.seata.commons;

import com.jfinal.plugin.activerecord.IBean;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "seata_stock", primaryKey = "ID")
public class Stock extends JbootModel<Stock> implements IBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
