package com.spread.persistence;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class SpreadMySQLDialect extends MySQLDialect {
	
	public SpreadMySQLDialect() {
		
		super();
		
		
		registerFunction("regexp", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 REGEXP ?2"));
	}

}
