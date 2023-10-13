package cn.mercury.xcode.mybatis.extend.dataaccess;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 表关联方式处理数据权限
 */
public class JoinTypeFieldDataAccessStatementProcessor extends FieldDataAccessStatementProcessor {

	final static Logger logger = LoggerFactory.getLogger(JoinTypeFieldDataAccessStatementProcessor.class);
	final FieldDataAccessStatementProcessor fieldDataAccessStatementProcessor = new DefaultFieldDataAccessStatementProcessor();
	int maxSize = 3;

	private String retailUCDatabaseName = "retail_uc";

	final String statementExpress;


	public JoinTypeFieldDataAccessStatementProcessor() {
		statementExpress =  "%s%s in (select %s from " + retailUCDatabaseName + ".%s where user_id=%d )";
	}

	Integer zoneSize = 10;

	private Integer getZoneSize() {
		 return zoneSize;
	}

	@Override
	public String process(String alias, String column, String name, boolean shopMode) {
		// 过滤店铺权限
		if (shopMode && DataAccessEnum.SHOP.equalsIgnoreCase(name)) {
			String val = processFilterStatement("", "order_unit_no", DataAccessEnum.ORDERUNIT);
			if (val == null)
				return null;//
			  return alias + column + String.format(shopFlag, val);
		} else {
			return processFilterStatement(alias, column, name);
		}
	}

	protected String processFilterStatement(String alias, String column, String name) {
		List<String> vals = getProvider().getAccessData(name);

		if (vals == null)
			return null;

		String script = "";
		if (vals.size() >= maxSize) {
			script = getFilterStatement(alias, column, name);
		}

		if ("".equals(script)) {
			logger.info("load access data with default mode.");
			script = this.fieldDataAccessStatementProcessor.processFilterStatement(alias, column, name);
		}

		return script;
	}



	private String getOrganFilterStatement(String alias, String column, String dataType) {
		Integer zoneSize = getZoneSize();
		if (zoneSize != null && zoneSize > 0) {
			List<String> zones = getProvider().getAccessData(DataAccessEnum.ZONE);
			if (zones != null && zones.size() == zoneSize) { // 所有大区
				return null;
			}
		}

		String zoneStatement = processFilterStatement("t.", "zone_no", DataAccessEnum.ZONE);
		StringBuffer sb = new StringBuffer();
		sb.append(alias).append(column).append(" In ( Select t.organ_no From organ t Where  t.organ_type_no = '")
				.append("U0102").append("' And ").append(zoneStatement);

		if (dataType.equalsIgnoreCase(DataAccessEnum.BUSINESS_CITY)) {
			sb.append(" And  t.organ_level = 2 ");
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.BUSINESS_CITY)) {
			sb.append(" And  t.organ_level = 1 ");
		}

		return sb.append(")").toString();
	}
	
	private String getNormalUserFilterStatement(int userId, String alias, String column) {		
		StringBuffer sb = new StringBuffer();
		sb.append(alias).append(column).append(" In ( ")
		.append("Select a.business_city_no From retail_uc.authority_user_business_city a Where a.user_id = " )
		.append(userId)
		.append( " Union All Select organ_no as _no From organ o ")		
		.append(" Inner Join ")
		.append(retailUCDatabaseName)
		.append(".authority_user_managing_city b On o.parent_no = b.managing_city_no Where b.user_id = " )
		.append(userId);
		return sb.append(")").toString();
	}
	

	private String getFilterStatement(String alias, String column, String dataType) {
		int userId =  10;
		int ogranLevel = 2;// 大区用户

		String field;
		String table;
		if (dataType.equalsIgnoreCase(DataAccessEnum.BRAND)) {
			field = "brand_detail_no";
			table = "authority_user_brand";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.ORDERUNIT)) {
			field = "order_unit_no";
			table = "authority_user_order_unit";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.STORE)) {
			field = "storage_no";
			table = "authority_user_storage";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.SHOP)) {
			field = "store_no";
			table = "authority_user_store";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.BUSINESSCITY)) {
			if (ogranLevel<4) {
				return getOrganFilterStatement(alias, column, dataType);
			}
			else if( ogranLevel >= 4 ) {
				return getNormalUserFilterStatement(userId,alias, column);
			}
			field = "business_city_no";
			table = "authority_user_business_city";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.MANAGERCITY)) {
			if (ogranLevel<4) {
				return getOrganFilterStatement(alias, column, dataType);
			}
			field = "managing_city_no";
			table = "authority_user_managing_city";
		} else if (dataType.equalsIgnoreCase(DataAccessEnum.ORGAN)) {
			if (ogranLevel<4) 
				return getOrganFilterStatement(alias, column, dataType);
			
			String first = getFilterStatement(alias, column, DataAccessEnum.BUSINESSCITY);
			String last = getFilterStatement(alias, column, DataAccessEnum.MANAGERCITY);
			return String.format("( (%s) OR (%s) )", first, last);
		} else {
			logger.info("skip unsported data type:" + dataType);
			return "";
		}
		
		return String.format(statementExpress, alias, column, field, table, userId);
	}


}
