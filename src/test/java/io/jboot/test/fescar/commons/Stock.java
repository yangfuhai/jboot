package io.jboot.test.fescar.commons;

import com.jfinal.plugin.activerecord.IBean;

import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;

@Table(tableName = "fescar_stock", primaryKey = "ID")
public class Stock extends JbootModel<Stock> implements IBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
