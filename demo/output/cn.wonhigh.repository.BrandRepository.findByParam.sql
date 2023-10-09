SELECT `id`, `brand_no`, `name`, `en_name`, `en_short_name`
	, `opcode`, `category`, `belonger`, `status`, `sys_no`
	, `search_code`, `parent_brand_id`, `logo_url`, `create_user`, `create_time`
	, `update_user`, `update_time`, `remark`, `time_seq`, `organ_type_no`
FROM brand
WHERE `id` = 'fjiv'
	AND `brand_no` = 'uzmk'
	AND `name` LIKE CONCAT('%', 'tejn', '%')
	AND `en_name` LIKE CONCAT('%', 'Mental Health Care', '%')
	AND `en_short_name` LIKE CONCAT('%', 'Computer Games', '%')
	AND `opcode` = 'al202309264124'
	AND `category` = 'olha'
	AND `belonger` = 'fubm'
	AND `status` = 13
	AND `sys_no` = 'rxik'
	AND `search_code` = 'nf202309212345'
	AND `parent_brand_id` = 'ryvk'
	AND `logo_url` = 'jyhe'
	AND `create_user` = '程立果'
	AND `create_time` = 'aomu'
	AND `update_user` = '王风华'
	AND `update_time` = 'bnqb'
	AND `remark` = 'unug'
	AND `time_seq` = 'iace'
	AND `organ_type_no` = 30
LIMIT 1