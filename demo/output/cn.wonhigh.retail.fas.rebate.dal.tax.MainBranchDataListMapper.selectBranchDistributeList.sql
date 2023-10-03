SELECT *
FROM (
	SELECT a.*, ROWNUM AS rn
	FROM (
		WITH branch_company AS (
				SELECT *
				FROM (
					SELECT main_company_no, main_company_name, branch_company_no, branch_company_name, tax_no
						, vat_type, '202307' AS frequency, rank() OVER (PARTITION BY main_company_no, branch_company_no ORDER BY main_company_no, branch_company_no, effective_date DESC) AS rank_no
					FROM main_branch_company_list
						INNER JOIN (
							SELECT c_tcs.company_no AS c_tcs_no, nc_org_code AS c_tcs_nc_org_code, company_type AS c_tcs_company_type
							FROM bus_company_org c_tcs
								INNER JOIN tax_city_config c_tcc
								ON c_tcs.city_no = c_tcc.city_no
									AND c_tcc.tax_name IN (张), 邵
								AND 石
							WHERE 1 = 1
								AND c_tcs.company_zone_no IN (jznd)
								AND c_tcs.city_no IN (jpii)
								AND c_tcs.company_no IN (gfso)
								AND c_tcs.nc_org_code IN (wa202309213782)
								AND c_tcs.company_type = 23
							GROUP BY c_tcs.company_no, c_tcs.nc_org_code, c_tcs.company_type
						) tcct
						ON main_branch_company_list.branch_company_no = tcct.c_tcs_no
					WHERE effective_date <= to_date('20230701', 'yyyy-MM-dd')
						AND branch_company_no IN (xnfs)
						AND main_company_no IN (msgr)
						AND shop_no IN (flsm)
				)
				WHERE rank_no = 1
			), 
			branch_main_company AS (
				SELECT main_company_no, vat_type, frequency
				FROM branch_company
				GROUP BY main_company_no, vat_type, frequency
			), 
			branch_company_config AS (
				SELECT bc.main_company_no, bc.main_company_name, bc.branch_company_no, bc.branch_company_name, tc.city_no
					, tc.tax_name, bc.tax_no, bc.vat_type, bc.frequency
					, MAX(tp.param_value) AS param_value, MAX(tp.param_value2) AS param_value2
				FROM branch_company bc
					INNER JOIN bus_company_org co
					ON bc.branch_company_no = co.company_no
						AND bc.vat_type = co.vat_type
					INNER JOIN tax_city_config tc ON co.city_no = tc.city_no
					LEFT JOIN tax_param_config tp
					ON tc.tax_name = tp.tax_name
						AND tp.param_no = 10
				GROUP BY bc.main_company_no, bc.main_company_name, bc.branch_company_no, bc.branch_company_name, tc.city_no, tc.tax_name, bc.tax_no, bc.vat_type, bc.frequency
			), 
			branch_company_sales AS (
				SELECT br.main_company_no, br.main_company_name, br.branch_company_no, br.branch_company_name, br.tax_no
					, br.vat_type, br.frequency
					, SUM(nvl(br.nc_sales_amount, 0)) AS nc_sales_amount
					, SUM(nvl(br.nc_sales_notax_amount, 0)) AS nc_sales_notax_amount
					, SUM(nvl(br.hands_sales_amount, 0)) AS hands_sales_amount
					, SUM(nvl(br.hands_sales_notax_amount, 0)) AS hands_sales_notax_amount
				FROM main_branch_company_report br
				WHERE br.frequency = 202307
					AND br.report_type = 1
				GROUP BY br.main_company_no, br.main_company_name, br.branch_company_no, br.branch_company_name, br.tax_no, br.vat_type, br.frequency
			), 
			main_vat_check AS (
				SELECT c.company_no AS main_company_no, c.vat_type, c.period AS frequency, SUM(CASE 
						WHEN actual_tax_rate = 5 THEN table_tax_amount
						ELSE 0
					END) AS main_table_tax_amount_5r
					, SUM(CASE 
						WHEN actual_tax_rate = 6 THEN table_tax_amount
						ELSE 0
					END) AS main_table_tax_amount_6r, SUM(CASE 
						WHEN actual_tax_rate = 13 THEN table_tax_amount
						ELSE 0
					END) AS main_table_tax_amount_13r
					, SUM(CASE 
						WHEN actual_tax_rate = 5 THEN table_not_tax_amount
						ELSE 0
					END) AS main_table_not_tax_amount_5r, SUM(CASE 
						WHEN actual_tax_rate = 6 THEN table_not_tax_amount
						ELSE 0
					END) AS main_table_not_tax_amount_6r
					, SUM(nvl(table_not_tax_amount, 0)) AS main_table_not_tax_amount
				FROM taxpayer_vat_check c
					INNER JOIN branch_main_company m
					ON c.company_no = m.main_company_no
						AND c.vat_type = m.vat_type
						AND c.period = m.frequency
				WHERE 1 = 1
					AND period = 202307
					AND save_type = '1'
				GROUP BY c.company_no, c.vat_type, c.period
			), 
			main_vat_file AS (
				SELECT f.company_no AS main_company_no, f.vat_type, f.period AS frequency
					, SUM(nvl(should_tax_amount, 0)) AS main_should_tax_amount
				FROM taxpayer_vat_accure f
					INNER JOIN branch_main_company m
					ON f.company_no = m.main_company_no
						AND f.vat_type = m.vat_type
						AND f.period = m.frequency
				WHERE 1 = 1
					AND '202307' BETWEEN f.start_period AND f.end_period
				GROUP BY f.company_no, f.vat_type, f.period
			)
		SELECT bc.*, br.nc_sales_amount, br.nc_sales_notax_amount, br.hands_sales_amount, br.hands_sales_notax_amount
			, mc.main_table_tax_amount_5r, mc.main_table_tax_amount_6r, mc.main_table_tax_amount_13r, mc.main_table_not_tax_amount_5r, mc.main_table_not_tax_amount_6r
			, mc.main_table_not_tax_amount, mf.main_should_tax_amount
		FROM branch_company_config bc
			LEFT JOIN branch_company_sales br
			ON bc.main_company_no = br.main_company_no
				AND bc.branch_company_no = br.branch_company_no
				AND bc.vat_type = br.vat_type
				AND bc.frequency = br.frequency
			LEFT JOIN main_vat_check mc
			ON mc.main_company_no = bc.main_company_no
				AND mc.vat_type = bc.vat_type
				AND mc.frequency = bc.frequency
			LEFT JOIN main_vat_file mf
			ON mf.main_company_no = bc.main_company_no
				AND mf.vat_type = bc.vat_type
				AND mf.frequency = bc.frequency
		ORDER BY bc.branch_company_no, bc.frequency
	) a
	WHERE ROWNUM <= 0 + 20
) b
WHERE b.rn > 0