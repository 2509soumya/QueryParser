package com.self.queryparsers;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloJava {

	
	public static void main(String[] args) {
		String query="CREATE VIEW [ww_fin_fcp_rpt_secure].[fcp_hfm_adjs_v] AS select a.[GEN_LDGR_MTHLY_BAL_KEY], substring(cast(fiscal_period_key as char(8)),1,4) as FISCAL_YR_NBR, 'Per' + substring(cast(fiscal_period_key as char(8)),5,2) as FISCAL_PERIOD_NBR, case when sm.sledg_scenario is null then b.SCENARIO else sm.sledg_scenario end as SCENARIO, c.VER, d.TRANS_TYPE, case when em.sledg_enty is null then h.rpt_cntry else em.sledg_enty end as LOC, 'Not Available' as LOC_STATUS, 'Not Available' as DEPT, 'Periodic' as [VIEW], case when isnumeric(substring(f.ACCT_NBR,1,1))=1 then 'A' + f.ACCT_NBR else f.ACCT_NBR end as ACCT, 'Not Available' as CNTR_TYPE, 'Not Available' as CHNL, 'USD' as CRNCY_CD, h.RPT_CNTRY, USD_AMT, a.load_ts, a.load_userid, a.upd_ts, a.upd_userid, eh.lvl2 as segment FROM (select * from [ww_fin_fcp_secure].[FCP_GEN_LDGR_MTHLY_BAL] where src_id=1) a left outer join [ww_fin_fcp_tables].[FCP_SCENARIO_DIM] b on a.scenario_key=b.scenario_key left outer join [ww_fin_fcp_tables].[FCP_VER_DIM] c on a.ver_key=c.ver_key left outer join [ww_fin_fcp_tables].[FCP_TRANS_TYPE_DIM] d on a.trans_type_key=d.trans_type_key left outer join [ww_fin_fcp_tables].[FCP_LOC_DIM] e on a.loc_key=e.loc_key left outer join [ww_fin_fcp_tables].[FCP_GEN_LDGR_ACCT_DIM] f on a.gen_ldgr_acct_key=f.gen_ldgr_acct_key left outer join [ww_fin_fcp_tables].[FCP_SRC_ID_DIM] g on a.src_id=g.src_id left outer join [ww_fin_fcp_tables].[FCP_RPT_CNTRY_DIM] h on a.rpt_cntry_key=h.rpt_cntry_key left outer join ( select 'Intl.Intl_Adj' as rpt_cntry_org, 'Intl_Adj' as rpt_cntry_new union select 'Moosejaw_Total.A285W' as rpt_cntry_org, 'A285W' as rpt_cntry_new union select 'Moosejaw_Total.Moosejaw_Total_Adj' as rpt_cntry_org, 'Moosejaw_Total_Adj' as rpt_cntry_new union select 'Sams.Sams_Seg' as rpt_cntry_org, 'Sams_Seg_Adj' as rpt_cntry_new union select 'Total_Walmex_USD.Walmex' as rpt_cntry_org, 'Walmex_Adj' as rpt_cntry_new union select 'WMT_US.WMT_US_Seg' as rpt_cntry_org, 'Wmt_US_Adj' as rpt_cntry_new union select 'WMT_US_Seg.Moosejaw_Total' as rpt_cntry_org, 'Moosejaw_Total_Adj' as rpt_cntry_new ) dq on h.rpt_cntry=dq.rpt_cntry_org left outer join [ww_fin_fcp_rpt_tables].[fcp_drm_entity_hrchy_flatten_v] eh on case when dq.rpt_cntry_new is not null then dq.rpt_cntry_new else h.rpt_cntry end = eh.entity left outer join [ww_fin_fcp_tables].[FCP_HFM_SLEDG_ENTY_MAP] em on h.rpt_cntry=em.rpt_cntry and d.trans_type=em.trans_type left outer join [ww_fin_fcp_tables].[FCP_HFM_SLEDG_SCENARIO_MAP] sm on d.trans_type=sm.trans_type where a.src_id=1 and h.rpt_cntry is not null and f.acct_nbr is not null and ((USD_AMT IS NOT NULL AND USD_AMT <> 0) OR (LCL_AMT is not null and LCL_AMT <> 0));";
		Pattern equality_pattern = Pattern.compile("([(]|[\\s]+)([a-zA-z]+[.])?(src_id)(=)[0-9]+([\\s]+|$|;)", Pattern.CASE_INSENSITIVE);  
		Matcher m = equality_pattern.matcher(query); 
		Set<String> sourceidset=new HashSet<String>();
		

		
		while(m.find()) {
			System.out.println(m.group().trim());
			String trimmedmatchstring=m.group().trim();
			if(trimmedmatchstring.contains(";")) {
				trimmedmatchstring=trimmedmatchstring.replaceAll(";", "");
			}
			String sourceid=trimmedmatchstring.split("=")[1];
			sourceidset.add(sourceid.trim());
		}
		
		
	    
		Pattern multiequality_pattern=Pattern.compile("([(]|[\\s]+)([a-zA-z]+[.])?(src_id)[\\s]+(in)[\\s]+[(].*[)]([\\s]+|$|;)", Pattern.CASE_INSENSITIVE);
		Matcher mic = multiequality_pattern.matcher(query); 
		while(mic.find()) {
			System.out.println(mic.group().trim());
			String trimmedmatchstring=mic.group().trim();
			if(trimmedmatchstring.contains(";")) {
				trimmedmatchstring=trimmedmatchstring.replaceAll(";", "");
			}
			String sourceidlist=trimmedmatchstring.split("[\\s]+(?i)(in)[\\s]+")[1];
			for( String sourceid : sourceidlist.trim().replaceAll("[(]", "").replaceAll("[)]", "").split(",")) {
				sourceidset.add(sourceid.trim());
			}
		}
		
		System.out.println(sourceidset.toString());
		
		
	}
}
